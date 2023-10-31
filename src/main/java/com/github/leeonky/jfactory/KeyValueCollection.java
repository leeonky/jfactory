package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;
import com.github.leeonky.util.Property;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class KeyValueCollection {
    private final FactorySet factorySet;
    private final Map<String, KeyValue> keyValues = new LinkedHashMap<>();

    public KeyValueCollection(FactorySet factorySet) {
        this.factorySet = factorySet;
    }

    public void insertAll(KeyValueCollection another) {
        LinkedHashMap<String, KeyValue> merged = new LinkedHashMap<String, KeyValue>() {{
            putAll(another.keyValues);
            putAll(keyValues);
        }};
        keyValues.clear();
        keyValues.putAll(merged);
    }

    public void appendAll(KeyValueCollection another) {
        keyValues.putAll(another.keyValues);
    }

    Builder<?> apply(Builder<?> builder) {
        for (KeyValue keyValue : keyValues.values())
            builder = keyValue.apply(builder);
        return builder;
    }

    //    TODO remove arg type
    <T> Collection<Expression<T>> expressions(BeanClass<T> type, ObjectFactory<T> objectFactory) {
        return keyValues.values().stream().map(keyValue -> keyValue.createExpression(type, objectFactory))
                .collect(Collectors.groupingBy(Expression::getProperty)).values().stream()
                .map(Expression::merge)
                .collect(Collectors.toList());
    }

    <H> Expression<H> createExpression(Property<H> property, TraitsSpec traitsSpec, Property<?> parentProperty, ObjectFactory<?> objectFactory) {
        if (isSingleValue()) {
            Object value = transform(property, parentProperty, objectFactory);
            if (createOrLinkAnyExist(value))
                return new SubObjectExpression<>(new KeyValueCollection(factorySet), traitsSpec, property, objectFactory);
            return new SingleValueExpression<>(value, traitsSpec, property);
        }
        return new SubObjectExpression<>(this, traitsSpec, property, objectFactory);
    }

    private boolean createOrLinkAnyExist(Object value) {
        return value instanceof Map && ((Map<?, ?>) value).isEmpty();
    }

    private <H> Object transform(Property<H> property, Property<?> parentProperty, ObjectFactory<?> objectFactory) {
        String transformerName = parentProperty != null && property.getBeanType().isCollection()
                ? parentProperty.getName() + "[]" : property.getName();
        return objectFactory.transform(transformerName, keyValues.values().iterator().next().getValue());
    }

    private boolean isSingleValue() {
        return keyValues.size() == 1 && keyValues.values().iterator().next().nullKey();
    }

    public KeyValueCollection append(String key, Object value) {
        keyValues.put(key, new KeyValue(key, value, factorySet));
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(KeyValueCollection.class, keyValues.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        return BeanClass.cast(obj, KeyValueCollection.class)
                .map(keyValueCollection -> Objects.equals(keyValues, keyValueCollection.keyValues))
                .orElseGet(() -> super.equals(obj));
    }

    public <T> Matcher<T> matcher(BeanClass<T> type, ObjectFactory<T> objectFactory) {
        return new Matcher<>(type, objectFactory);
    }

    public class Matcher<T> {
        private final Collection<Expression<T>> expressions;

        Matcher(BeanClass<T> type, ObjectFactory<T> objectFactory) {
            expressions = expressions(type, objectFactory);
        }

        public boolean matches(T object) {
            return expressions.stream().allMatch(e -> e.isMatch(object));
        }
    }
}
