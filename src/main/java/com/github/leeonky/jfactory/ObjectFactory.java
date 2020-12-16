package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

class ObjectFactory<T> implements Factory<T> {
    private final BeanClass<T> type;
    private final FactorySet factorySet;
    private Function<Instance<T>, T> constructor = instance -> getType().newInstance();
    private Consumer<Instance<T>> spec = (instance) -> {
    };
    private Map<String, Consumer<Instance<T>>> traits = new HashMap<>();

    public ObjectFactory(BeanClass<T> type, FactorySet factorySet) {
        this.type = type;
        this.factorySet = factorySet;
    }

    protected Spec<T> createSpec() {
        return new Spec<>();
    }

    @Override
    public Factory<T> constructor(Function<Instance<T>, T> constructor) {
        this.constructor = Objects.requireNonNull(constructor);
        return this;
    }

    @Override
    public Factory<T> spec(Consumer<Instance<T>> spec) {
        this.spec = Objects.requireNonNull(spec);
        return this;
    }

    @Override
    public Factory<T> spec(String name, Consumer<Instance<T>> traits) {
        this.traits.put(name, Objects.requireNonNull(traits));
        return this;
    }

    public final T create(Instance<T> instance) {
        return constructor.apply(instance);
    }

    @Override
    public BeanClass<T> getType() {
        return type;
    }

    public void collectSpec(Collection<String> traits, Instance<T> instance) {
        spec.accept(instance);
        traits.stream().map(name -> this.traits.computeIfAbsent(name, k -> {
            throw new IllegalArgumentException("Trait `" + k + "` not exist");
        })).forEach(spec -> spec.accept(instance));
    }

    public RootInstance<T> createInstance(DefaultArguments argument) {
        return new RootInstance<>(factorySet.nextSequence(type.getType()), createSpec(), argument);
    }

    public FactorySet getFactorySet() {
        return factorySet;
    }
}
