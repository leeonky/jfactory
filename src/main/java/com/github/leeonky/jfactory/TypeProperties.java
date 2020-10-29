package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.leeonky.util.BeanClass.cast;
import static java.util.Objects.hash;

class TypeProperties<T> {
    public final Map<String, Object> properties = new LinkedHashMap<>();
    public final BeanClass<T> type;

    public TypeProperties(BeanClass<T> type) {
        this.type = type;
    }

    public void merge(TypeProperties<T> another) {
        properties.putAll(another.properties);
    }

    public void putAll(Map<String, ?> properties) {
        this.properties.putAll(properties);
    }

    //TODO missing type
    @Override
    public int hashCode() {
        return hash(TypeProperties.class, properties);
    }

    //TODO missing compare type
    @Override
    public boolean equals(Object another) {
        return cast(another, TypeProperties.class)
                .map(typeProperties -> Objects.equals(properties, typeProperties.properties))
                .orElseGet(() -> super.equals(another));
    }

    public Map<String, PropertyExpression<T>> createPropertyExpressions() {
        return properties.entrySet().stream()
                .map(e -> ExpressionParser.parse(type, e.getKey(), e.getValue()))
                .collect(Collectors.groupingBy(PropertyExpression::getProperty)).values().stream()
                .map(expressions -> expressions.stream().reduce(PropertyExpression::merge).get())
                .collect(Collectors.toMap(PropertyExpression::getProperty, Function.identity()));
    }
}
