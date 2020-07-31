package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.Objects;
import java.util.function.Function;

class ObjectFactory<T> implements Factory<T> {
    private final BeanClass<T> type;
    private Function<Instance, T> constructor = this::construct;

    public ObjectFactory(BeanClass<T> type) {
        this.type = type;
    }

    @Override
    public Factory<T> construct(Function<Instance, T> constructor) {
        this.constructor = Objects.requireNonNull(constructor);
        return this;
    }

    private T construct(Instance _ignore) {
        return type.newInstance();
    }

    public final T create(Instance instance) {
        return constructor.apply(instance);
    }

    public BeanClass<T> getType() {
        return type;
    }
}
