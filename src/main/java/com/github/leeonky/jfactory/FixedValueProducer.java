package com.github.leeonky.jfactory;

class FixedValueProducer<T> extends Producer<T> {
    private final T value;

    public FixedValueProducer(T value) {
        this.value = value;
    }

    @Override
    protected T produce() {
        return value;
    }
}
