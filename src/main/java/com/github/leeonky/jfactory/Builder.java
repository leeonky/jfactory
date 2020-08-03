package com.github.leeonky.jfactory;

import java.util.Collection;
import java.util.Map;

public interface Builder<T> {
    T create();

    Builder<T> property(String property, Object value);

    Builder<T> properties(Map<String, ?> properties);

    T query();

    Collection<T> queryAll();

    ObjectProducer<T> toProducer(String property);

    Builder<T> mixIn(String... mixIns);
}
