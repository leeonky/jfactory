package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.Optional;
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

    public Optional<Producer<?>> getChild(String property) {
        return Optional.empty();
    }

    public Producer<?> getChildOrDefault(String property) {
        return getChild(property).orElse(null);
    }

    public Producer<?> getChild(PropertyChain property) {
        return property.childOf(this, producer -> producer, (producer, subProperty) ->
                producer.getChild(subProperty).orElseGet(() -> new ReadOnlyProducer<>(producer, subProperty)));
    }

    protected Producer<T> changeTo(Producer<T> newProducer) {
        return newProducer;
    }

    public void changeChild(PropertyChain property, BiFunction<Producer<?>, String, Producer<?>> producerGenerator) {
//        property.forTail(this, (lastProperty, p) -> p.getProducerForCreate(this).ifPresent(producer ->
//                producer.changeChild(lastProperty, producerGenerator.apply(producer, lastProperty)))
//        );

        property.forTail(this, (tail, producer) -> producer.changeChild(tail, producerGenerator.apply(producer, tail)));

//        String lastProperty = property.getTail();
//
//        property.removeTail().getProducerForCreate(this).ifPresent(producer ->
//                producer.changeChild(lastProperty, producerGenerator.apply(producer, lastProperty)));
    }

    @SuppressWarnings("unchecked")
    private <T> void changeChild(String property, Producer<T> producer) {
        Producer<T> original = (Producer<T>) getChildOrDefault(property);
        addChild(property, original == null ? producer : original.changeTo(producer));
    }
}
