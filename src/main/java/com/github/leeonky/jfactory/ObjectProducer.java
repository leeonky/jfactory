package com.github.leeonky.jfactory;

import java.util.HashMap;
import java.util.Map;

class ObjectProducer<T> extends Producer<T> {
    private final ObjectFactory<T> objectFactory;
    private final FactorySet factorySet;
    private final Instance instance;
    private final Map<String, Producer<?>> children = new HashMap<>();

    public ObjectProducer(ObjectFactory<T> objectFactory, Map<String, Object> properties, FactorySet factorySet) {
        this.objectFactory = objectFactory;
        this.factorySet = factorySet;
        instance = new Instance(factorySet.sequence(objectFactory.getType()));
        collectPropertyDefaultProducer(factorySet.getObjectFactorySet());
        QueryExpression.createQueryExpressions(objectFactory.getType(), properties)
                .forEach(exp -> addChild(exp.getProperty(), exp.buildProducer(factorySet)));
    }

    private void collectPropertyDefaultProducer(ObjectFactorySet objectFactorySet) {
        objectFactory.getProperties().forEach((name, propertyWriter) ->
                objectFactorySet.queryValueFactory(propertyWriter.getPropertyType()).ifPresent(factory ->
                        addChild(name, new ValueProducer<>(factory, instance.nested(name)))
                ));
    }

    public void addChild(String name, Producer<?> producer) {
        children.put(name, producer);
    }

    @Override
    protected T produce() {
        T obj = objectFactory.create(instance);
        children.forEach((property, producer) -> objectFactory.getType().setPropertyValue(obj, property, producer.produce()));
        factorySet.getDataRepository().save(obj);
        return obj;
    }
}
