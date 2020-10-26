package com.github.leeonky.jfactory;

import java.util.Objects;

class SingleValuePropertyExpression<H> extends PropertyExpression<H> {
    private final Object value;
    private final MixInsSpec mixInsSpec;

    public SingleValuePropertyExpression(Object value, MixInsSpec mixInsSpec, Property<H> property) {
        super(property);
        this.value = value;
        this.mixInsSpec = mixInsSpec;
    }

    @Override
    protected boolean isPropertyMatch(Object propertyValue) {
        return Objects.equals(propertyValue, property.getReader().tryConvert(value));
    }

    @Override
    public Producer<?> buildProducer(FactorySet factorySet, Producer<H> host) {
        if (isIntently())
            return mixInsSpec.toBuilder(factorySet, property.getWriterType()).createProducer(property.getName(), true);
        return new FixedValueProducer<>(property.getWriterType(), value);
    }

}
