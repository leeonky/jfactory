package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
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

    @Override
    public int hashCode() {
        return hash(TypeProperties.class, type, properties);
    }

    @Override
    public boolean equals(Object another) {
        return cast(another, TypeProperties.class)
                .map(typeProperties -> Objects.equals(properties, typeProperties.properties)
                        && Objects.equals(type, typeProperties.type))
                .orElseGet(() -> super.equals(another));
    }

    public Collection<T> select(Collection<T> data) {
        Collection<PropertyExpression<T>> expressions = toExpressions();
        return data.stream().filter(o -> expressions.stream().allMatch(e -> e.isMatch(o))).collect(Collectors.toList());
    }

    public Collection<PropertyExpression<T>> toExpressions() {
        return properties.entrySet().stream()
                .map(e -> ExpressionParser.parse(type, e.getKey(), e.getValue()))
                .collect(Collectors.groupingBy(PropertyExpression::getProperty)).values().stream()
                .map(expressions -> expressions.stream().reduce(PropertyExpression::merge).get())
                .collect(Collectors.toList());
    }
}
