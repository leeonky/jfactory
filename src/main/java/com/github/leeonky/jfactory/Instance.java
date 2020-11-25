package com.github.leeonky.jfactory;

import java.util.function.Supplier;

public interface Instance<T> {
    int getSequence();

    Spec<T> spec();

    Supplier<T> reference();

    <P> P param(String key);

    //TODO support namespace
    <P> P param(String key, P defaultValue);
}
