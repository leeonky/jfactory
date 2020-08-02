package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;
import com.github.leeonky.util.PropertyWriter;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

class ObjectFactory<T> implements Factory<T> {
    private final BeanClass<T> type;
    private Function<Instance<T>, T> constructor = this::construct;
    private Consumer<Instance<T>> specification = (instance) -> {
    };

    public ObjectFactory(BeanClass<T> type) {
        this.type = type;
    }

    @Override
    public Factory<T> constructor(Function<Instance<T>, T> constructor) {
        this.constructor = Objects.requireNonNull(constructor);
        return this;
    }

    @Override
    public Factory<T> specification(Consumer<Instance<T>> specification) {
        this.specification = Objects.requireNonNull(specification);
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

    public void collectSpecification(Instance<T> instance) {
        specification.accept(instance);
    }
}
