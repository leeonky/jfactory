package com.github.leeonky.jfactory;

class DefaultBuilder<T> implements Builder<T> {
    private final FactorySet factorySet;
    private final ObjectFactory<T> objectFactory;
    private final int sequence;

    public DefaultBuilder(FactorySet factorySet, ObjectFactory<T> objectFactory, int sequence) {
        this.factorySet = factorySet;
        this.objectFactory = objectFactory;
        this.sequence = sequence;
    }

    @Override
    public T create() {
        return new ObjectReference<>(objectFactory.getType(),
                new ObjectProducer<>(objectFactory, new Instance(sequence))
        ).produce();
    }
}
