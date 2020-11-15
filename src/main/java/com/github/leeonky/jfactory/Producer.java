package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static com.github.leeonky.jfactory.Linker.Reference.defaultLinkerReference;
import static java.util.function.Function.identity;

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

    protected void doLinks(Producer<?> root, PropertyChain current) {
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
        return property.access(this, (producer, subProperty) -> producer.child(subProperty)
                .orElseGet(() -> new ReadOnlyProducer<>(producer, subProperty)), identity());
    }

    protected Producer<T> changeTo(Producer<T> newProducer) {
        return newProducer;
    }

    public void changeChild(PropertyChain property, BiFunction<Producer<?>, String, Producer<?>> producerFactory) {
        String lastProperty = property.tail();
        property.removeTail().access(this, Producer::childOrDefault, Optional::ofNullable).ifPresent(nextToLast ->
                nextToLast.changeChild(lastProperty, producerFactory.apply(nextToLast, lastProperty)));
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
            producer.getAllChildren().forEach((subChain, subProducer) ->
                    allChildren.put(propertyChain.concat(subChain), subProducer));
        });
        return allChildren;
    }

    public void beforeCheckChange() {
        cachedAllChildren = new LinkedHashSet<>(getAllChildren().values());
    }

    public void checkChange() {
        notChange = notChange && Objects.equals(cachedAllChildren, new LinkedHashSet<>(getAllChildren().values()));
    }

    public boolean isNotChange() {
        return notChange;
    }

    public BeanClass<?> getPropertyWriterType(String property) {
        return getType().getPropertyWriter(property).getType();
    }

    public Stream<Linker.Reference<T>> allLinkerReferences(Producer<?> root, PropertyChain absoluteCurrent) {
        return Stream.of(defaultLinkerReference(this, root, absoluteCurrent));
    }

    public Optional<Producer> subDefaultValueProducer(String property) {
        return Optional.empty();
    }

    public Producer<T> getLinkOrigin() {
        return this;
    }
}
