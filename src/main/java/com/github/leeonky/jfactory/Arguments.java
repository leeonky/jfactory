package com.github.leeonky.jfactory;

public interface Arguments {
    <P> P param(String key);

    <P> P param(String key, P defaultValue);

    Arguments params(String property);
}