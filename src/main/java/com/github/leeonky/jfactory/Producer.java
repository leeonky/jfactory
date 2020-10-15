package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.function.BiFunction;

//TODO getChild for read/write refactor
abstract class Producer<T> {
    private final BeanClass<T> type;

    Producer(BeanClass<T> type) {
        this.type = type;
    }

    public BeanClass<T> getType() {
        return type;
    }

    protected abstract T produce();

    public T getValue() {
        // TODO cache produced value
        return produce();
    }

    protected void processDependencies() {
    }

    public void addChild(String property, Producer<?> producer) {
    }

    protected Producer<?> queryOrCreateChild(String property) {
        return null;
    }

    public Producer<?> getChild(String property) {
        return null;
    }

    public Producer<?> getOrCreateChild(String property) {
        return queryOrCreateChild(property);
    }

    public Producer<?> getChild(PropertyChain property) {
        return property.getProducer(this);
    }

    protected Producer<T> changeTo(Producer<T> newProducer) {
        return newProducer;
    }

    public void changeChild(PropertyChain property, BiFunction<Producer<?>, String, Producer<?>> producerGenerator) {
        String p = property.getTail();

        property.removeTail().getProducerForCreate(this).ifPresent(producer ->
                producer.changeChild(p, producerGenerator.apply(producer, p)));
    }

    @SuppressWarnings("unchecked")
    private <T> void changeChild(String property, Producer<T> producer) {
        Producer<T> original = (Producer<T>) getOrCreateChild(property);
        addChild(property, original == null ? producer : original.changeTo(producer));
    }
}
