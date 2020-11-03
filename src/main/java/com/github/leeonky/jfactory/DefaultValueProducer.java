package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

class DefaultValueProducer<T, V> extends Producer<V> {
    private final BeanClass<T> beanType;
    private final DefaultValueBuilder<V> builder;
    private final RootInstance<T>.Sub instance;

    public DefaultValueProducer(BeanClass<T> beanType, DefaultValueBuilder<V> builder, RootInstance<T>.Sub instance) {
        super(BeanClass.create(builder.getType()));
        this.beanType = beanType;
        this.builder = builder;
        this.instance = instance;
    }

    @Override
    protected V produce() {
        return builder.create(beanType, instance);
    }
}
