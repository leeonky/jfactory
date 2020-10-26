package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.Map;
import java.util.stream.Collectors;

abstract class PropertyExpression<H> {
    protected final String property;
    protected final BeanClass<H> hostClass;
    private boolean intently = false;

    public PropertyExpression(String property, BeanClass<H> hostClass) {
        this.property = property;
        this.hostClass = hostClass;
    }

    public static <T> Map<String, PropertyExpression<T>> createPropertyExpressions(BeanClass<T> beanClass,
                                                                                   Map<String, Object> criteria) {
        return criteria.entrySet().stream()
                .map(e -> ExpressionParser.parse(beanClass, e.getKey(), e.getValue()))
                .collect(Collectors.groupingBy(expression -> expression.property)).values().stream()
                .map(expressions -> expressions.stream().reduce(PropertyExpression::merge).get())
                .collect(Collectors.toMap(q -> q.property, q -> q));
    }

    @SuppressWarnings("unchecked")
    public boolean isMatch(H object) {
        return object != null && !isIntently()
                && isMatch((BeanClass) hostClass.getPropertyReader(property).getType(), hostClass.getPropertyValue(object, property));
    }

    protected abstract <P> boolean isMatch(BeanClass<P> propertyType, P propertyValue);

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
