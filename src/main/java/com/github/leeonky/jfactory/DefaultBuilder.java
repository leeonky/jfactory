package com.github.leeonky.jfactory;

class DefaultBuilder<T> implements Builder<T> {
    private final FactorySet factorySet;
    private final ObjectFactory<T> objectFactory;

    public DefaultBuilder(FactorySet factorySet, ObjectFactory<T> objectFactory) {
        this.factorySet = factorySet;
        this.objectFactory = objectFactory;
    }

    @Override
    public T create() {
        return new ObjectReference<>(objectFactory.getType(),
                new ObjectProducer<>(objectFactory, new Instance(factorySet.getSequence(objectFactory.getType())))
        ).produce();
    }
}
