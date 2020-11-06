package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;
import com.github.leeonky.util.Property;

import java.util.Optional;

class SubObjectExpression<P> extends Expression<P> {
    private final KeyValueCollection properties;
    private final TraitsSpec traitsSpec;

    public SubObjectExpression(KeyValueCollection properties, TraitsSpec traitsSpec, Property<P> property) {
        super(property);
        this.properties = properties;
        this.traitsSpec = traitsSpec;
    }

    @Override
    protected boolean isPropertyMatch(Object propertyValue) {
        return properties.matcher(property.getReaderType()).matches(propertyValue);
    }

    @Override
    public Producer<?> buildProducer(FactorySet factorySet, Producer<P> parent) {
        return query(factorySet).<Producer<?>>map(object -> new FixedValueProducer<>(property.getWriterType(), object))
                .orElseGet(() -> toBuilder(factorySet, property.getWriterType()).createProducer(intently));
    }

    private Optional<?> query(FactorySet factorySet) {
        if (intently)
            return Optional.empty();
        return toBuilder(factorySet, property.getReaderType()).queryAll().stream().findFirst();
    }

    private Builder<?> toBuilder(FactorySet factorySet, BeanClass<?> propertyType) {
        return properties.apply(traitsSpec.toBuilder(factorySet, propertyType));
    }

    @Override
    public Expression<P> merge(Expression<P> another) {
        return another.mergeBy(this);
    }

    @Override
    protected Expression<P> mergeBy(SubObjectExpression<P> another) {
        properties.merge(another.properties);
        traitsSpec.merge(another.traitsSpec, property.toString());
        setIntently(intently || another.intently);
        return this;
    }
}
