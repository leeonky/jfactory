package com.github.leeonky.jfactory;

import java.util.function.Consumer;
import java.util.function.Supplier;

class ValueCache<T> {
    private T value;
    private boolean cached = false;

    public T cache(Supplier<T> supplier) {
        return cache(supplier, v -> {
        });
    }

    public T cache(Supplier<T> supplier, Consumer<T> operation) {
        if (!cached) {
            value = supplier.get();
            cached = true;
            operation.accept(value);
        }
        return value;
    }

    public T getValue() {
        if (cached)
            return value;
        throw new IllegalStateException("No value");
    }
}
