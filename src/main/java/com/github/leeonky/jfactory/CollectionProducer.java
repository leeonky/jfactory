package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.ArrayList;
import java.util.List;

class CollectionProducer<T, C> extends Producer<C> {
    private final ObjectFactorySet objectFactorySet;
    private final Instance<T> instance;
    private final BeanClass<T> beanType;
    private List<Producer<?>> children = new ArrayList<>();

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
        List<Object> list = new ArrayList<>();
        // Should not use java stream here, children.size may be changed in produce
        for (int i = 0; i < children.size(); i++)
            list.add(children.get(i).getValue());
        return (C) getType().createCollection(list);
    }

    @Override
    public Producer<?> getChild(String property) {
        return children.get(Integer.valueOf(property));
    }

    @Override
    public void addChild(String property, Producer<?> producer) {
        int intIndex = Integer.valueOf(property);
        fillCollectionWithDefaultValue(intIndex);
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

    @Override
    public Producer<?> queryOrCreateChild(String property) {
        int index = Integer.valueOf(property);
        fillCollectionWithDefaultValue(index);
        return children.get(index);
    }
}
