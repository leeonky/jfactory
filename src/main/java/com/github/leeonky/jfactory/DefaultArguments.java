package com.github.leeonky.jfactory;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.github.leeonky.jfactory.PropertyChain.propertyChain;

class DefaultArguments implements Arguments {

    final Map<PropertyChain, Object> params = new LinkedHashMap<>();

    public void merge(DefaultArguments argument) {
        params.putAll(argument.params);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <P> P param(String key) {
        return (P) params.get(propertyChain(key));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <P> P param(String key, P defaultValue) {
        return (P) params.getOrDefault(propertyChain(key), defaultValue);
    }

    @Override
    public Arguments params(String property) {
        return params(propertyChain(property));
    }

    public void put(String key, Object value) {
        put(propertyChain(key), value);
    }

    private void put(PropertyChain key, Object value) {
        params.put(key, value);
    }

    public void put(String property, String key, Object value) {
        put(propertyChain(property).concat(key), value);
    }

    public Arguments params(PropertyChain propertyChain) {
        DefaultArguments defaultArguments = new DefaultArguments();
        params.forEach((key, value) -> key.sub(propertyChain).ifPresent(p -> defaultArguments.put(p, value)));
        return defaultArguments;
    }
}
