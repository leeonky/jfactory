package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;
import com.github.leeonky.util.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class CollectionProducer<T, C> extends Producer<C> {
    private final ObjectFactorySet objectFactorySet;
    private final Instance<T> instance;
    private final BeanClass<T> beanType;
    private List<Producer<?>> children = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public CollectionProducer(ObjectFactorySet objectFactorySet, Property<T> property, Instance<T> instance) {
        super((BeanClass<C>) property.getType());
        this.objectFactorySet = objectFactorySet;
        this.instance = instance.inCollection();
        beanType = property.getBeanType();
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
