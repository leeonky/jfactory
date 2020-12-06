package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

class FactoryPool {
    public final TypeSequence typeSequence = new TypeSequence();
    private final DefaultValueFactories defaultValueFactories = new DefaultValueFactories();
    private final Map<Class<?>, ObjectFactory<?>> objectFactories = new HashMap<>();
    private final Map<Class<?>, SpecClassFactory<?>> specClassFactoriesWithType = new HashMap<>();
    private final Map<String, SpecClassFactory<?>> specClassFactoriesWithName = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> ObjectFactory<T> queryObjectFactory(Class<T> type) {
        return (ObjectFactory<T>) objectFactories.computeIfAbsent(type,
                key -> new ObjectFactory<>(BeanClass.create(key), this));
    }

    @SuppressWarnings("unchecked")
    public <T> SpecClassFactory<T> registerSpecClassFactory(Class<? extends Spec<T>> specClass) {
        Spec<T> spec = BeanClass.newInstance(specClass);
        SpecClassFactory<?> specClassFactory = specClassFactoriesWithType.computeIfAbsent(specClass,
                type -> new SpecClassFactory<>(queryObjectFactory(spec.getType()), specClass, this));
        specClassFactoriesWithName.put(spec.getName(), specClassFactory);
        return (SpecClassFactory<T>) specClassFactory;
    }

    @SuppressWarnings("unchecked")
    public <T> SpecClassFactory<T> querySpecClassFactory(String specName) {
        return (SpecClassFactory<T>) specClassFactoriesWithName.computeIfAbsent(specName, key -> {
            throw new IllegalArgumentException("Spec `" + specName + "` not exist");
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
        return new SpecFactory<>(queryObjectFactory(spec.getType()), spec, this, trait);
    }

    public <T> void registerDefaultValueFactory(Class<T> type, DefaultValueFactory<T> factory) {
        defaultValueFactories.register(type, factory);
    }
}
