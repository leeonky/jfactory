package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.github.leeonky.util.BeanClass.cast;
import static java.util.Objects.hash;

//TODO to be instead by KeyValueCollection
@Deprecated
class TypeProperties<T> {
    private final KeyValueCollection keyValueCollection = new KeyValueCollection();

    private final BeanClass<T> type;

    public TypeProperties(BeanClass<T> type) {
        this.type = type;
    }

    public void merge(TypeProperties<T> another) {
        keyValueCollection.merge(another.keyValueCollection);
    }

    public void putAll(Map<String, ?> properties) {
        properties.forEach(keyValueCollection::add);
    }

    @Override
    public int hashCode() {
        return hash(TypeProperties.class, type, keyValueCollection);
    }

    @Override
    public boolean equals(Object another) {
        return cast(another, TypeProperties.class)
                .map(typeProperties -> Objects.equals(keyValueCollection, typeProperties.keyValueCollection)
                        && Objects.equals(type, typeProperties.type))
                .orElseGet(() -> super.equals(another));
    }

    public Collection<T> select(Collection<T> data) {
        Collection<PropertyExpression<T>> expressions = toExpressions();
        return data.stream().filter(o -> expressions.stream().allMatch(e -> e.isMatch(o))).collect(Collectors.toList());
    }

    public Collection<PropertyExpression<T>> toExpressions() {
        return keyValueCollection.parseExpressions(type)
                .collect(Collectors.groupingBy(PropertyExpression::getProperty)).values().stream()
                .map(expressions -> expressions.stream().reduce(PropertyExpression::merge).get())
                .collect(Collectors.toList());
    }
}
