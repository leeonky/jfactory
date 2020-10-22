package com.github.leeonky.jfactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface Builder<T> {
    T create();

    default Builder<T> property(String property, Object value) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(property, value);
        return properties(properties);
    }

    Builder<T> properties(Map<String, ?> properties);

    T query();

    Collection<T> queryAll();

    Builder<T> mixIn(String... mixIns);

    Producer<T> createProducer(String property, boolean intently);
}
