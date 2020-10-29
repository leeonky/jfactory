package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class CollectionProducer<T, C> extends Producer<C> {
    private final ObjectFactorySet objectFactorySet;
    private final Instance<T> instance;
    private final BeanClass<T> beanType;
    private final ProducerList producerList = new ProducerList();

    public CollectionProducer(ObjectFactorySet objectFactorySet, BeanClass<T> beanType,
                              BeanClass<C> collectionType, Instance<T> instance) {
        super(collectionType);
        this.objectFactorySet = objectFactorySet;
        this.instance = instance.inCollection();
        this.beanType = beanType;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected C produce() {
        return (C) getType().createCollection(producerList.stream().map(Producer::produce).collect(Collectors.toList()));
    }

    @Override
    public Optional<Producer<?>> getChild(String property) {
        return producerList.query(property);
    }

    @Override
    public void addChild(String property, Producer<?> producer) {
        producerList.set(Integer.valueOf(property), producer, this::createPlaceholder);
    }

    private DefaultValueProducer<T, ?> createPlaceholder(Integer index) {
        return new DefaultValueProducer<>(beanType,
                getDefaultValueBuilder(getType().getElementType()), instance.element(index));
    }

    private <E> DefaultValueBuilder<E> getDefaultValueBuilder(BeanClass<E> elementType) {
        return objectFactorySet.queryDefaultValueFactory(elementType)
                .orElseGet(() -> new DefaultValueBuilders.DefaultTypeBuilder<>(elementType));
    }

    @Override
    public Producer<?> getChildOrDefault(String property) {
        return producerList.get(Integer.valueOf(property), this::createPlaceholder);
    }

    @Override
    public Map<PropertyChain, Producer<?>> getChildren() {
        Iterator<Integer> integerIterator = Stream.iterate(0, i -> i + 1).iterator();
        return producerList.stream().collect(Collectors.toMap(
                p -> PropertyChain.createChain(integerIterator.next().toString()), Function.identity()));
    }

    @Override
    protected void processDependencies() {
        //TODO add one UT for this
        producerList.stream().forEach(Producer::processDependencies);
    }

    //TODO should nested process sub link
}
