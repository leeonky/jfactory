package com.github.leeonky.jfactory;

import java.util.function.Supplier;

class UnFixedValueProducer<T> extends Producer<T> {
    private final Supplier<T> supplier;

    public UnFixedValueProducer(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    protected T produce() {
        return supplier.get();
    }
}
