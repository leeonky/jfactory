package com.github.leeonky.jfactory;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static com.github.leeonky.jfactory.PropertyExpression.createPropertyExpressions;
import static com.github.leeonky.util.BeanClass.cast;
import static java.util.Arrays.asList;
import static java.util.Objects.hash;

class DefaultBuilder<T> implements Builder<T> {
    private final ObjectFactory<T> objectFactory;
    private final FactorySet factorySet;
    private final Set<String> mixIns = new LinkedHashSet<>();
    private final TypeProperties<T> typeProperties;

    public DefaultBuilder(ObjectFactory<T> objectFactory, FactorySet factorySet) {
        this.factorySet = factorySet;
        this.objectFactory = objectFactory;
        typeProperties = new TypeProperties<>(objectFactory.getType());
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
        builder.typeProperties.merge(typeProperties);
        builder.mixIns.addAll(mixIns);
        return builder;
    }

    @Override
    public Builder<T> properties(Map<String, ?> properties) {
        DefaultBuilder<T> newBuilder = copy();
        newBuilder.typeProperties.putAll(properties);
        return newBuilder;
    }

    @Override
    public T query() {
        return queryAll().stream().findFirst().orElse(null);
    }

    @Override
    public Collection<T> queryAll() {
        return factorySet.getDataRepository().query(typeProperties.type, typeProperties.properties);
    }

    public void collectSpec(Instance<T> instance) {
        objectFactory.collectSpec(mixIns, instance);
    }

    public Map<String, PropertyExpression<T>> toExpressions() {
        return createPropertyExpressions(typeProperties.type, typeProperties.properties);
    }

    @Override
    public int hashCode() {
        return hash(DefaultBuilder.class, typeProperties, mixIns);
    }

    @Override
    public boolean equals(Object another) {
        return cast(another, DefaultBuilder.class)
                .map(builder -> typeProperties.equals(builder.typeProperties) && mixIns.equals(builder.mixIns))
                .orElseGet(() -> super.equals(another));
    }
}
