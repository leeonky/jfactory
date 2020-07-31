package com.github.leeonky.jfactory;

import java.util.HashMap;
import java.util.Map;

class ObjectProducer<T> extends Producer<T> {
    private final ObjectFactory<T> objectFactory;
    private final Instance instance;
    private final Map<String, Producer<?>> children = new HashMap<>();

    public ObjectProducer(ObjectFactory<T> objectFactory, Instance instance, ObjectFactorySet objectFactorySet) {
        this.objectFactory = objectFactory;
        this.instance = instance;
        collectPropertyDefaultProducer(instance, objectFactorySet);
    }

    private void collectPropertyDefaultProducer(Instance instance, ObjectFactorySet objectFactorySet) {
        objectFactory.getProperties().forEach((name, propertyWriter) ->
                objectFactorySet.queryValueFactory(propertyWriter.getPropertyType()).ifPresent(factory ->
                        children.put(name, new ValueProducer<>(factory, instance.nested(name))))
        );
    }

    @Override
    protected T produce() {
        T obj = objectFactory.create(instance);
        children.forEach((property, producer) -> objectFactory.getType().setPropertyValue(obj, property, producer.produce()));
        return obj;
    }
}
