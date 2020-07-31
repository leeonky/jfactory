package com.github.leeonky.jfactory;

class ValueProducer<T> extends Producer<T> {

    private final ObjectFactory<T> objectFactory;
    private final Instance instance;

    public ValueProducer(ObjectFactory<T> objectFactory, Instance instance) {
        this.objectFactory = objectFactory;
        this.instance = instance;
    }

    @Override
    protected T produce() {
        return objectFactory.create(instance);
    }
}
