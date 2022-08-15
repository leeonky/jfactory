package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

class DependencyProducer<T> extends Producer<T> {
    private final List<Supplier<Object>> dependencies;
    private final Function<Object[], T> rule;

    public DependencyProducer(BeanClass<T> type, List<Supplier<Object>> dependencies, Function<Object[], T> rule) {
        super(type);
        this.dependencies = dependencies;
        this.rule = rule;
    }

    @Override
    protected T produce() {
        return rule.apply(dependencies.stream().map(Supplier::get).toArray());
    }

    @Override
    protected Producer<T> changeFrom(ObjectProducer<T> producer) {
        if (producer.isFixed())
            return producer;
        return super.changeFrom(producer);
    }
}
