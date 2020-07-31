package com.github.leeonky.jfactory;

import java.util.function.Function;

public interface Factory<T> {
    Factory<T> constructor(Function<Instance, T> constructor);
}
