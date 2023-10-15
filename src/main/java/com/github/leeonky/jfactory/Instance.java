package com.github.leeonky.jfactory;

import java.util.function.Supplier;

public interface Instance<T> {
    int getSequence();

    Spec<T> spec();

    Supplier<T> reference();

    <P> P param(String key);

    <P> P param(String key, P defaultValue);

    Arguments params(String property);

    Arguments params();

    default int collectionSize() {
        return 0;
    }

    Object[] traitParams();

    default Object traitParam(int index) {
        return traitParams()[index];
    }
}
