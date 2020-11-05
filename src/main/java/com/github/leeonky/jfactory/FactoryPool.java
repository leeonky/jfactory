package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class FactoryPool {
    public final TypeSequence typeSequence = new TypeSequence();
    private final DefaultValueBuilders defaultValueBuilders = new DefaultValueBuilders();
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

    public <T> Optional<DefaultValueBuilder<T>> queryDefaultValueBuilder(BeanClass<T> type) {
        return defaultValueBuilders.query(type.getType());
    }

    public <T> DefaultValueBuilder<T> getDefaultValueBuilder(BeanClass<T> type) {
        return queryDefaultValueBuilder(type).orElseGet(() -> new DefaultValueBuilders.DefaultTypeBuilder<>(type));
    }

    public int nextSequence(Class<?> type) {
        return typeSequence.generate(type);
    }
}
