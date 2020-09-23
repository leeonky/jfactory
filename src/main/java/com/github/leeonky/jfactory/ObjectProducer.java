package com.github.leeonky.jfactory;

import java.util.HashMap;
import java.util.Map;

class ObjectProducer<T> extends Producer<T> {
    private final ObjectFactory<T> objectFactory;
    private final FactorySet factorySet;
    private final Instance<T> instance;
    private final Map<String, Producer<?>> children = new HashMap<>();

    public ObjectProducer(FactorySet factorySet, ObjectFactory<T> objectFactory, DefaultBuilder<T> builder) {
        super(objectFactory.getType());
        this.objectFactory = objectFactory;
        this.factorySet = factorySet;
        instance = objectFactory.createInstance(factorySet.getTypeSequence());
        establishProducers(factorySet, builder);
    }

    private void establishProducers(FactorySet factorySet, DefaultBuilder<T> builder) {
        buildPropertyValueProducers(factorySet.getObjectFactorySet());
        buildProducersFromSpec(builder);
        buildProducerFromInputProperties(factorySet, builder);
    }

    private void buildProducerFromInputProperties(FactorySet factorySet, DefaultBuilder<T> builder) {
        builder.toExpressions().forEach((p, exp) -> addChild(p, exp.buildProducer(factorySet, instance.sub(p))));
    }

    private void buildProducersFromSpec(DefaultBuilder<T> builder) {
        builder.collectSpec(instance);
        instance.spec().apply(factorySet, this);
    }

    private void buildPropertyValueProducers(ObjectFactorySet objectFactorySet) {
        getType().getPropertyWriters().forEach((name, propertyWriter) ->
                objectFactorySet.queryPropertyValueFactory(propertyWriter.getType()).ifPresent(propertyValueFactory ->
                        addChild(name, new PropertyValueProducer<>(getType(), propertyValueFactory, instance.sub(name)))));
    }

    @Override
    public void addChild(Object name, Producer<?> producer) {
        children.put((String) name, producer);
    }

    @Override
    public Producer<?> getChild(Object index) {
        return children.get(index);
    }

    @Override
    protected T produce() {
        T obj = objectFactory.create(instance);
        instance.giveValue(obj);
        children.forEach((property, producer) -> getType().setPropertyValue(obj, property, producer.produce()));
        factorySet.getDataRepository().save(obj);
        return obj;
    }
}
