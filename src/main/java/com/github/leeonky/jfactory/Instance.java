package com.github.leeonky.jfactory;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class Instance<T> {
    private final int sequence;
    private final String property;
    private final Spec<T> spec;
    private final AtomicReference<T> reference;

    Instance(int sequence, Spec<T> spec) {
        this(sequence, null, spec, new AtomicReference<>());
    }

    private Instance(int sequence, String property, Spec<T> spec, AtomicReference<T> reference) {
        this.sequence = sequence;
        this.property = property;
        this.spec = spec;
        this.reference = reference;
    }

    public int getSequence() {
        return sequence;
    }

    Instance<T> nested(String property) {
        return new Instance<>(sequence, property, spec, reference);
    }

    void giveValue(T value) {
        reference.set(value);
    }

    public String getProperty() {
        return property;
    }

    public Spec<T> spec() {
        return spec;
    }

    public Supplier<T> reference() {
        return () -> reference.get();
    }
}
