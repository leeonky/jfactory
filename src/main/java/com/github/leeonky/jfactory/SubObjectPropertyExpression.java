package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

class SubObjectPropertyExpression<H, B> extends PropertyExpression<H, B> {
    private final Map<String, Object> conditionValues = new LinkedHashMap<>();
    private final MixInsSpec mixInsSpec;

    public SubObjectPropertyExpression(String condition, Object value,
                                       MixInsSpec mixInsSpec, String property, BeanClass<H> hostClass, BeanClass<B> beanClass) {
        super(property, hostClass, beanClass);
        this.mixInsSpec = mixInsSpec;
        conditionValues.put(condition, value);
    }

    @Override
    protected <P> boolean isMatch(BeanClass<P> propertyType, P propertyValue) {
        return conditionValues.entrySet().stream()
                .map(conditionValue -> ExpressionParser.parse(propertyType, conditionValue.getKey(), conditionValue.getValue()))
                .allMatch(queryExpression -> queryExpression.isMatch(propertyValue));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Producer<?> buildProducer(FactorySet factorySet, Producer<H> host, Instance<B> instance) {
        BeanClass<?> propertyType = hostClass.getPropertyWriter(property).getType();
        if (isIntently())
            return toBuilder(factorySet, propertyType).createProducer(property, true);
        Collection<?> queried = toBuilder(factorySet, hostClass.getPropertyReader(property).getType()).queryAll();
        if (queried.isEmpty())
            return toBuilder(factorySet, propertyType).createProducer(property, false);
        return new FixedValueProducer(propertyType, queried.iterator().next());
    }

    private Builder<?> toBuilder(FactorySet factorySet, BeanClass<?> propertyType) {
        return mixInsSpec.toBuilder(factorySet, propertyType).properties(conditionValues);
    }

    @Override
    public PropertyExpression<H, B> merge(PropertyExpression<H, B> propertyExpression) {
        return propertyExpression.mergeBy(this);
    }

    @Override
    protected PropertyExpression<H, B> mergeBy(SubObjectPropertyExpression<H, B> another) {
        another.conditionValues.putAll(conditionValues);
        conditionValues.clear();
        conditionValues.putAll(another.conditionValues);
        mixInsSpec.mergeSubObject(another.mixInsSpec, hostClass, property);
        setIntently(isIntently() || another.isIntently());
        return this;
    }

}
