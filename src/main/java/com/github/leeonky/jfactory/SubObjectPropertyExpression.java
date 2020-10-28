package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;
import com.github.leeonky.util.Property;

import java.util.Collection;

class SubObjectPropertyExpression<H> extends PropertyExpression<H> {
    private final SupKeyValue supKeyValue;
    private final MixInsSpec mixInsSpec;

    public SubObjectPropertyExpression(SupKeyValue supKeyValue, MixInsSpec mixInsSpec, Property<H> property) {
        super(property);
        this.supKeyValue = supKeyValue;
        this.mixInsSpec = mixInsSpec;
    }

    @Override
    protected boolean isPropertyMatch(Object propertyValue) {
        return supKeyValue.parseExpressions(property.getReaderType())
                .allMatch(queryExpression -> queryExpression.isMatch(propertyValue));
    }

    @Override
    public Producer<?> buildProducer(FactorySet factorySet, Producer<H> host) {
        if (isIntently())
            return toBuilder(factorySet, property.getWriterType()).createProducer(property.getName(), true);
        Collection<?> queried = toBuilder(factorySet, property.getReaderType()).queryAll();
        if (queried.isEmpty())
            return toBuilder(factorySet, property.getWriterType()).createProducer(property.getName(), false);
        return new FixedValueProducer<>(property.getWriterType(), queried.iterator().next());
    }

    private Builder<?> toBuilder(FactorySet factorySet, BeanClass<?> propertyType) {
        return supKeyValue.apply(mixInsSpec.toBuilder(factorySet, propertyType));
    }

    @Override
    public PropertyExpression<H> merge(PropertyExpression<H> propertyExpression) {
        return propertyExpression.mergeBy(this);
    }

    @Override
    protected PropertyExpression<H> mergeBy(SubObjectPropertyExpression<H> another) {
        supKeyValue.merge(another.supKeyValue);
        mixInsSpec.mergeSubObject(another.mixInsSpec, property.getBeanType(), property.getName());
        setIntently(isIntently() || another.isIntently());
        return this;
    }

}
