package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface Builder<T> {

    T create();

    BeanClass<T> getType();

    default Builder<T> property(String property, Object value) {
        return properties(new HashMap<String, Object>() {{
            put(property, value);
        }});
    }

    Builder<T> properties(Map<String, ?> properties);

    default T query() {
        return queryAll().stream().findFirst().orElse(null);
    }

    Collection<T> queryAll();

    Builder<T> traits(String... traits);

    Producer<T> createProducer();

    Builder<T> arg(String key, Object value);

    Builder<T> args(Arguments arguments);

    Builder<T> args(Map<String, ?> args);

    Builder<T> args(String property, Map<String, Object> args);

    default Builder<T> propertyValue(String property, PropertyValue value) {
        return value.setToBuilder(property, this);
    }

    default Builder<T> properties(PropertyValue value) {
        return value.setToBuilder("", this);
    }
}
