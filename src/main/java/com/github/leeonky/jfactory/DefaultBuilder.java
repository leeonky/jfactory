package com.github.leeonky.jfactory;

import java.util.*;

import static java.util.Arrays.asList;

class DefaultBuilder<T> implements Builder<T> {
    private final ObjectFactory<T> objectFactory;
    private final FactorySet factorySet;
    private final Map<String, Object> properties = new LinkedHashMap<>();
    private final Set<String> mixIns = new LinkedHashSet<>();

    public DefaultBuilder(ObjectFactory<T> objectFactory, FactorySet factorySet) {
        this.factorySet = factorySet;
        this.objectFactory = objectFactory;
    }

    @Override
    public T create() {
        return toProducer(null).getValue();
    }

    @Override
    public ObjectProducer<T> toProducer(String property) {
        return new ObjectProducer<>(factorySet, objectFactory, properties, mixIns);
    }

    @Override
    public Builder<T> mixIn(String... mixIns) {
        DefaultBuilder<T> newBuilder = copy();
        newBuilder.mixIns.addAll(asList(mixIns));
        return newBuilder;
    }

    @Override
    public Builder<T> property(String property, Object value) {
        DefaultBuilder<T> newBuilder = copy();
        newBuilder.properties.put(property, value);
        return newBuilder;
    }

    private DefaultBuilder<T> copy() {
        DefaultBuilder<T> builder = new DefaultBuilder<>(objectFactory, factorySet);
        builder.properties.putAll(properties);
        builder.mixIns.addAll(mixIns);
        return builder;
    }

    @Override
    public Builder<T> properties(Map<String, ?> properties) {
        DefaultBuilder<T> newBuilder = copy();
        newBuilder.properties.putAll(properties);
        return newBuilder;
    }

    @Override
    public T query() {
        Collection<T> collection = queryAll();
        return collection.isEmpty() ?
                null
                : collection.iterator().next();
    }

    @Override
    public Collection<T> queryAll() {
        return factorySet.getDataRepository().query(objectFactory.getType(), properties);
    }
}
