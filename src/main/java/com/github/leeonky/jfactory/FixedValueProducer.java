package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

class FixedValueProducer<T> extends Producer<T> {
    private final T value;

    public FixedValueProducer(BeanClass<T> type, Object value) {
        super(type);
        this.value = BeanClass.getConverter().convert(type.getType(), value);
    }

    @Override
    protected T produce() {
        return value;
    }

    @Override
    public Producer<T> changeTo(Producer<T> newProducer) {
        if (newProducer instanceof FixedValueProducer)
            return newProducer;
        return this;
    }
}
