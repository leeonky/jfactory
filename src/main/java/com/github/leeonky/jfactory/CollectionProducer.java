package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;
import com.github.leeonky.util.PropertyWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class CollectionProducer<T, C> extends Producer<C> {
    private final ObjectFactorySet objectFactorySet;
    private final PropertyWriter<T> propertyWriter;
    private final Instance<T> instance;
    private List<Producer<?>> children = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public CollectionProducer(ObjectFactorySet objectFactorySet, PropertyWriter<T> propertyWriter, Instance<T> instance) {
        // TODO BeanClass can get generic type params
        super((BeanClass<C>) propertyWriter.getPropertyTypeWrapper());
        this.objectFactorySet = objectFactorySet;
        this.propertyWriter = propertyWriter;
        this.instance = instance.inCollection();
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
            children.add(new PropertyValueProducer<>(propertyWriter.getBeanClass(),
                    getPropertyValueBuilder(propertyWriter.getElementType()),
                    instance.element(i)));
    }

    private <E> PropertyValueBuilder<E> getPropertyValueBuilder(Class<E> elementType) {
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
