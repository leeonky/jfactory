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

    public <T> Builder<T> spec(Class<? extends Spec<T>> specClass) {
        return new DefaultBuilder<>(
                new SpecClassFactory<>(objectFactorySet.queryObjectFactory(BeanClass.newInstance(specClass).getType()), specClass), this);
    }

    public <T> T create(Spec<T> spec) {
        return spec(spec).create();
    }

    private <T> Builder<T> spec(Spec<T> spec) {
        return new DefaultBuilder<>(new SpecFactory<>(objectFactorySet.queryObjectFactory(spec.getType()), spec), this);
    }
}
