package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.*;
import java.util.function.BiFunction;

//TODO move to new package
abstract class Producer<T> {
    private final BeanClass<T> type;
    private final ValueCache<T> valueCache = new ValueCache<>();
    private Set<Producer<?>> cachedAllChildren;
    private boolean notChange = true;

    protected Producer(BeanClass<T> type) {
        this.type = type;
    }

    public BeanClass<T> getType() {
        return type;
    }

    protected abstract T produce();

    public T getValue() {
        return valueCache.cache(this::produce);
    }

    protected void doDependencies() {
    }

    public void addChild(String property, Producer<?> producer) {
    }

    public Optional<Producer<?>> child(String property) {
        return Optional.empty();
    }

    public Producer<?> childOrDefault(String property) {
        return child(property).orElse(null);
    }

    public Producer<?> child(PropertyChain property) {
        return property.applyAccess(this, (producer, subProperty) -> producer.child(subProperty)
                .orElseGet(() -> new ReadOnlyProducer<>(producer, subProperty)), Objects::requireNonNull);
    }

    protected Producer<T> changeTo(Producer<T> newProducer) {
        return newProducer;
    }

    public void changeChild(PropertyChain property, BiFunction<Producer<?>, String, Producer<?>> producerGenerator) {
        String lastProperty = property.tail();
        property.removeTail().applyAccess(this, Producer::childOrDefault, Optional::ofNullable).ifPresent(producer ->
                producer.changeChild(lastProperty, producerGenerator.apply(producer, lastProperty)));
    }

    @SuppressWarnings("unchecked")
    private <T> void changeChild(String property, Producer<T> producer) {
        Producer<T> original = (Producer<T>) childOrDefault(property);
        addChild(property, original == null ? producer : original.changeTo(producer));
    }

    public Map<PropertyChain, Producer<?>> children() {
        return new HashMap<>();
    }

    public Map<PropertyChain, Producer<?>> getAllChildren() {
        Map<PropertyChain, Producer<?>> allChildren = new HashMap<>();
        children().forEach((propertyChain, producer) -> {
            allChildren.put(propertyChain, producer);
            producer.getAllChildren().forEach((subChain, subProducer) -> {
                allChildren.put(propertyChain.concat(subChain), subProducer);
            });
        });
        return allChildren;
    }

    public void beforeCheckChange() {
        cachedAllChildren = new HashSet<>(getAllChildren().values());
    }

    public void checkChange() {
        notChange = Objects.equals(cachedAllChildren, new HashSet<>(getAllChildren().values()));
    }

    public boolean isNotChange() {
        return notChange;
    }
}
