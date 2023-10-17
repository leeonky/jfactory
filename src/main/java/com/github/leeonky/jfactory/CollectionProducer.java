package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;
import com.github.leeonky.util.PropertyWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.lang.Integer.max;
import static java.lang.Integer.valueOf;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

class CollectionProducer<T, C> extends Producer<C> {
    private final List<Producer<?>> children = new ArrayList<>();
    private final Function<Integer, Producer<?>> placeholderFactory;
    private final Function<String, Optional<Producer>> subDefaultValueProducerFactory;

    public CollectionProducer(BeanClass<T> parentType, BeanClass<C> collectionType,
                              SubInstance<T> instance, FactorySet factorySet) {
        super(collectionType);
        CollectionInstance<T> collection = instance.inCollection();
        BeanClass<?> elementType = collectionType.getElementType();
        placeholderFactory = index -> new DefaultValueFactoryProducer<>(parentType,
                factorySet.getDefaultValueBuilder(elementType), collection.element(index));
        subDefaultValueProducerFactory = index -> factorySet.queryDefaultValueBuilder(elementType)
                .map(builder -> new DefaultValueFactoryProducer<>(parentType, builder, collection.element(valueOf(index))));
    }

    @Override
    @SuppressWarnings("unchecked")
    protected C produce() {
        return (C) getType().createCollection(children.stream().map(Producer::produce).collect(toList()));
    }

    @Override
    public Optional<Producer<?>> child(String property) {
        int index = valueOf(property);
        if (index < 0)
            index = children.size() + index;
        return Optional.ofNullable(index < children.size() ? children.get(index) : null);
    }

    @Override
    public void setChild(String property, Producer<?> producer) {
        int index = valueOf(property);
        fillCollectionWithDefaultValue(index);
        if (index < 0)
            index = children.size() + index;
        children.set(index, producer);
    }

    public int fillCollectionWithDefaultValue(int index) {
        int changed = 0;
        if (index >= 0) {
            for (int i = children.size(); i <= index; i++, changed++)
                children.add(placeholderFactory.apply(i));
        } else {
            int count = max(children.size(), -index) - children.size();
            for (int i = 0; i < count; i++, changed++)
                children.add(i, placeholderFactory.apply(i));
        }
        return changed;
    }

    @Override
    public Producer<?> childOrDefault(String property) {
        int index = valueOf(property);
        fillCollectionWithDefaultValue(index);
        return children.get(index);
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

    @Override
    protected <T> void setupAssociation(String association, RootInstance<T> instance, ListPersistable cachedChildren) {
        children.stream().filter(ObjectProducer.class::isInstance).map(ObjectProducer.class::cast).forEach(objectProducer ->
                objectProducer.setupAssociation(association, instance, cachedChildren));

    }

    public int childrenCount() {
        return children.size();
    }
}
