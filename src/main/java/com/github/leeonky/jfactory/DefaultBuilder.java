package com.github.leeonky.jfactory;

class DefaultBuilder<T> implements Builder<T> {
    private final FactorySet factorySet;
    private final ObjectFactory<T> objectFactory;
    private final int sequence;
    private final ObjectFactorySet objectFactorySet;

    public DefaultBuilder(FactorySet factorySet, ObjectFactory<T> objectFactory, int sequence, ObjectFactorySet objectFactorySet) {
        this.factorySet = factorySet;
        this.objectFactory = objectFactory;
        this.sequence = sequence;
        this.objectFactorySet = objectFactorySet;
    }

    @Override
    public T create() {
        return new ObjectProducer<>(objectFactory, new Instance(sequence), objectFactorySet).getValue();
    }
}
