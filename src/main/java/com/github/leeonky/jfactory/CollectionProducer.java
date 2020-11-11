package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.github.leeonky.jfactory.PropertyChain.createChain;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

class CollectionProducer<T, C> extends Producer<C> {
    private final List<Producer<?>> children = new ArrayList<>();
    private final Function<Integer, Producer<?>> placeholderFactory;
    private final CollectionInstance collection;

    public CollectionProducer(BeanClass<T> parentType, BeanClass<C> collectionType,
                              SubInstance instance, DefaultValueBuilder<?> valueBuilder) {
        super(collectionType);
        collection = instance.inCollection();
        placeholderFactory = index -> new DefaultValueProducer<>(parentType, valueBuilder, collection.element(index));
    }

    @Override
    @SuppressWarnings("unchecked")
    protected C produce() {
        return (C) getType().createCollection(children.stream().map(Producer::produce).collect(toList()));
    }

    @Override
    public Optional<Producer<?>> child(String property) {
        return Optional.ofNullable(children.get(Integer.valueOf(property)));
    }

    @Override
    public void addChild(String property, Producer<?> producer) {
        int intIndex = Integer.valueOf(property);
        fillCollectionWithDefaultValue(intIndex);
        children.set(intIndex, producer);
    }

    private void fillCollectionWithDefaultValue(int index) {
        for (int i = children.size(); i <= index; i++)
            children.add(placeholderFactory.apply(i));
    }

    @Override
    public Producer<?> childOrDefault(String property) {
        int index = Integer.valueOf(property);
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
    protected void doLinks() {
        children.forEach(Producer::doLinks);
    }
}
