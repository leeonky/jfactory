package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.Optional;
import java.util.function.BiFunction;

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

    protected Producer<?> queryChild(String property) {
        return null;
    }

    public Producer<?> getChild(String property) {
        Producer<?> producer = queryChild(property);
        if (producer == null)
            producer = new ReadOnlyProducer<>(this, property);
        return producer;
    }

    public BeanClass<T> getType() {
        return type;
    }

    //TODO remove optional
    public Optional<Producer<?>> getChild(PropertyChain property) {
        return property.getProducer(this);
    }

    protected Producer<T> changeTo(Producer<T> newProducer) {
        return newProducer;
    }

    public void changeChild(PropertyChain property, BiFunction<Producer<?>, String, Producer<?>> producerGenerator) {
        String p = property.getTail();

        getChild(property.removeTail()).ifPresent(producer ->
                producer.changeChild(p, producerGenerator.apply(producer, p)));
    }

    @SuppressWarnings("unchecked")
    private <T> void changeChild(String property, Producer<T> producer) {
        Producer<T> original = (Producer<T>) getChild(property);
        addChild(property, original == null ? producer : original.changeTo(producer));
    }

    protected void processDependencies() {
    }
}
