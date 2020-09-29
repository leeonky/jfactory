package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

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

    protected Producer<T> changeTo(Producer<T> newProducer) {
        return newProducer;
    }

    public void changeChild(List<String> property, BiFunction<Producer<?>, String, Producer<?>> producerGenerator) {
        LinkedList<String> linkedProperty = new LinkedList<>(property);
        String p = linkedProperty.removeLast();

        // TODO property in sub is readonly: no producer
        Producer<?> producer = getChild(linkedProperty).get();

        producer.changeChild(p, producerGenerator.apply(producer, p));
    }

    @SuppressWarnings("unchecked")
    private <T> void changeChild(String property, Producer<T> producer) {
        Producer<T> original = (Producer<T>) getChild(property);
        addChild(property, original == null ? producer : original.changeTo(producer));
    }
}
