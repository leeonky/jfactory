package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.Map;
import java.util.stream.Collectors;

abstract class PropertyExpression<T> {
    protected final String property, field;
    protected final BeanClass<T> beanClass;
    private boolean intently = false;

    public PropertyExpression(String property, BeanClass<T> beanClass, String field) {
        this.property = property;
        this.beanClass = beanClass;
        this.field = field;
    }

    public static <T> Map<String, PropertyExpression<T>> createPropertyExpressions(BeanClass<T> beanClass, Map<String, Object> criteria) {
        return criteria.entrySet().stream()
                .map(e -> ExpressionParser.parse(beanClass, e.getKey(), e.getValue()))
                .collect(Collectors.groupingBy(expression -> expression.property)).values().stream()
                .map(expressions -> expressions.stream().reduce(PropertyExpression::merge).get())
                .collect(Collectors.toMap(q -> q.property, q -> q));
    }

    @SuppressWarnings("unchecked")
    public boolean isMatch(Object object) {
        return object != null && !isIntently() && isMatch(beanClass.getPropertyReader(property).getType(), beanClass.getPropertyValue((T) object, property));
    }

    protected abstract boolean isMatch(BeanClass<?> propertyType, Object propertyValue);

    public abstract Producer<?> buildProducer(FactorySet factorySet, Producer<T> parent, Instance<T> instance);

    protected abstract PropertyExpression<T> merge(PropertyExpression<T> propertyExpression);

    protected PropertyExpression<T> mergeTo(SingleValuePropertyExpression<T> singleValuePropertyExpression) {
        throw new IllegalArgumentException(String.format("Cannot merge different structure %s.%s", beanClass.getName(), property));
    }

    protected PropertyExpression<T> mergeTo(SubObjectPropertyExpression<T> conditionValueSet) {
        throw new IllegalArgumentException(String.format("Cannot merge different structure %s.%s", beanClass.getName(), property));
    }

    protected PropertyExpression<T> mergeTo(CollectionPropertyExpression<T> collectionConditionValue) {
        throw new IllegalArgumentException(String.format("Cannot merge different structure %s.%s", beanClass.getName(), property));
    }

    protected boolean isIntently() {
        return intently;
    }

    protected PropertyExpression<T> setIntently(boolean intently) {
        this.intently = intently;
        return this;
    }
}
