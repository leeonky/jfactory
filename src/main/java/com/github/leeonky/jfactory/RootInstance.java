package com.github.leeonky.jfactory;

import com.github.leeonky.util.PropertyWriter;

import java.util.function.Consumer;
import java.util.function.Supplier;

class RootInstance<T> implements Instance<T> {
    protected final Spec<T> spec;
    protected final DefaultArguments arguments;
    private final int sequence;
    private final ValueCache<T> valueCache = new ValueCache<>();
    private int collectionSize = 0;

    public RootInstance(int sequence, Spec<T> spec, DefaultArguments arguments) {
        this.sequence = sequence;
        this.spec = spec;
        this.arguments = arguments;
    }

    @Override
    public int getSequence() {
        return sequence;
    }

    SubInstance<T> sub(PropertyWriter<?> property) {
        return new SubInstance<>(property, sequence, spec, arguments);
    }

    @Override
    public Spec<T> spec() {
        return spec;
    }

    @Override
    public Supplier<T> reference() {
        return valueCache::getValue;
    }

    @Override
    public <P> P param(String key) {
        return arguments.param(key);
    }

    @Override
    public <P> P param(String key, P defaultValue) {
        return arguments.param(key, defaultValue);
    }

    @Override
    public Arguments params(String property) {
        return arguments.params(property);
    }

    @Override
    public Arguments params() {
        return arguments;
    }

    T cache(Supplier<T> supplier, Consumer<T> operation) {
        return valueCache.cache(supplier, operation);
    }

    public void setCollectionSize(int collectionSize) {
        this.collectionSize = collectionSize;
    }

    @Override
    public int collectionSize() {
        return collectionSize;
    }
}
