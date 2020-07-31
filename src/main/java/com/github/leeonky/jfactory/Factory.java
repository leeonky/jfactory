package com.github.leeonky.jfactory;

import java.util.function.Function;

public interface Factory<T> {
    Factory<T> construct(Function<Instance, T> constructor);
}
