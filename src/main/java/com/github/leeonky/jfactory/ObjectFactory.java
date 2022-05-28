package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

class ObjectFactory<T> implements Factory<T> {
    private final BeanClass<T> type;
    private final FactorySet factorySet;
    private final Map<String, Consumer<Instance<T>>> traits = new HashMap<>();
    private final Map<String, Transformer> transformers = new LinkedHashMap<>();
    private final Transformer passThrough = input -> input;
    private Function<Instance<T>, T> constructor = this::defaultConstruct;
    private Consumer<Instance<T>> spec = (instance) -> {
    };

    public ObjectFactory(BeanClass<T> type, FactorySet factorySet) {
        this.type = type;
        this.factorySet = factorySet;
    }

    @SuppressWarnings("unchecked")
    private T defaultConstruct(Instance<T> instance) {
        return getType().isCollection()
                ? (T) getType().createCollection(Collections.nCopies(instance.collectionSize(), null))
                : getType().newInstance();
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

    @Override
    public Factory<T> transformer(String property, Transformer transformer) {
        transformers.put(property, transformer);
        return this;
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

    public ObjectFactory<T> getBase() {
        return this;
    }

    public Object transform(String name, Object value) {
        return transformers.getOrDefault(name, passThrough).checkAndTransform(value);
    }

}
