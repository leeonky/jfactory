package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

class FixedValueProducer<T> extends Producer<T> {
    private final T value;

    public FixedValueProducer(BeanClass<T> type, T value) {
        super(type);
        this.value = value;
    }

    @Override
    protected T produce() {
        return value;
    }

    @Override
    protected Producer<T> changeTo(Producer<T> newProducer) {
        //TODO link producer may changeTo
        return this;
    }
}
