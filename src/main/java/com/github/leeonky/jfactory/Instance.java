package com.github.leeonky.jfactory;

public class Instance<T> {
    private final int sequence;
    private final String property;
    private final Specification<T> specification;

    public Instance(int sequence, Specification<T> specification) {
        this(sequence, null, specification);
    }

    private Instance(int sequence, String property, Specification<T> specification) {
        this.sequence = sequence;
        this.property = property;
        this.specification = specification;
    }

    public int getSequence() {
        return sequence;
    }

    public Instance<T> nested(String property) {
        return new Instance<>(sequence, property, specification);
    }

    public String getProperty() {
        return property;
    }

    public Specification<T> spec() {
        return specification;
    }
}
