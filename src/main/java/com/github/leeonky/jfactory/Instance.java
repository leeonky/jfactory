package com.github.leeonky.jfactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Collections.emptyList;

public class Instance<T> {
    private final int sequence;
    private final String property;
    private final Spec<T> spec;
    private final List<Integer> indexes;
    private final ValueCache<T> valueCache;

    Instance(int sequence, Spec<T> spec) {
        this(sequence, null, spec, emptyList(), new ValueCache<>());
    }

    private Instance(int sequence, String property, Spec<T> spec,
                     List<Integer> indexes, ValueCache<T> valueCache) {
        this.sequence = sequence;
        this.property = property;
        this.spec = spec;
        this.indexes = new ArrayList<>(indexes);
        this.valueCache = valueCache;
    }

    public int getSequence() {
        return sequence;
    }

    Instance<T> sub(String property) {
        return new Instance<>(sequence, property, spec, indexes, valueCache);
    }

    Instance<T> element(int index) {
        Instance<T> instance = new Instance<>(sequence, property, spec, indexes, valueCache);
        instance.indexes.set(instance.indexes.size() - 1, index);
        return instance;
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
        return valueCache::getValue;
    }

    public Instance<T> inCollection() {
        Instance<T> instance = new Instance<>(sequence, property, spec, indexes, valueCache);
        instance.indexes.add(0);
        return instance;
    }

    ValueCache<T> getValueCache() {
        return valueCache;
    }
}
