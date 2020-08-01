package com.github.leeonky.jfactory;

class DefaultBuilder<T> implements Builder<T> {
    private final ObjectFactory<T> objectFactory;
    private final FactorySet factorySet;

    public DefaultBuilder(ObjectFactory<T> objectFactory, FactorySet factorySet) {
        this.factorySet = factorySet;
        this.objectFactory = objectFactory;
    }

    @Override
    public T create() {
        return new ObjectProducer<>(objectFactory,
                new Instance(factorySet.sequence(objectFactory.getType())),
                factorySet.getObjectFactorySet()).getValue();
    }
}
