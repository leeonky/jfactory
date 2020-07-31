package com.github.leeonky.jfactory;

public class FactorySet {
    private final TypeSequence typeSequence = new TypeSequence();
    private final ObjectFactorySet objectFactorySet = new ObjectFactorySet();

    public <T> Builder<T> type(Class<T> type) {
        return new DefaultBuilder<>(this, objectFactorySet.queryObjectFactory(type), typeSequence.generate(type), objectFactorySet);
    }

    public <T> T create(Class<T> type) {
        return type(type).create();
    }
}
