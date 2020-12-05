package com.github.leeonky.jfactory;

import com.github.leeonky.util.PropertyWriter;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.leeonky.jfactory.PropertyChain.createChain;
import static com.github.leeonky.util.BeanClass.cast;
import static java.util.stream.Collectors.*;

class ObjectProducer<T> extends Producer<T> {
    private final ObjectFactory<T> factory;
    private final FactorySet factorySet;
    private final DefaultBuilder<T> builder;
    private final boolean intently;
    private final RootInstance<T> instance;
    private final Map<String, Producer<?>> children = new HashMap<>();
    private final Map<PropertyChain, Dependency<?>> dependencies = new LinkedHashMap<>();
    private final LinkCollection linkCollection = new LinkCollection();

    public ObjectProducer(FactorySet factorySet, ObjectFactory<T> factory, DefaultBuilder<T> builder, boolean intently) {
        super(factory.getType());
        this.factory = factory;
        this.factorySet = factorySet;
        this.builder = builder;
        this.intently = intently;
        instance = factory.createInstance(builder.getArguments());
        establishDefaultValueProducers();
        builder.establishSpecProducers(this, instance);
    }

    @Override
    public void addChild(String property, Producer<?> producer) {
        children.put(property, producer);
    }

    @Override
    public Producer<?> childOrDefault(String property) {
        Producer<?> producer = children.get(property);
        if (producer == null)
            producer = addDefaultCollectionProducer(getType().getPropertyWriter(property));
        return producer;
    }

    private Producer<?> addDefaultCollectionProducer(PropertyWriter<?> property) {
        Producer<?> result = null;
        if (property.getType().isCollection())
            addChild(property.getName(), result = new CollectionProducer<>(getType(), property.getType(),
                    instance.sub(property), factory.getFactoryPool()));
        return result;
    }

    @Override
    protected T produce() {
        return instance.cache(() -> factory.create(instance), obj -> {
            children.forEach((property, producer) -> getType().setPropertyValue(obj, property, producer.getValue()));
            factorySet.getDataRepository().save(obj);
        });
    }

    @Override
    public Optional<Producer<?>> child(String property) {
        return Optional.ofNullable(children.get(property));
    }

    public void addDependency(PropertyChain property, Function<Object[], Object> rule, List<PropertyChain> dependencies) {
        this.dependencies.put(property, new Dependency<>(property, dependencies, rule));
    }

    public ObjectProducer<T> doDependenciesAndLinks() {
        doDependencies();
        getAllChildren().values().forEach(Producer::checkChange);
        doLinks(this, createChain(""));
        getAllChildren().values().forEach(Producer::checkChange);
        uniqSameSubObjectProducer();
        return this;
    }

    @Override
    protected void doLinks(Producer<?> root, PropertyChain current) {
        children.forEach((property, producer) -> producer.doLinks(root, current.concat(property)));
        linkCollection.processLinks(root, current);
        beforeCheckChange();
    }

    private void uniqSameSubObjectProducer() {
        getAllChildren().entrySet().stream()
                .filter(e -> e.getValue() instanceof ObjectProducer)
                .collect(Collectors.groupingBy(Map.Entry::getValue, mapping(Map.Entry::getKey, toList())))
                .forEach((_ignore, properties) -> link(properties));
        doLinks(this, createChain(""));
    }

    @Override
    protected void doDependencies() {
        children.values().forEach(Producer::doDependencies);
        dependencies.values().forEach(dependency -> dependency.process(this));
        beforeCheckChange();
    }

    @Override
    public int hashCode() {
        return Objects.hash(ObjectProducer.class, factory, builder.hashCode(), uniqHashWhenChange(), uniqHashWhenIntently());
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
                .map(another -> factory.equals(another.factory) && builder.equals(another.builder)
                        && isNotChange() && another.isNotChange() && !intently && !another.intently)
                .orElseGet(() -> super.equals(obj));
    }

    public void link(List<PropertyChain> properties) {
        linkCollection.link(properties);
    }

    @Override
    public Map<PropertyChain, Producer<?>> children() {
        return children.entrySet().stream().collect(toMap(e -> createChain(e.getKey()), Map.Entry::getValue));
    }

    private void establishDefaultValueProducers() {
        getType().getPropertyWriters().values().forEach(writer -> subDefaultValueProducer(writer)
                .ifPresent(producer -> addChild(writer.getName(), producer)));
    }

    @Override
    public Optional<Producer> subDefaultValueProducer(PropertyWriter<?> property) {
        return factory.getFactoryPool().queryDefaultValueBuilder(property.getType())
                .map(builder -> new DefaultValueProducer<>(getType(), builder, instance.sub(property)));
    }

    @Override
    protected Producer<T> changeTo(Producer<T> newProducer) {
        return newProducer.changeFrom(this);
    }

    @Override
    protected Producer<T> changeFrom(ObjectProducer<T> origin) {
        return origin.clone(builder);
    }

    private Producer<T> clone(DefaultBuilder<T> another) {
        return builder.clone(another).createProducer(intently);
    }
}
