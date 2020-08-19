package com.github.leeonky.jfactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class ObjectProducer<T> extends Producer<T> {
    private final ObjectFactory<T> objectFactory;
    private final FactorySet factorySet;
    private final Instance<T> instance;
    private final Map<String, Producer<?>> children = new HashMap<>();

    public ObjectProducer(FactorySet factorySet, ObjectFactory<T> objectFactory, Map<String, Object> properties, Collection<String> mixIns) {
        super(objectFactory.getType());
        this.objectFactory = objectFactory;
        this.factorySet = factorySet;
        instance = objectFactory.createInstance(factorySet.getTypeSequence());
        establishProducers(factorySet, properties, mixIns);
    }

    private void establishProducers(FactorySet factorySet, Map<String, Object> properties, Collection<String> mixIns) {
        buildPropertyValueProducers(factorySet.getObjectFactorySet());
        buildProducersFromSpec(mixIns);
        buildProducerFromInputProperties(factorySet, properties);
    }

    private void buildProducerFromInputProperties(FactorySet factorySet, Map<String, Object> properties) {
        QueryExpression.createQueryExpressions(getType(), properties)
                .forEach((p, exp) -> addChild(p, exp.buildProducer(factorySet, this, instance.nested(p))));
    }

    private void buildProducersFromSpec(Collection<String> mixIns) {
        objectFactory.collectSpec(mixIns, instance);
        instance.spec().apply(factorySet, this);
    }

    private void buildPropertyValueProducers(ObjectFactorySet objectFactorySet) {
        getType().getPropertyWriters().forEach((name, propertyWriter) ->
                objectFactorySet.queryPropertyValueFactory(propertyWriter.getType()).ifPresent(propertyValueFactory ->
                        addChild(name, new PropertyValueProducer<>(getType(), propertyValueFactory, instance.nested(name)))));
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
