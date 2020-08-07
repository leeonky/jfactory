package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class ObjectProducer<T> extends Producer<T> {
    private final ObjectFactory<T> objectFactory;
    private final FactorySet factorySet;
    private final Instance<T> instance;
    private final Map<String, Producer<?>> children = new HashMap<>();
    private final BeanClass<T> type;

    public ObjectProducer(FactorySet factorySet, ObjectFactory<T> objectFactory, Map<String, Object> properties, Collection<String> mixIns) {
        this.objectFactory = objectFactory;
        this.factorySet = factorySet;
        instance = objectFactory.createInstance(factorySet.getTypeSequence());
        type = objectFactory.getType();
        establishProducers(factorySet, properties, mixIns);
    }

    private void establishProducers(FactorySet factorySet, Map<String, Object> properties, Collection<String> mixIns) {
        buildPropertyValueProducers(factorySet.getObjectFactorySet());
        buildProducersFromSpec(mixIns);
        buildProducerFromInputProperties(factorySet, properties);
    }

    private void buildProducerFromInputProperties(FactorySet factorySet, Map<String, Object> properties) {
        QueryExpression.createQueryExpressions(type, properties)
                .forEach(exp -> addChild(exp.getProperty(), exp.buildProducer(factorySet)));
    }

    private void buildProducersFromSpec(Collection<String> mixIns) {
        objectFactory.collectSpec(mixIns, instance);
        instance.spec().apply(this);
    }

    private void buildPropertyValueProducers(ObjectFactorySet objectFactorySet) {
        type.getPropertyWriters().forEach((name, propertyWriter) ->
                objectFactorySet.queryPropertyValueFactory(propertyWriter.getPropertyType()).ifPresent(propertyValueFactory ->
                        addChild(name, new PropertyValueProducer<>(type, propertyValueFactory, instance.nested(name)))));
    }

    public void addChild(String name, Producer<?> producer) {
        children.put(name, producer);
    }

    @Override
    protected T produce() {
        T obj = objectFactory.create(instance);
        children.forEach((property, producer) -> type.setPropertyValue(obj, property, producer.produce()));
        factorySet.getDataRepository().save(obj);
        return obj;
    }
}
