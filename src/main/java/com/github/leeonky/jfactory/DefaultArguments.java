package com.github.leeonky.jfactory;

import java.util.LinkedHashMap;
import java.util.Map;

//TODO should pass argument to all builder in creation
class DefaultArguments implements Arguments {

    //TODO support namespace
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
}
