package com.github.leeonky.jfactory;

import java.util.function.Consumer;
import java.util.function.Supplier;

class RootInstance<T> implements Instance<T> {
    protected final int sequence;
    protected final Spec<T> spec;
    protected final DefaultArguments arguments;
    private final ValueCache<T> valueCache = new ValueCache<>();

    public RootInstance(int sequence, Spec<T> spec, DefaultArguments arguments) {
        this.sequence = sequence;
        this.spec = spec;
        this.arguments = arguments;
    }

    @Override
    public int getSequence() {
        return sequence;
    }

    public SubInstance<T> sub(String property) {
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

    public T cache(Supplier<T> supplier, Consumer<T> operation) {
        return valueCache.cache(supplier, operation);
    }
}
