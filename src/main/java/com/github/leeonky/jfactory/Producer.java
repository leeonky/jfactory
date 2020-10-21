package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.*;
import java.util.function.BiFunction;

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
        return property.applyAccess(this, (producer, subProperty) -> producer.getChild(subProperty)
                .orElseGet(() -> new ReadOnlyProducer<>(producer, subProperty)), Objects::requireNonNull);
    }

    protected Producer<T> changeTo(Producer<T> newProducer) {
        return newProducer;
    }

    public void changeChild(PropertyChain property, BiFunction<Producer<?>, String, Producer<?>> producerGenerator) {
        String lastProperty = property.tail();
        property.removeTail().applyAccess(this, Producer::getChildOrDefault, Optional::ofNullable).ifPresent(producer ->
                producer.changeChild(lastProperty, producerGenerator.apply(producer, lastProperty)));
    }

    @SuppressWarnings("unchecked")
    private <T> void changeChild(String property, Producer<T> producer) {
        Producer<T> original = (Producer<T>) getChildOrDefault(property);
        addChild(property, original == null ? producer : original.changeTo(producer));
    }

    public Map<PropertyChain, Producer<?>> getChildren() {
        return new HashMap<>();
    }

    public Map<PropertyChain, Producer<?>> getAllChildren() {
        Map<PropertyChain, Producer<?>> allChildren = new HashMap<>();
        getChildren().forEach((propertyChain, producer) -> {
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
