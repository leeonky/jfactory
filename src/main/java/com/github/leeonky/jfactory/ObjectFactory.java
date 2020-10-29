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
    private Function<Instance<T>, T> constructor = instance -> getType().newInstance();
    private Consumer<Instance<T>> spec = (instance) -> {
    };
    private Map<String, Consumer<Instance<T>>> mixIns = new HashMap<>();

    public ObjectFactory(BeanClass<T> type) {
        this.type = type;
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
    public Factory<T> spec(String name, Consumer<Instance<T>> mixIn) {
        mixIns.put(name, Objects.requireNonNull(mixIn));
        return this;
    }

    public final T create(Instance<T> instance) {
        return constructor.apply(instance);
    }

    public BeanClass<T> getType() {
        return type;
    }

    public void collectSpec(Collection<String> mixIns, Instance<T> instance) {
        spec.accept(instance);
        mixIns.stream().map(name -> this.mixIns.computeIfAbsent(name, k -> {
            throw new IllegalArgumentException("Mix-in `" + k + "` not exist");
        })).forEach(spec -> spec.accept(instance));
    }

    public Instance<T> createInstance(int sequence) {
        return new Instance<>(sequence, createSpec());
    }
}
