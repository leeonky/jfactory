package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.Objects;

class SingleValuePropertyExpression<T> extends PropertyExpression<T> {
    private final Object value;
    private final String[] mixIns;
    private final String definition;

    public SingleValuePropertyExpression(Object value, String[] mixIns, String definition, String property, BeanClass<T> beanClass, String field) {
        super(property, beanClass, field);
        this.value = value;
        this.mixIns = mixIns;
        this.definition = definition;
    }

    @Override
    protected boolean isMatch(BeanClass<?> propertyType, Object propertyValue) {
        return Objects.equals(propertyValue, beanClass.getConverter().tryConvert(propertyType.getType(), value));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Producer<?> buildProducer(FactorySet factorySet, Instance<T> instance) {
        if (isIntently())
            return toBuilder(factorySet, beanClass.getPropertyWriter(property).getType().getElementOrPropertyType().getType()).createProducer(property);
        return new FixedValueProducer(beanClass.getPropertyWriter(property).getType(), value);
    }

    @Override
    public PropertyExpression<T> merge(PropertyExpression<T> propertyExpression) {
        return propertyExpression.mergeTo(this);
    }

    @Override
    protected PropertyExpression<T> mergeTo(SingleValuePropertyExpression<T> singleValuePropertyExpression) {
        return this;
    }

    private Builder<?> toBuilder(FactorySet factorySet, Class<?> propertyType) {
        return (definition != null ?
                factorySet.spec(definition)
                : factorySet.type(propertyType))
                .mixIn(mixIns);
    }
}
