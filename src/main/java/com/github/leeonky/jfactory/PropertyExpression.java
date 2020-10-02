package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.Map;
import java.util.stream.Collectors;

abstract class PropertyExpression<H, B> {
    protected final String property;
    protected final BeanClass<H> hostClass;
    protected final BeanClass<B> beanClass;
    private boolean intently = false;

    public PropertyExpression(String property, BeanClass<H> hostClass, BeanClass<B> beanClass) {
        this.property = property;
        this.hostClass = hostClass;
        this.beanClass = beanClass;
    }

    public static <T> Map<String, PropertyExpression<T, T>> createPropertyExpressions(BeanClass<T> beanClass, Map<String, Object> criteria) {
        return criteria.entrySet().stream()
                .map(e -> ExpressionParser.parse(beanClass, e.getKey(), e.getValue()))
                .collect(Collectors.groupingBy(expression -> expression.property)).values().stream()
                .map(expressions -> expressions.stream().reduce(PropertyExpression::merge).get())
                .collect(Collectors.toMap(q -> q.property, q -> q));
    }

    @SuppressWarnings("unchecked")
    public boolean isMatch(H object) {
        return object != null && !isIntently() && isMatch((BeanClass) hostClass.getPropertyReader(property).getType(),
                hostClass.getPropertyValue(object, property));
    }

    protected abstract <P> boolean isMatch(BeanClass<P> propertyType, P propertyValue);

    public abstract Producer<?> buildProducer(FactorySet factorySet, Instance<B> instance);

    protected PropertyExpression<H, B> merge(PropertyExpression<H, B> propertyExpression) {
        return propertyExpression;
    }

    protected PropertyExpression<H, B> mergeBy(SubObjectPropertyExpression<H, B> conditionValueSet) {
        return this;
    }

    protected PropertyExpression<H, B> mergeBy(CollectionPropertyExpression<H, ?, B> collectionConditionValue) {
        return this;
    }

    protected boolean isIntently() {
        return intently;
    }

    protected PropertyExpression<H, B> setIntently(boolean intently) {
        this.intently = intently;
        return this;
    }
}
