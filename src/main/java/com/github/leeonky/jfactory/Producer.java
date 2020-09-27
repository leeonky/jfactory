package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.LinkedList;
import java.util.Optional;

import static java.util.Optional.of;

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

    public Optional<Producer<?>> getChild(LinkedList<String> propertyChain) {
        if (propertyChain.isEmpty())
            return of(this);
        Producer<?> producer = getChild(propertyChain.removeFirst());

        //TODO producer maybe null
        return producer.getChild(propertyChain);
    }
}
