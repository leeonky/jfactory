package com.github.leeonky.jfactory;

import com.github.leeonky.util.PropertyWriter;

import java.util.HashMap;
import java.util.Map;

class ObjectProducer<T> extends Producer<T> {
    private final ObjectFactory<T> objectFactory;
    private final Instance instance;
    private final Map<String, Object> properties;
    private final Map<String, Producer<?>> children = new HashMap<>();

    public ObjectProducer(ObjectFactory<T> objectFactory, Instance instance,
                          ObjectFactorySet objectFactorySet, Map<String, Object> properties) {
        this.objectFactory = objectFactory;
        this.instance = instance;
        this.properties = properties;
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
        properties.forEach((key, value) -> {
            PropertyWriter<T> propertyWriter = objectFactory.getType().getPropertyWriter(key);
            propertyWriter.setValue(obj, propertyWriter.tryConvert(value));
        });
        return obj;
    }
}
