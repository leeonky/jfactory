package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

class FactorySet {
    public final TypeSequence typeSequence = new TypeSequence();
    private final DefaultValueFactories defaultValueFactories = new DefaultValueFactories();
    private final Map<BeanClass<?>, ObjectFactory<?>> objectFactories = new HashMap<>();
    private final Map<Class<?>, SpecClassFactory<?>> specClassFactoriesWithType = new HashMap<>();
    private final Map<String, SpecClassFactory<?>> specClassFactoriesWithName = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> ObjectFactory<T> queryObjectFactory(BeanClass<T> type) {
        return (ObjectFactory<T>) objectFactories.computeIfAbsent(type,
                key -> new ObjectFactory<>(key, this));
    }

    public <T, S extends Spec<T>> void registerSpecClassFactory(Class<S> specClass) {
        Spec<T> spec = BeanClass.newInstance(specClass);
        SpecClassFactory<?> specClassFactory = specClassFactoriesWithType.computeIfAbsent(specClass,
                type -> new SpecClassFactory<>(queryObjectFactory(BeanClass.create(spec.getType())), specClass, this));
        specClassFactoriesWithName.put(spec.getName(), specClassFactory);
    }

    @SuppressWarnings("unchecked")
    public <T> SpecClassFactory<T> querySpecClassFactory(String specName) {
        return (SpecClassFactory<T>) specClassFactoriesWithName.computeIfAbsent(specName, key -> {
            throw new IllegalArgumentException("Spec `" + specName + "` not exist");
        });
    }

    @SuppressWarnings("unchecked")
    public <T> SpecClassFactory<T> querySpecClassFactory(Class<? extends Spec<T>> specClass) {
        return (SpecClassFactory<T>) specClassFactoriesWithType.computeIfAbsent(specClass, key -> {
            throw new IllegalArgumentException("Spec `" + specClass.getName() + "` not exist");
        });
    }

    public <T> Optional<DefaultValueFactory<T>> queryDefaultValueBuilder(BeanClass<T> type) {
        return defaultValueFactories.query(type.getType());
    }

    public <T> DefaultValueFactory<T> getDefaultValueBuilder(BeanClass<T> type) {
        return queryDefaultValueBuilder(type).orElseGet(() -> new DefaultValueFactories.DefaultTypeFactory<>(type));
    }

    public int nextSequence(Class<?> type) {
        return typeSequence.generate(type);
    }

    public <T, S extends Spec<T>> SpecFactory<T, S> createSpecFactory(Class<S> specClass, Consumer<S> trait) {
        S spec = BeanClass.newInstance(specClass);
        return new SpecFactory<>(queryObjectFactory(BeanClass.create(spec.getType())), spec, this, trait);
    }

    public <T> void registerDefaultValueFactory(Class<T> type, DefaultValueFactory<T> factory) {
        defaultValueFactories.register(type, factory);
    }
}
