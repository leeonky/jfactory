package com.github.leeonky.jfactory;

import java.util.Arrays;

public class FactorySet {
    private final TypeSequence typeSequence = new TypeSequence();
    private final ObjectFactorySet objectFactorySet = new ObjectFactorySet();
    private final DataRepository dataRepository;

    public FactorySet() {
        dataRepository = new HashMapDataRepository();
    }

    public ObjectFactorySet getObjectFactorySet() {
        return objectFactorySet;
    }

    public TypeSequence getTypeSequence() {
        return typeSequence;
    }

    public DataRepository getDataRepository() {
        return dataRepository;
    }

    public <T> Factory<T> factory(Class<T> type) {
        return objectFactorySet.queryObjectFactory(type);
    }

    public <T> Builder<T> type(Class<T> type) {
        return new DefaultBuilder<>(objectFactorySet.queryObjectFactory(type), this);
    }

    public <T> Builder<T> spec(Class<? extends Spec<T>> specClass) {
        return new DefaultBuilder<>(objectFactorySet.registerSpecClassFactory(specClass), this);
    }

    private <T> Builder<T> spec(Spec<T> spec) {
        return new DefaultBuilder<>(new SpecFactory<>(objectFactorySet.queryObjectFactory(spec.getType()), spec), this);
    }

    private <T> Builder<T> spec(String specName) {
        return new DefaultBuilder<>(objectFactorySet.querySpecClassFactory(specName), this);
    }

    public <T> T create(Class<T> type) {
        return type(type).create();
    }

    public <T> T create(Spec<T> spec) {
        return spec(spec).create();
    }

    public <T> T create(String... mixInsAndSpec) {
        return this.<T>spec(mixInsAndSpec[mixInsAndSpec.length - 1])
                .mixIn(Arrays.copyOf(mixInsAndSpec, mixInsAndSpec.length - 1)).create();
    }
}
