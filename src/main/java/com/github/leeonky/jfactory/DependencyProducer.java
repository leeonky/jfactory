package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

class DependencyProducer<T> extends Producer<T> {
    private final List<Supplier<Object>> dependencies;
    private final Function<Object[], T> function;

    public DependencyProducer(List<Supplier<Object>> dependencies, Function<Object[], T> function, BeanClass<T> type) {
        super(type);
        this.dependencies = dependencies;
        this.function = function;
    }

    @Override
    protected T produce() {
        return function.apply(dependencies.stream().map(Supplier::get).toArray());
    }
}
