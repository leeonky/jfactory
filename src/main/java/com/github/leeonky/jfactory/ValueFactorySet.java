package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class ValueFactorySet {
    private final Map<Class<?>, Factory<?>> buildIns = new HashMap<Class<?>, Factory<?>>() {{
        put(String.class, new ValueFactory<>(String.class).constructor(instance ->
                String.format("%s#%d", instance.getProperty() == null ? "string" : instance.getProperty(), instance.getSequence())));
    }};

    @SuppressWarnings("unchecked")
    public <T> Optional<ObjectFactory<T>> get(Class<T> type) {
        return Optional.ofNullable((ObjectFactory<T>) buildIns.get(type));
    }

    public static class ValueFactory<T> extends ObjectFactory<T> {
        public ValueFactory(Class<T> type) {
            super(BeanClass.create(type));
        }
    }
}
