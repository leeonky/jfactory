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

    protected abstract PropertyExpression<H, B> merge(PropertyExpression<H, B> propertyExpression);

    protected PropertyExpression<H, B> mergeTo(SingleValuePropertyExpression<H, B> singleValuePropertyExpression) {
        throw new IllegalArgumentException(String.format("Cannot merge different structure %s.%s", hostClass.getName(), property));
    }

    protected PropertyExpression<H, B> mergeTo(SubObjectPropertyExpression<H, B> conditionValueSet) {
        throw new IllegalArgumentException(String.format("Cannot merge different structure %s.%s", hostClass.getName(), property));
    }

    protected PropertyExpression<H, B> mergeTo(CollectionPropertyExpression<H, B> collectionConditionValue) {
        throw new IllegalArgumentException(String.format("Cannot merge different structure %s.%s", hostClass.getName(), property));
    }

    protected boolean isIntently() {
        return intently;
    }

    protected PropertyExpression<H, B> setIntently(boolean intently) {
        this.intently = intently;
        return this;
    }
}
