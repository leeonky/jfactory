package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;
import com.github.leeonky.util.PropertyWriter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

class ObjectFactory<T> implements Factory<T> {
    private final BeanClass<T> type;
    private Function<Instance<T>, T> constructor = this::construct;
    private Consumer<Instance<T>> specification = (instance) -> {
    };
    private Map<String, Consumer<Instance<T>>> mixIns = new HashMap<>();

    public ObjectFactory(BeanClass<T> type) {
        this.type = type;
    }

    public Spec<T> createSpec() {
        return new Spec<>();
    }

    @Override
    public Factory<T> constructor(Function<Instance<T>, T> constructor) {
        this.constructor = Objects.requireNonNull(constructor);
        return this;
    }

    @Override
    public Factory<T> spec(Consumer<Instance<T>> specification) {
        this.specification = Objects.requireNonNull(specification);
        return this;
    }

    @Override
    public Factory<T> spec(String name, Consumer<Instance<T>> mixIn) {
        mixIns.put(name, Objects.requireNonNull(mixIn));
        return this;
    }

    private T construct(Instance<T> _ignore) {
        return type.newInstance();
    }

    public final T create(Instance<T> instance) {
        return constructor.apply(instance);
    }

    public BeanClass<T> getType() {
        return type;
    }

    public Map<String, PropertyWriter<T>> getProperties() {
        return type.getPropertyWriters();
    }

    public void collectSpecification(Collection<String> mixIns, Instance<T> instance) {
        specification.accept(instance);
        mixIns.stream().peek(name -> {
            if (!this.mixIns.containsKey(name))
                throw new IllegalArgumentException("Mix-in `" + name + "` not exist");
        }).map(this.mixIns::get).forEach(specification -> specification.accept(instance));
    }
}
