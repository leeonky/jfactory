package com.github.leeonky.jfactory;

import java.util.function.Supplier;

public interface Instance<T> {
    int getSequence();

    Spec<T> spec();

    Supplier<T> reference();

    <P> P param(String key);

    <P> P param(String key, P defaultValue);

    Arguments params(PropertyChain propertyChain);

    // TODO
//    Arguments params(String);

    // TODO
//    Arguments params();
}
