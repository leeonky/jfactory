package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;
import com.github.leeonky.util.Classes;

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
        Spec<T> spec = Classes.newInstance(specClass);
        boolean globalSpec = isGlobalSpec(specClass);
        SpecClassFactory<?> specClassFactory = specClassFactoriesWithType.computeIfAbsent(specClass,
                type -> new SpecClassFactory<>(specClass, this, globalSpec));
        specClassFactoriesWithName.put(spec.getName(), specClassFactory);
        if (globalSpec)
            registerGlobalSpec(specClassFactory);
        if (!specClass.getSuperclass().equals(Spec.class))
            registerSpecClassFactory((Class<S>) specClass.getSuperclass());
    }

    private <T, S extends Spec<T>> boolean isGlobalSpec(Class<S> specClass) {
        return specClass.getAnnotation(Global.class) != null;
    }

    private <T> void registerGlobalSpec(SpecClassFactory<?> specClassFactory) {
        Class<? extends Spec<?>> specClass = specClassFactory.getSpecClass();
        if (specClassFactory.getBase() instanceof SpecClassFactory)
            throw new IllegalArgumentException(String.format("More than one @Global Spec class `%s` and `%s`",
                    ((SpecClassFactory<?>) specClassFactory.getBase()).getSpecClass().getName(), specClass.getName()));
        if (!specClass.getSuperclass().equals(Spec.class))
            throw new IllegalArgumentException(String.format("Global Spec %s should not have super Spec %s.",
                    specClass.getName(), specClass.getSuperclass().getName()));
        objectFactories.put(specClassFactory.getType(), specClassFactory);
    }

    public void removeGlobalSpec(BeanClass<?> type) {
        objectFactories.computeIfPresent(type, (key, factory) -> factory.getBase());
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
        return new SpecFactory<>(Classes.newInstance(specClass), this, trait);
    }

    public <T> void registerDefaultValueFactory(Class<T> type, DefaultValueFactory<T> factory) {
        defaultValueFactories.register(type, factory);
    }
}
