package com.github.leeonky.jfactory;

import com.github.leeonky.util.PropertyWriter;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class FactorySet {
    private final FactoryPool factoryPool = new FactoryPool();
    private final DataRepository dataRepository;
    private final Set<Predicate<PropertyWriter<?>>> ignoreDefaultValues = new LinkedHashSet<>();

    public FactorySet() {
        dataRepository = new MemoryDataRepository();
    }

    public FactorySet(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
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

    public <T, S extends Spec<T>> Builder<T> from(Class<S> specClass) {
        return new DefaultBuilder<>(register(specClass), this);
    }

    public <T, S extends Spec<T>> Builder<T> spec(Class<S> specClass, Consumer<S> trait) {
        return new DefaultBuilder<>(factoryPool.createSpecFactory(specClass, trait), this);
    }

    public <T> SpecClassFactory<T> register(Class<? extends Spec<T>> specClass) {
        return factoryPool.registerSpecClassFactory(specClass);
    }

    public <T> Builder<T> from(String... traitsAndSpec) {
        return new DefaultBuilder<T>(factoryPool.querySpecClassFactory(traitsAndSpec[traitsAndSpec.length - 1]), this)
                .trait(Arrays.copyOf(traitsAndSpec, traitsAndSpec.length - 1));
    }

    public <T> T create(Class<T> type) {
        return type(type).create();
    }

    public <T, S extends Spec<T>> T createFrom(Class<S> spec) {
        return from(spec).create();
    }

    public <T, S extends Spec<T>> T createFrom(Class<S> spec, Consumer<S> trait) {
        return spec(spec, trait).create();
    }

    public <T> T create(String... traitsAndSpec) {
        return this.<T>from(traitsAndSpec).create();
    }

    public <T> FactorySet registerDefaultValueFactory(Class<T> type, DefaultValueFactory<T> factory) {
        factoryPool.registerDefaultValueFactory(type, factory);
        return this;
    }

    public FactorySet ignoreDefaultValue(Predicate<PropertyWriter<?>> ignoreProperty) {
        ignoreDefaultValues.add(ignoreProperty);
        return this;
    }

    <T> boolean shouldCreateDefaultValue(PropertyWriter<T> propertyWriter) {
        return ignoreDefaultValues.stream().noneMatch(p -> p.test(propertyWriter));
    }
}
