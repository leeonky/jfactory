package com.github.leeonky.jfactory;

import com.github.leeonky.util.Property;

import java.util.Objects;

class SingleValueExpression<H> extends Expression<H> {
    private final Object value;
    private final TraitsSpec traitsSpec;

    public SingleValueExpression(Object value, TraitsSpec traitsSpec, Property<H> property) {
        super(property);
        this.value = value;
        this.traitsSpec = traitsSpec;
    }

    @Override
    protected boolean isPropertyMatch(Object propertyValue) {
        return Objects.equals(propertyValue, property.getReader().tryConvert(value));
    }

    @Override
    public Producer<?> buildProducer(FactorySet factorySet, Producer<H> parent) {
        if (isIntently())
            return traitsSpec.toBuilder(factorySet, property.getWriterType()).createProducer(true);
        return new FixedValueProducer<>(property.getWriterType(), value);
    }

}
