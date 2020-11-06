package com.github.leeonky.jfactory;

import java.util.Arrays;

public class FactorySet {
    private final FactoryPool factoryPool = new FactoryPool();
    private final DataRepository dataRepository;

    public FactorySet() {
        dataRepository = new MemoryDataRepository();
    }

    public DataRepository getDataRepository() {
        return dataRepository;
    }

    public <T> Factory<T> factory(Class<T> type) {
        return factoryPool.queryObjectFactory(type);
    }

    public <T> Builder<T> type(Class<T> type) {
        return new DefaultBuilder<>(factoryPool.queryObjectFactory(type), this);
    }

    public <T> Builder<T> spec(Class<? extends Spec<T>> specClass) {
        return new DefaultBuilder<>(register(specClass), this);
    }

    public <T> SpecClassFactory<T> register(Class<? extends Spec<T>> specClass) {
        return factoryPool.registerSpecClassFactory(specClass);
    }

    public <T> Builder<T> spec(Spec<T> spec) {
        return new DefaultBuilder<>(factoryPool.createSpecFactory(spec), this);
    }

    public <T> Builder<T> spec(String... traitsAndSpec) {
        return new DefaultBuilder<T>(factoryPool.querySpecClassFactory(traitsAndSpec[traitsAndSpec.length - 1]), this)
                .trait(Arrays.copyOf(traitsAndSpec, traitsAndSpec.length - 1));
    }

    public <T> T create(Class<T> type) {
        return type(type).create();
    }

    public <T> T create(Spec<T> spec) {
        return spec(spec).create();
    }

    public <T> T create(String... traitsAndSpec) {
        return this.<T>spec(traitsAndSpec).create();
    }
}
