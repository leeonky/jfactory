package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.Objects;

class SingleValuePropertyExpression<H> extends PropertyExpression<H> {
    private final Object value;
    private final MixInsSpec mixInsSpec;

    public SingleValuePropertyExpression(Object value, String property, BeanClass<H> hostClass, MixInsSpec mixInsSpec) {
        super(property, hostClass);
        this.value = value;
        this.mixInsSpec = mixInsSpec;
    }

    @Override
    protected <P> boolean isMatch(BeanClass<P> propertyType, P propertyValue) {
        return Objects.equals(propertyValue, hostClass.getConverter().tryConvert(propertyType.getType(), value));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Producer<?> buildProducer(FactorySet factorySet, Producer<H> host) {
        BeanClass<?> propertyType = hostClass.getPropertyWriter(property).getType();
        if (isIntently())
            return mixInsSpec.toBuilder(factorySet, propertyType).createProducer(property, true);
        return new FixedValueProducer(propertyType, value);
    }
}
