package com.github.leeonky.jfactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static com.github.leeonky.jfactory.PropertyChain.createChain;

class DefaultArguments implements Arguments {

    //TODO use PropertyChain
    final Map<String, Object> params = new LinkedHashMap<>();

    public void merge(DefaultArguments argument) {
        params.putAll(argument.params);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <P> P param(String key) {
        return (P) params.get(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <P> P param(String key, P defaultValue) {
        return (P) params.getOrDefault(key, defaultValue);
    }

    public void put(String key, Object value) {
        params.put(key, value);
    }

    public void put(String property, String key, Object value) {
        params.put(property + "." + key, value);
    }

    public Arguments params(PropertyChain propertyChain) {
        DefaultArguments defaultArguments = new DefaultArguments();
        params.forEach((key, value) -> {
            PropertyChain chain = createChain(key);
            Optional<PropertyChain> subKey = chain.sub(propertyChain);
            subKey.ifPresent(p -> defaultArguments.put(p.toString(), value));
        });
        return defaultArguments;
    }
}
