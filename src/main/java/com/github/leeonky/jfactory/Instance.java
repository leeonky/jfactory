package com.github.leeonky.jfactory;

public class Instance {
    private final int sequence;
    private final String property;

    public Instance(int sequence) {
        this(sequence, null);
    }

    private Instance(int sequence, String property) {
        this.sequence = sequence;
        this.property = property;
    }

    public int getSequence() {
        return sequence;
    }

    public Instance nested(String property) {
        return new Instance(sequence, property);
    }

    public String getProperty() {
        return property;
    }
}
