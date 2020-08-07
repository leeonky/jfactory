package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

class PropertyValueProducer<T, V> extends Producer<V> {
    private final BeanClass<T> objectType;
    private final PropertyValueBuilder<V> propertyValueBuilder;
    private final Instance<T> instance;

    public PropertyValueProducer(BeanClass<T> objectType, PropertyValueBuilder<V> propertyValueBuilder, Instance<T> instance) {
        this.objectType = objectType;
        this.propertyValueBuilder = propertyValueBuilder;
        this.instance = instance;
    }

    @Override
    protected V produce() {
        return propertyValueBuilder.create(objectType, instance);
    }
}
