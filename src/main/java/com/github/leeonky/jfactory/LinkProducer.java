package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.List;

public class LinkProducer<T> extends Producer<T> {
    private final List<Producer<T>> linkedProducers;

    public LinkProducer(List<Producer<T>> linkedProducers, BeanClass<T> type) {
        super(type);
        this.linkedProducers = linkedProducers;
    }

    @Override
    protected T produce() {
        //TODO choose producer by priority
        return linkedProducers.iterator().next().produce();
    }
}
