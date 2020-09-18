package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class CollectionProducer<T, C> extends Producer<C> {
    private final ObjectFactorySet objectFactorySet;
    private final Instance<T> instance;
    private final BeanClass<T> beanType;
    private List<Producer<?>> children = new ArrayList<>();

    public CollectionProducer(ObjectFactorySet objectFactorySet, BeanClass<T> beanType, BeanClass<C> collectionType, Instance<T> instance) {
        super(collectionType);
        this.objectFactorySet = objectFactorySet;
        this.instance = instance.inCollection();
        this.beanType = beanType;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected C produce() {
        return (C) getType().createCollection(children.stream().map(Producer::produce).collect(Collectors.toList()));
    }

    @Override
    public void addChild(Object index, Producer<?> producer) {
        int intIndex = (int) index;
        fillCollectionWithDefaultValue((int) index);
        children.set(intIndex, producer);
    }

    private void fillCollectionWithDefaultValue(int index) {
        for (int i = children.size(); i <= index; i++)
            children.add(new PropertyValueProducer<>(beanType,
                    getPropertyValueBuilder(getType().getElementType()), instance.element(i)));
    }

    private <E> PropertyValueBuilder<E> getPropertyValueBuilder(BeanClass<E> elementType) {
        return objectFactorySet.queryPropertyValueFactory(elementType)
                .orElseGet(() -> new PropertyValueBuilders.DefaultValueBuilder<>(elementType));
    }

    //TODO set child
//    @Override
//    public Producer<?> getChild(Object index) {
//        int intIndex = (int) index;
//        fillCollectionWithDefaultValue(intIndex);
//        return children.get(intIndex);
//    }
}
