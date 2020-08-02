package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

public class FactorySet {
    private final TypeSequence typeSequence = new TypeSequence();
    private final ObjectFactorySet objectFactorySet = new ObjectFactorySet();
    private final DataRepository dataRepository;

    public FactorySet() {
        dataRepository = new HashMapDataRepository();
    }

    public <T> Builder<T> type(Class<T> type) {
        return new DefaultBuilder<>(objectFactorySet.queryObjectFactory(type), this);
    }

    ObjectFactorySet getObjectFactorySet() {
        return objectFactorySet;
    }

    <T> int sequence(BeanClass<T> type) {
        return typeSequence.generate(type.getType());
    }

    public <T> T create(Class<T> type) {
        return type(type).create();
    }

    public void clearRepo() {
        dataRepository.clear();
    }

    public DataRepository getDataRepository() {
        return dataRepository;
    }

    public <T> Factory<T> factory(Class<T> type) {
        return objectFactorySet.queryObjectFactory(type);
    }
}
