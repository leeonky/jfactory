package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Factory<T> {
    Factory<T> constructor(Function<Instance<T>, T> constructor);

    Factory<T> spec(Consumer<Instance<T>> instance);

    Factory<T> spec(String name, Consumer<Instance<T>> instance);

    BeanClass<T> getType();

    Factory<T> transformer(String property, Transformer transformer);
}
