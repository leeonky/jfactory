package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;
import com.github.leeonky.util.Property;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

class KeyValueCollection {

    //TODO try to use collection with uniq key type
    private final Map<String, KeyValue> keyValues = new LinkedHashMap<>();

    public void merge(KeyValueCollection another) {
        another.keyValues.putAll(keyValues);
        keyValues.clear();
        keyValues.putAll(another.keyValues);
    }

    // TODO try clean with reduce
    public Builder<?> apply(Builder<?> builder) {
        for (KeyValue keyValue : keyValues.values())
            builder = keyValue.apply(builder);
        return builder;
    }

    public <T> Stream<PropertyExpression<T>> parseExpressions(BeanClass<T> beanType) {
        return keyValues.values().stream().map(keyValue -> keyValue.createExpression(beanType));
    }

    public <H> PropertyExpression<H> createSubExpression(Property<H> property, MixInsSpec mixInsSpec, Object value) {
        return isSingleValue() ? new SingleValuePropertyExpression<>(value, mixInsSpec, property)
                : new SubObjectPropertyExpression<>(this, mixInsSpec, property);
    }

    private boolean isSingleValue() {
        return keyValues.size() == 1 && keyValues.values().iterator().next().nullKey();
    }

    public KeyValueCollection add(String key, Object value) {
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
}
