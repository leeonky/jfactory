package com.github.leeonky.jfactory;

public class Instance<T> {
    private final int sequence;
    private final String property;
    private final Spec<T> spec;

    public Instance(int sequence, Spec<T> spec) {
        this(sequence, null, spec);
    }

    private Instance(int sequence, String property, Spec<T> spec) {
        this.sequence = sequence;
        this.property = property;
        this.spec = spec;
    }

    public int getSequence() {
        return sequence;
    }

    public Instance<T> nested(String property) {
        return new Instance<>(sequence, property, spec);
    }

    public String getProperty() {
        return property;
    }

    public Spec<T> spec() {
        return spec;
    }
}
