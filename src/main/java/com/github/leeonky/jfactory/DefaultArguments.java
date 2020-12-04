package com.github.leeonky.jfactory;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.github.leeonky.jfactory.PropertyChain.createChain;

class DefaultArguments implements Arguments {

    final Map<PropertyChain, Object> params = new LinkedHashMap<>();

    public void merge(DefaultArguments argument) {
        params.putAll(argument.params);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <P> P param(String key) {
        return (P) params.get(createChain(key));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <P> P param(String key, P defaultValue) {
        return (P) params.getOrDefault(createChain(key), defaultValue);
    }

    @Override
    public Arguments params(String property) {
        return params(createChain(property));
    }

    public void put(String key, Object value) {
        put(createChain(key), value);
    }

    private void put(PropertyChain key, Object value) {
        params.put(key, value);
    }

    public void put(String property, String key, Object value) {
        put(createChain(property).concat(key), value);
    }

    public Arguments params(PropertyChain propertyChain) {
        DefaultArguments defaultArguments = new DefaultArguments();
        params.forEach((key, value) -> key.sub(propertyChain).ifPresent(p -> defaultArguments.put(p, value)));
        return defaultArguments;
    }
}
