package com.github.leeonky.jfactory;

import java.util.*;

import static com.github.leeonky.jfactory.PropertyExpression.createPropertyExpressions;
import static com.github.leeonky.util.BeanClass.cast;
import static java.util.Arrays.asList;
import static java.util.Objects.hash;

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
        return createProducer(null, false).processDependencyAndLink().getValue();
    }

    @Override
    public ObjectProducer<T> createProducer(String property, boolean intently) {
        return new ObjectProducer<>(factorySet, objectFactory, this, intently);
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

    public void collectSpec(Instance<T> instance) {
        objectFactory.collectSpec(mixIns, instance);
    }

    public Map<String, PropertyExpression<T, T>> toExpressions() {
        return createPropertyExpressions(objectFactory.getType(), properties);
    }

    @Override
    public int hashCode() {
        return hash(DefaultBuilder.class, properties, mixIns);
    }

    @Override
    public boolean equals(Object another) {
        return cast(another, DefaultBuilder.class)
                .map(builder -> properties.equals(builder.properties) && mixIns.equals(builder.mixIns))
                .orElseGet(() -> super.equals(another));
    }
}
