package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

class DefaultValueProducer<T, V> extends Producer<V> {
    private final BeanClass<T> objectType;
    private final DefaultValueBuilder<V> defaultValueBuilder;
    private final Instance<T> instance;

    public DefaultValueProducer(BeanClass<T> objectType, DefaultValueBuilder<V> defaultValueBuilder, Instance<T> instance) {
        super(BeanClass.create(defaultValueBuilder.getType()));
        this.objectType = objectType;
        this.defaultValueBuilder = defaultValueBuilder;
        this.instance = instance;
    }

    @Override
    protected V produce() {
        return defaultValueBuilder.create(objectType, instance);
    }
}
