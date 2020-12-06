package com.github.leeonky.jfactory;

import com.github.leeonky.util.PropertyWriter;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

class RootInstance<T> implements Instance<T> {
    protected final int sequence;
    protected final Spec<T> spec;
    protected final DefaultArguments arguments;
    private final ValueCache<T> valueCache = new ValueCache<>();

    public RootInstance(int sequence, Spec<T> spec, DefaultArguments arguments) {
        this.sequence = sequence;
        this.spec = Objects.requireNonNull(spec).setInstance(this);
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
}
