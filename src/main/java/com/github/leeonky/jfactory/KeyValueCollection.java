package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;
import com.github.leeonky.util.Property;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class KeyValueCollection {

    private final Map<String, KeyValue> keyValues = new LinkedHashMap<>();

    public void merge(KeyValueCollection another) {
        another.keyValues.putAll(keyValues);
        keyValues.clear();
        keyValues.putAll(another.keyValues);
    }

    Builder<?> apply(Builder<?> builder) {
        for (KeyValue keyValue : keyValues.values())
            builder = keyValue.apply(builder);
        return builder;
    }

    <T> Collection<Expression<T>> expressions(BeanClass<T> type) {
        return keyValues.values().stream().map(keyValue -> keyValue.createExpression(type))
                .collect(Collectors.groupingBy(Expression::getProperty)).values().stream()
                .map(expressions -> expressions.stream().reduce(Expression::merge).get())
                .collect(Collectors.toList());
    }

    <H> Expression<H> createExpression(Property<H> property, TraitsSpec traitsSpec, Object value) {
        return isSingleValue() ? new SingleValueExpression<>(value, traitsSpec, property)
                : new SubObjectExpression<>(this, traitsSpec, property);
    }

    private boolean isSingleValue() {
        return keyValues.size() == 1 && keyValues.values().iterator().next().nullKey();
    }

    public KeyValueCollection append(String key, Object value) {
        keyValues.put(key, new KeyValue(key, value));
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

    public <T> Matcher<T> matcher(BeanClass<T> type) {
        return new Matcher<>(type);
    }

    public class Matcher<T> {
        private final Collection<Expression<T>> expressions;

        Matcher(BeanClass<T> type) {
            expressions = expressions(type);
        }

        public boolean matches(T object) {
            return expressions.stream().allMatch(e -> e.isMatch(object));
        }
    }
}
