package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class ObjectFactorySet {
    private final ValueFactorySet valueFactorySet = new ValueFactorySet();
    private final Map<Class<?>, ObjectFactory<?>> objectFactories = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> ObjectFactory<T> queryObjectFactory(Class<T> type) {
        return (ObjectFactory<T>) objectFactories.computeIfAbsent(type, this::create);
    }

    private <T> ObjectFactory<T> create(Class<T> type) {
        return valueFactorySet.get(type).orElseGet(() -> new ObjectFactory<>(BeanClass.create(type)));
    }

    public <T> Optional<ObjectFactory<T>> queryValueFactory(Class<T> type) {
        return valueFactorySet.get(type);
    }
}
