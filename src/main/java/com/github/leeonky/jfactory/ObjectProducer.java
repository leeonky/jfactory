package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

class ObjectProducer<T> extends Producer<T> {
    private final ObjectFactory<T> objectFactory;
    private final FactorySet factorySet;
    private final DefaultBuilder<T> builder;
    private final boolean intently;
    private final Instance<T> instance;
    private final Map<String, Producer<?>> children = new HashMap<>();
    private final Map<PropertyChain, Dependency<?>> dependencies = new LinkedHashMap<>();
    private final List<Link> links = new ArrayList<>();

    public ObjectProducer(FactorySet factorySet, ObjectFactory<T> objectFactory, DefaultBuilder<T> builder, boolean intently) {
        super(objectFactory.getType());
        this.objectFactory = objectFactory;
        this.factorySet = factorySet;
        this.builder = builder;
        this.intently = intently;
        instance = objectFactory.createInstance(factorySet.getTypeSequence());
        establishProducers(factorySet, builder);
    }

    private void establishProducers(FactorySet factorySet, DefaultBuilder<T> builder) {
        buildPropertyValueProducers(factorySet.getObjectFactorySet());
        buildProducersFromSpec(builder);
        buildProducerFromInputProperties(factorySet, builder);
    }

    private void buildProducerFromInputProperties(FactorySet factorySet, DefaultBuilder<T> builder) {
        builder.toExpressions().forEach((p, exp) -> addChild(p, exp.buildProducer(factorySet, this, instance.sub(p))));
    }

    private void buildProducersFromSpec(DefaultBuilder<T> builder) {
        builder.collectSpec(instance);
        instance.spec().apply(factorySet, this);
    }

    private void buildPropertyValueProducers(ObjectFactorySet objectFactorySet) {
        getType().getPropertyWriters().forEach((name, propertyWriter) ->
                objectFactorySet.queryPropertyValueFactory(propertyWriter.getType()).ifPresent(propertyValueFactory ->
                        addChild(name, new PropertyValueProducer<>(getType(), propertyValueFactory, instance.sub(name)))));
    }

    @Override
    public void addChild(String property, Producer<?> producer) {
        children.put(property, producer);
    }

    @Override
    public Producer<?> getChildOrDefault(String property) {
        Producer<?> producer = children.get(property);
        if (producer == null) {
            BeanClass<?> propertyType = getType().getPropertyWriter(property).getType();
            if (propertyType.isCollection())
                addChild(property, producer = new CollectionProducer<>(
                        factorySet.getObjectFactorySet(), getType(), propertyType, instance.sub(property)));
        }
        return producer;
    }

    @Override
    protected T produce() {
        return instance.getValueCache().cache(() -> objectFactory.create(instance), obj -> {
            children.forEach((property, producer) -> getType().setPropertyValue(obj, property, producer.getValue()));
            factorySet.getDataRepository().save(obj);
        });
    }

    @Override
    public Optional<Producer<?>> getChild(String property) {
        return Optional.ofNullable(children.get(property));
    }

    public void addDependency(PropertyChain property, Function<Object[], Object> function, List<PropertyChain> propertyChains) {
        dependencies.put(property, new Dependency<>(function, property, propertyChains));
    }

    public ObjectProducer<T> processDependencyAndLink() {
        processDependencies();
        getAllChildren().values().forEach(Producer::checkChange);
        processLinks();
        uniqSameSubObjectProducer();
        return this;
    }

    private void uniqSameSubObjectProducer() {
        getAllChildren().entrySet().stream()
                .filter(e -> e.getValue() instanceof ObjectProducer)
                .collect(Collectors.groupingBy(Map.Entry::getValue))
                .forEach((_ignore, properties) -> link(properties.stream().map(Map.Entry::getKey).collect(Collectors.toList())));
        processLinks();
    }

    private void processLinks() {
        links.forEach(link -> link.process(this));
        links.clear();
    }

    @Override
    protected void processDependencies() {
        children.values().forEach(Producer::processDependencies);
        dependencies.values().forEach(dependency -> dependency.process(this));
        beforeCheckChange();
    }

    @Override
    public int hashCode() {
        return Objects.hash(ObjectProducer.class, objectFactory, builder.hashCode(), uniqHashWhenChange(), uniqHashWhenIntently());
    }

    private Object uniqHashWhenIntently() {
        return intently ? new Object() : false;
    }

    private Object uniqHashWhenChange() {
        return isNotChange() ? true : new Object();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ObjectProducer) {
            ObjectProducer another = (ObjectProducer) obj;
            return objectFactory.equals(another.objectFactory) && builder.equals(another.builder)
                    && isNotChange() && another.isNotChange() && !intently && !another.intently;
        }
        return super.equals(obj);
    }

    public void link(List<PropertyChain> properties) {
        if (properties.size() > 1) {
            links.stream().filter(link -> link.contains(properties))
                    .findFirst().orElseGet(() -> {
                Link link = new Link();
                links.add(link);
                return link;
            }).merge(properties);
        }
    }

    @Override
    public Map<PropertyChain, Producer<?>> getChildren() {
        return children.entrySet().stream()
                .collect(Collectors.toMap(e -> PropertyChain.createChain(e.getKey()), Map.Entry::getValue));
    }
}
