package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

abstract class Producer<T> {
    private final BeanClass<T> type;

    Producer(BeanClass<T> type) {
        this.type = type;
    }

    protected abstract T produce();

    public T getValue() {
        return produce();
    }

    public void addChild(String property, Producer<?> producer) {
    }

    public Producer<?> getChild(String property) {
        return null;
    }

    public BeanClass<T> getType() {
        return type;
    }
}
