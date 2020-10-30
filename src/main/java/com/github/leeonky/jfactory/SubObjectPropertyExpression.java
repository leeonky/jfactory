package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;
import com.github.leeonky.util.Property;

import java.util.Collection;

import static java.lang.String.format;

class SubObjectPropertyExpression<H> extends PropertyExpression<H> {
    private final KeyValueCollection keyValueCollection;
    private final MixInsSpec mixInsSpec;

    public SubObjectPropertyExpression(KeyValueCollection keyValueCollection, MixInsSpec mixInsSpec, Property<H> property) {
        super(property);
        this.keyValueCollection = keyValueCollection;
        this.mixInsSpec = mixInsSpec;
    }

    @Override
    protected boolean isPropertyMatch(Object propertyValue) {
        return keyValueCollection.parseExpressions(property.getReaderType())
                .allMatch(queryExpression -> queryExpression.isMatch(propertyValue));
    }

    @Override
    public Producer<?> buildProducer(FactorySet factorySet, Producer<H> host) {
        if (isIntently())
            return toBuilder(factorySet, property.getWriterType()).createProducer(true);
        Collection<?> queried = toBuilder(factorySet, property.getReaderType()).queryAll();
        if (queried.isEmpty())
            return toBuilder(factorySet, property.getWriterType()).createProducer(false);
        return new FixedValueProducer<>(property.getWriterType(), queried.iterator().next());
    }

    private Builder<?> toBuilder(FactorySet factorySet, BeanClass<?> propertyType) {
        return keyValueCollection.apply(mixInsSpec.toBuilder(factorySet, propertyType));
    }

    @Override
    public PropertyExpression<H> merge(PropertyExpression<H> propertyExpression) {
        return propertyExpression.mergeBy(this);
    }

    @Override
    protected PropertyExpression<H> mergeBy(SubObjectPropertyExpression<H> another) {
        keyValueCollection.merge(another.keyValueCollection);
        mixInsSpec.merge(another.mixInsSpec, format("%s.%s", property.getBeanType().getName(), property.getName()));
        setIntently(isIntently() || another.isIntently());
        return this;
    }
}
