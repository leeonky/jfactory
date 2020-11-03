package com.github.leeonky.jfactory;

import java.util.function.Supplier;

public interface Instance<T> {
    int getSequence();

    Spec<T> spec();

    Supplier<T> reference();
}
