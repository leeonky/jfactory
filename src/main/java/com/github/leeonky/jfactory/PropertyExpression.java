package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

abstract class PropertyExpression<T> {
    private static final int GROUP_PROPERTY = 1;
    private static final int GROUP_COLLECTION_INDEX = 3;
    private static final int GROUP_MIX_IN = 5;
    private static final int GROUP_DEFINITION = 6;
    private static final int GROUP_INTENTLY = 7;
    private static final int GROUP_CONDITION = 9;
    protected final String property;
    protected final BeanClass<T> beanClass;
    private boolean intently = false;

    public PropertyExpression(String property, BeanClass<T> beanClass) {
        this.property = property;
        this.beanClass = beanClass;
    }

    public static <T> PropertyExpression<T> create(BeanClass<T> beanClass, String chain, Object value) {
        Matcher matcher = Pattern.compile("([^.(!\\[]+)(\\[(\\d+)])?(\\(([^, ]*[, ])*(.+)\\))?(!)?(\\.(.+))?").matcher(chain);
        if (!matcher.matches()) {
            //TODO not matched should throw exception
        }
        String property = matcher.group(GROUP_PROPERTY);
        PropertyExpression<T> propertyExpression = create(value,
                matcher.group(GROUP_MIX_IN) != null ? matcher.group(GROUP_MIX_IN).split(", |,| ") : new String[0],
                matcher.group(GROUP_DEFINITION), matcher.group(GROUP_CONDITION), property, beanClass)
                .setIntently(matcher.group(GROUP_INTENTLY) != null);

        if (matcher.group(GROUP_COLLECTION_INDEX) != null)
            propertyExpression = new CollectionPropertyExpression<>(Integer.valueOf(matcher.group(GROUP_COLLECTION_INDEX)), propertyExpression, property, beanClass);
        return propertyExpression;
    }

    private static <T> PropertyExpression<T> create(Object value, String[] mixIn, String definition, String condition, String property, BeanClass<T> beanClass) {
        return condition != null ?
                new SubObjectPropertyExpression<>(condition, value, mixIn, definition, property, beanClass)
                : new SingleValuePropertyExpression<>(value, mixIn, definition, property, beanClass);
    }

    public static <T> Map<String, PropertyExpression<T>> createPropertyExpressions(BeanClass<T> beanClass, Map<String, Object> criteria) {
        return criteria.entrySet().stream()
                .map(e -> create(beanClass, e.getKey(), e.getValue()))
                .collect(Collectors.groupingBy(expression -> expression.property)).values().stream()
                .map(expressions -> expressions.stream().reduce(PropertyExpression::merge).get())
                .collect(Collectors.toMap(q -> q.property, q -> q));
    }

    @SuppressWarnings("unchecked")
    public boolean isMatch(Object object) {
        if (object == null)
            return false;
        return isMatch(beanClass.getPropertyReader(property).getType(), beanClass.getPropertyValue((T) object, property));
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
