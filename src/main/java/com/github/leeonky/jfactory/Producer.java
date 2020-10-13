package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.Optional;
import java.util.function.BiFunction;

//TODO getChild for read/write
abstract class Producer<T> {
    private final BeanClass<T> type;

    Producer(BeanClass<T> type) {
        this.type = type;
    }

    protected abstract T produce();

    public T getValue() {
        // TODO cache produced value
        return produce();
    }

    public void addChild(String property, Producer<?> producer) {
    }

    protected Producer<?> queryOrCreateChild(String property) {
        return null;
    }

    protected Producer<?> getChild(String property) {
        return null;
    }

    public Producer<?> getOrCreateChild(String property) {
        return queryOrCreateChild(property);
    }

    public BeanClass<T> getType() {
        return type;
    }

    //TODO remove optional
    public Optional<Producer<?>> getOrCreateChild(PropertyChain property) {
        return property.getProducerForCreate(this);
    }

    public Optional<Producer<?>> getChild(PropertyChain property) {
        return property.getProducer(this);
    }

    protected Producer<T> changeTo(Producer<T> newProducer) {
        return newProducer;
    }

    public void changeChild(PropertyChain property, BiFunction<Producer<?>, String, Producer<?>> producerGenerator) {
        String p = property.getTail();

        getOrCreateChild(property.removeTail()).ifPresent(producer ->
                producer.changeChild(p, producerGenerator.apply(producer, p)));
    }

    @SuppressWarnings("unchecked")
    private <T> void changeChild(String property, Producer<T> producer) {
        Producer<T> original = (Producer<T>) getOrCreateChild(property);
        addChild(property, original == null ? producer : original.changeTo(producer));
    }

    protected void processDependencies() {
    }
}
