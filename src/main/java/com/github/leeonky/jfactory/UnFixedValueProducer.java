package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.function.Supplier;

class UnFixedValueProducer<T> extends Producer<T> {
    private final Supplier<T> supplier;

    public UnFixedValueProducer(Supplier<T> supplier, BeanClass<T> type) {
        super(type);
        this.supplier = supplier;
    }

    @Override
    protected T produce() {
        return supplier.get();
    }
}
