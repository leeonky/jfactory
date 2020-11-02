package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.leeonky.util.BeanClass.cast;

class ObjectProducer<T> extends Producer<T> {
    private final ObjectFactory<T> objectFactory;
    private final FactorySet factorySet;
    private final DefaultBuilder<T> builder;
    private final boolean intently;
    private final Instance<T> instance;
    private final Map<String, Producer<?>> children = new HashMap<>();
    private final Map<PropertyChain, Dependency<?>> dependencies = new LinkedHashMap<>();
    private final List<Link> links = new ArrayList<>();

    public ObjectProducer(FactorySet factorySet, ObjectFactory<T> objectFactory, DefaultBuilder<T> builder,
                          boolean intently, Instance<T> instance) {
        super(objectFactory.getType());
        this.objectFactory = objectFactory;
        this.factorySet = factorySet;
        this.builder = builder;
        this.intently = intently;
        this.instance = instance;
    }

    @Override
    public void addChild(String property, Producer<?> producer) {
        children.put(property, producer);
    }

    @Override
    public Producer<?> childOrDefault(String property) {
        Producer<?> producer = children.get(property);
        if (producer == null) {
            BeanClass<?> propertyType = getType().getPropertyWriter(property).getType();
            if (propertyType.isCollection())
                addChild(property, producer = new CollectionProducer<>(factorySet.getFactoryPool(),
                        getType(), propertyType, instance.sub(property)));
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
    public Optional<Producer<?>> child(String property) {
        return Optional.ofNullable(children.get(property));
    }

    public void addDependency(PropertyChain property, Function<Object[], Object> function, List<PropertyChain> propertyChains) {
        dependencies.put(property, new Dependency<>(property, propertyChains, function));
    }

    public ObjectProducer<T> doDependenciesAndLinks() {
        doDependencies();
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
    protected void doDependencies() {
        children.values().forEach(Producer::doDependencies);
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
        return cast(obj, ObjectProducer.class)
                .map(another -> objectFactory.equals(another.objectFactory) && builder.equals(another.builder)
                        && isNotChange() && another.isNotChange() && !intently && !another.intently)
                .orElseGet(() -> super.equals(obj));
    }

    public void link(List<PropertyChain> properties) {
        if (properties.size() > 1)
            links.stream().filter(link -> link.contains(properties)).findFirst().orElseGet(() -> {
                Link link = new Link();
                links.add(link);
                return link;
            }).merge(properties);
    }

    @Override
    public Map<PropertyChain, Producer<?>> children() {
        return children.entrySet().stream()
                .collect(Collectors.toMap(e -> PropertyChain.createChain(e.getKey()), Map.Entry::getValue));
    }
}
