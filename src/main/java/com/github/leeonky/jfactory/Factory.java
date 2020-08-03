package com.github.leeonky.jfactory;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Factory<T> {
    Factory<T> constructor(Function<Instance<T>, T> constructor);

    Factory<T> spec(Consumer<Instance<T>> instance);

    Factory<T> spec(String name, Consumer<Instance<T>> instance);

}
