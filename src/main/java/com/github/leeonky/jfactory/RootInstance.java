package com.github.leeonky.jfactory;

import java.util.function.Consumer;
import java.util.function.Supplier;

class RootInstance<T> implements Instance<T> {
    protected final int sequence;
    protected final Spec<T> spec;
    private final ValueCache<T> valueCache = new ValueCache<>();

    public RootInstance(int sequence, Spec<T> spec) {
        this.sequence = sequence;
        this.spec = spec;
    }

    @Override
    public int getSequence() {
        return sequence;
    }

    public SubInstance<T> sub(String property) {
        return new SubInstance<>(property, sequence, spec);
    }

    @Override
    public Spec<T> spec() {
        return spec;
    }

    @Override
    public Supplier<T> reference() {
        return valueCache::getValue;
    }

    public T cache(Supplier<T> supplier, Consumer<T> operation) {
        return valueCache.cache(supplier, operation);
    }
}
