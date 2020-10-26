package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.github.leeonky.jfactory.ExpressionParser.parse;

class SubObjectPropertyExpression<H> extends PropertyExpression<H> {
    private final Map<String, Object> conditionValues = new LinkedHashMap<>();
    private final MixInsSpec mixInsSpec;

    public SubObjectPropertyExpression(String condition, Object value, MixInsSpec mixInsSpec, Property<H> property) {
        super(property);
        this.mixInsSpec = mixInsSpec;
        conditionValues.put(condition, value);
    }

    @Override
    protected boolean isPropertyMatch(Object propertyValue) {
        return conditionValues.entrySet().stream()
                .map(conditionValue -> parse(property.getReaderType(), conditionValue.getKey(), conditionValue.getValue()))
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
        return mixInsSpec.toBuilder(factorySet, propertyType).properties(conditionValues);
    }

    @Override
    public PropertyExpression<H> merge(PropertyExpression<H> propertyExpression) {
        return propertyExpression.mergeBy(this);
    }

    @Override
    protected PropertyExpression<H> mergeBy(SubObjectPropertyExpression<H> another) {
        another.conditionValues.putAll(conditionValues);
        conditionValues.clear();
        conditionValues.putAll(another.conditionValues);
        mixInsSpec.mergeSubObject(another.mixInsSpec, property.getBeanType(), property.getName());
        setIntently(isIntently() || another.isIntently());
        return this;
    }
}
