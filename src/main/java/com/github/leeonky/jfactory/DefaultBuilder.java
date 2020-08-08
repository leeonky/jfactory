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
        return createProducer(null).getValue();
    }

    @Override
    public Producer<T> createProducer(String property) {
        return new ObjectProducer<>(factorySet, objectFactory, properties, mixIns);
    }

    @Override
    public Builder<T> mixIn(String... mixIns) {
        DefaultBuilder<T> newBuilder = copy();
        newBuilder.mixIns.addAll(asList(mixIns));
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
        return queryAll().stream().findFirst().orElse(null);
    }

    @Override
    public Collection<T> queryAll() {
        return factorySet.getDataRepository().query(objectFactory.getType(), properties);
    }
}
