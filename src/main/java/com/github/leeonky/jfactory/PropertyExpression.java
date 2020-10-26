package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

abstract class PropertyExpression<H> {
    protected final Property<H> property;
    private boolean intently = false;

    public PropertyExpression(Property<H> property) {
        this.property = property;
    }

    public static <T> Map<String, PropertyExpression<T>> createPropertyExpressions(BeanClass<T> beanClass,
                                                                                   Map<String, Object> criteria) {
        return criteria.entrySet().stream()
                .map(e -> ExpressionParser.parse(beanClass, e.getKey(), e.getValue()))
                .collect(Collectors.groupingBy(PropertyExpression::getProperty)).values().stream()
                .map(expressions -> expressions.stream().reduce(PropertyExpression::merge).get())
                .collect(Collectors.toMap(PropertyExpression::getProperty, Function.identity()));
    }

    protected String getProperty() {
        return property.getName();
    }

    public boolean isMatch(H object) {
        return object != null && !isIntently() && isPropertyMatch(property.getValue(object));
    }

    protected abstract boolean isPropertyMatch(Object propertyValue);

    public abstract Producer<?> buildProducer(FactorySet factorySet, Producer<H> host);

    protected PropertyExpression<H> merge(PropertyExpression<H> propertyExpression) {
        return propertyExpression;
    }

    protected PropertyExpression<H> mergeBy(SubObjectPropertyExpression<H> conditionValueSet) {
        return this;
    }

    protected PropertyExpression<H> mergeBy(CollectionPropertyExpression<H, ?> collectionConditionValue) {
        return this;
    }

    protected boolean isIntently() {
        return intently;
    }

    protected PropertyExpression<H> setIntently(boolean intently) {
        this.intently = intently;
        return this;
    }
}
