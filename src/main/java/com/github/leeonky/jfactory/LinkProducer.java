package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.List;
import java.util.Optional;

public class LinkProducer<T> extends Producer<T> {
    private final List<Producer<T>> linkedProducers;

    public LinkProducer(List<Producer<T>> linkedProducers, BeanClass<T> type) {
        super(type);
        this.linkedProducers = linkedProducers;
    }

    private Optional<Producer<T>> chooseProducer(Class<?> type) {
        // should return only one producer
        return linkedProducers.stream().filter(type::isInstance).findFirst();
    }

    @Override
    protected T produce() {
        return chooseProducer(FixedValueProducer.class).orElseGet(() ->
                linkedProducers.iterator().next()
        ).produce();
    }
}
