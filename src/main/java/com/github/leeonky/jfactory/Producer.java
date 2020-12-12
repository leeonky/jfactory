package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;
import com.github.leeonky.util.PropertyWriter;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static com.github.leeonky.jfactory.Linker.Reference.defaultLinkerReference;
import static java.util.function.Function.identity;

//TODO too many child access method: get getOptional getOrDefault add change...
abstract class Producer<T> {
    private final BeanClass<T> type;
    private final ValueCache<T> valueCache = new ValueCache<>();

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

    public void changeChild(PropertyChain property, BiFunction<Producer<?>, String, Producer<?>> producerFactory) {
        String lastProperty = property.tail();
        property.removeTail().access(this, Producer::childOrDefault, Optional::ofNullable).ifPresent(nextToLast ->
                nextToLast.changeChild(lastProperty, producerFactory.apply(nextToLast, lastProperty)));
    }

    @SuppressWarnings("unchecked")
    public <T> void changeChild(String property, Producer<T> producer) {
        Producer<T> origin = (Producer<T>) childOrDefault(property);
        addChild(property, origin == null ? producer : origin.changeTo(producer));
    }

    public BeanClass<?> getPropertyWriterType(String property) {
        return getType().getPropertyWriter(property).getType();
    }

    public Stream<Linker.Reference<T>> allLinkerReferences(Producer<?> root, PropertyChain absoluteCurrent) {
        return Stream.of(defaultLinkerReference(root, absoluteCurrent));
    }

    public Optional<Producer> subDefaultValueProducer(PropertyWriter<?> property) {
        return Optional.empty();
    }

    public Producer<T> getLinkOrigin() {
        return this;
    }

    protected Producer<T> changeTo(Producer<T> newProducer) {
        return newProducer.changeFrom(this);
    }

    protected Producer<T> changeFrom(Producer<T> producer) {
        return this;
    }

    protected Producer<T> changeFrom(ObjectProducer<T> producer) {
        return this;
    }

    protected Producer<T> changeTo(DefaultValueProducer<T> newProducer) {
        return this;
    }

    protected <T> void setupAssociation(String association, RootInstance<T> instance, ListPersistable cachedChildren) {
    }
}
