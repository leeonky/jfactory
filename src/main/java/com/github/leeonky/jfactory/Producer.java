package com.github.leeonky.jfactory;

abstract class Producer<T> {
    protected abstract T produce();

    public T getValue() {
        return produce();
    }
}
