package com.github.leeonky.jfactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static java.util.Collections.emptyList;

public class Instance<T> {
    private final int sequence;
    private final String property;
    private final Spec<T> spec;
    private final AtomicReference<T> reference;
    private final List<Integer> indexes;

    Instance(int sequence, Spec<T> spec) {
        this(sequence, null, spec, new AtomicReference<>(), emptyList());
    }

    private Instance(int sequence, String property, Spec<T> spec, AtomicReference<T> reference, List<Integer> indexes) {
        this.sequence = sequence;
        this.property = property;
        this.spec = spec;
        this.reference = reference;
        this.indexes = new ArrayList<>(indexes);
    }

    public int getSequence() {
        return sequence;
    }

    Instance<T> nested(String property) {
        return new Instance<>(sequence, property, spec, reference, indexes);
    }

    Instance<T> element(int index) {
        Instance<T> instance = new Instance<>(sequence, property, spec, reference, indexes);
        instance.indexes.set(instance.indexes.size() - 1, index);
        return instance;
    }

    void giveValue(T value) {
        reference.set(value);
    }

    public String getProperty() {
        return property;
    }

    public List<Integer> getIndexes() {
        return indexes;
    }

    public Spec<T> spec() {
        return spec;
    }

    public Supplier<T> reference() {
        return reference::get;
    }

    public Instance<T> inCollection() {
        Instance<T> instance = new Instance<>(sequence, property, spec, reference, indexes);
        instance.indexes.add(0);
        return instance;
    }
}
