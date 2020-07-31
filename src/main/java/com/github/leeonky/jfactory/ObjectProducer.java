package com.github.leeonky.jfactory;

public class ObjectProducer<T> extends Producer<T> {

    private final ObjectFactory<T> objectFactory;
    private final Instance instance;

    public ObjectProducer(ObjectFactory<T> objectFactory, Instance instance) {
        this.objectFactory = objectFactory;
        this.instance = instance;
    }

    @Override
    public T produce(ObjectReference<T> reference) {
        return objectFactory.create(instance);
    }
}
