package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.Objects;

class SingleValuePropertyExpression<H> extends PropertyExpression<H> {
    private final Object value;
    private final MixInsSpec mixInsSpec;

    public SingleValuePropertyExpression(Object value, MixInsSpec mixInsSpec, Property<H> property) {
        super(property);
        this.value = value;
        this.mixInsSpec = mixInsSpec;
    }

    //TODO try to remove parameter propertyType
    @Override
    protected boolean isPropertyMatch(Object propertyValue) {
        return Objects.equals(propertyValue, property.getReader().tryConvert(value));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Producer<?> buildProducer(FactorySet factorySet, Producer<H> host) {
        BeanClass<?> propertyType = property.getWriter().getType();
        if (isIntently())
            return mixInsSpec.toBuilder(factorySet, propertyType).createProducer(property.getProperty(), true);
        return new FixedValueProducer(propertyType, value);
    }
}
