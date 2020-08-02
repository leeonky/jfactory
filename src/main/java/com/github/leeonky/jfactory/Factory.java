package com.github.leeonky.jfactory;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Factory<T> {
    Factory<T> constructor(Function<Instance<T>, T> constructor);

    Factory<T> specification(Consumer<Instance<T>> instance);
}
