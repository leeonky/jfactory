package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static com.github.leeonky.util.BeanClass.cast;

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
        return Objects.hash(TypeProperties.class, properties);
    }

    //TODO missing compare type
    @Override
    public boolean equals(Object another) {
        return cast(another, TypeProperties.class)
                .map(typeProperties -> Objects.equals(properties, typeProperties.properties))
                .orElseGet(() -> super.equals(another));
    }
}
