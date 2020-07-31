package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.HashMap;
import java.util.Map;

public class FactorySet {
    private final Map<Class<?>, Integer> sequences = new HashMap<>();
    private final ValueFactories valueFactories = new ValueFactories();
    private final Map<Class<?>, ObjectFactory<?>> beanFactories = new HashMap<>();

    public <T> Builder<T> type(Class<T> type) {
        return new DefaultBuilder<>(this, queryObjectFactory(type));
    }

    public <T> T create(Class<T> type) {
        return type(type).create();
    }

    @SuppressWarnings("unchecked")
    private <T> ObjectFactory<T> queryObjectFactory(Class<T> type) {
        return (ObjectFactory<T>) beanFactories.computeIfAbsent(type, this::createFactory);
    }

    private <T> ObjectFactory<T> createFactory(Class<T> type) {
        return valueFactories.get(type).orElseGet(() -> new ObjectFactory<>(BeanClass.create(type)));
    }

    public <T> int getSequence(BeanClass<T> type) {
        int sequence = sequences.getOrDefault(type.getType(), 0) + 1;
        sequences.put(type.getType(), sequence);
        return sequence;
    }
}
