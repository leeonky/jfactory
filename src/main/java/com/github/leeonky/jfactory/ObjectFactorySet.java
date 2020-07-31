package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.HashMap;
import java.util.Map;

class ObjectFactorySet {
    private final ValueFactorySet valueFactorySet = new ValueFactorySet();
    private final Map<Class<?>, ObjectFactory<?>> beanFactories = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> ObjectFactory<T> query(Class<T> type) {
        return (ObjectFactory<T>) beanFactories.computeIfAbsent(type, this::create);
    }

    private <T> ObjectFactory<T> create(Class<T> type) {
        return valueFactorySet.get(type).orElseGet(() -> new ObjectFactory<>(BeanClass.create(type)));
    }
}
