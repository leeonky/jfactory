package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;
import com.github.leeonky.util.PropertyWriter;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.github.leeonky.jfactory.PropertyChain.createChain;
import static java.lang.Integer.valueOf;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;

class CollectionProducer<T, C> extends Producer<C> {
    private final List<Producer<?>> children = new ArrayList<>();
    private final Function<Integer, Producer<?>> placeholderFactory;
    private final Function<String, Optional<Producer>> subDefaultValueProducerFactory;

    public CollectionProducer(BeanClass<T> parentType, BeanClass<C> collectionType,
                              SubInstance<T> instance, FactoryPool factoryPool) {
        super(collectionType);
        CollectionInstance<T> collection = instance.inCollection();
        BeanClass<?> elementType = collectionType.getElementType();
        placeholderFactory = index -> new DefaultValueProducer<>(parentType,
                factoryPool.getDefaultValueBuilder(elementType), collection.element(index));
        subDefaultValueProducerFactory = index -> factoryPool.queryDefaultValueBuilder(elementType)
                .map(builder -> new DefaultValueProducer<>(parentType, builder, collection.element(valueOf(index))));
    }

    @Override
    @SuppressWarnings("unchecked")
    protected C produce() {
        return (C) getType().createCollection(children.stream().map(Producer::produce).collect(toList()));
    }

    @Override
    public Optional<Producer<?>> child(String property) {
        int index = valueOf(property);
        return Optional.ofNullable(index < children.size() ? children.get(index) : null);
    }

    @Override
    public void addChild(String property, Producer<?> producer) {
        int intIndex = valueOf(property);
        fillCollectionWithDefaultValue(intIndex);
        children.set(intIndex, producer);
    }

    private void fillCollectionWithDefaultValue(int index) {
        for (int i = children.size(); i <= index; i++)
            children.add(placeholderFactory.apply(i));
    }

    @Override
    public Producer<?> childOrDefault(String property) {
        int index = valueOf(property);
        fillCollectionWithDefaultValue(index);
        return children.get(index);
    }

    @Override
    public Map<PropertyChain, Producer<?>> children() {
        Iterator<Integer> index = Stream.iterate(0, i -> i + 1).iterator();
        return children.stream().collect(toMap(p -> createChain(index.next().toString()), identity()));
    }

    @Override
    protected void doDependencies() {
        children.forEach(Producer::doDependencies);
    }

    @Override
    protected void doLinks(Producer<?> root, PropertyChain current) {
        range(0, children.size()).forEach(i ->
                children.get(i).doLinks(root, current.concat(String.valueOf(i))));
    }

    @Override
    public Optional<Producer> subDefaultValueProducer(PropertyWriter<?> property) {
        return subDefaultValueProducerFactory.apply(property.getName());
    }

    protected <T> void setupReverseAssociations(String association, RootInstance<T> instance, ObjectFactory<T> factory, ListPersistable cachedChildren) {
        children.stream().filter(ObjectProducer.class::isInstance).map(ObjectProducer.class::cast).forEach(objectProducer ->
                objectProducer.setupReverseAssociation(association, instance, factory, cachedChildren));

    }
}
