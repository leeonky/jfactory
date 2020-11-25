package com.github.leeonky.jfactory;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

class RootInstance<T> implements Instance<T> {
    protected final int sequence;
    protected final Spec<T> spec;
    protected final Map<String, Object> params;
    private final ValueCache<T> valueCache = new ValueCache<>();

    public RootInstance(int sequence, Spec<T> spec, Map<String, Object> params) {
        this.sequence = sequence;
        this.spec = spec;
        this.params = params;
    }

    @Override
    public int getSequence() {
        return sequence;
    }

    public SubInstance<T> sub(String property) {
        return new SubInstance<>(property, sequence, spec, params);
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
    @SuppressWarnings("unchecked")
    public <P> P param(String key) {
        return (P) params.get(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <P> P param(String key, P defaultValue) {
        return (P) params.getOrDefault(key, defaultValue);
    }

    public T cache(Supplier<T> supplier, Consumer<T> operation) {
        return valueCache.cache(supplier, operation);
    }
}
