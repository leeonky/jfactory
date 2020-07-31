package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

public class ObjectReference<T> {
    private Producer<T> producer;

    public ObjectReference(BeanClass<T> type, Producer<T> producer) {
        this.producer = producer;
    }

    public T produce() {
        return producer.produce(this);
    }
}
