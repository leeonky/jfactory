package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;
import com.github.leeonky.util.Property;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static com.github.leeonky.jfactory.ExpressionParser.parse;

class SupKeyValue {
    private final Map<String, Object> keyValues = new LinkedHashMap<>();
    private final boolean simgleValue;

    public SupKeyValue(String key, Object value) {
        simgleValue = key != null;
        keyValues.put(key, value);
    }

    public void merge(SupKeyValue another) {
        another.keyValues.putAll(keyValues);
        keyValues.clear();
        keyValues.putAll(another.keyValues);
    }

    public Builder<?> apply(Builder<?> builder) {
        return builder.properties(keyValues);
    }

    public <T> Stream<PropertyExpression<T>> parseExpressions(BeanClass<T> beanType) {
        return keyValues.entrySet().stream().map(keyValue -> parse(beanType, keyValue.getKey(), keyValue.getValue()));
    }

    public <H> PropertyExpression<H> createSubExpression(Property<H> property, MixInsSpec mixInsSpec, Object value) {
        return simgleValue ? new SubObjectPropertyExpression<>(this, mixInsSpec, property)
                : new SingleValuePropertyExpression<>(value, mixInsSpec, property);
    }
}
