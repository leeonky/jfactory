package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;
import com.github.leeonky.util.GenericType;

public abstract class SpecReference<T> {
    private final Class<? extends Spec<?>> spec;

    public SpecReference(Class<? extends Spec<?>> spec) {
        this.spec = spec;
    }

    public Class<? extends Spec<?>> getSpec() {
        return spec;
    }

    @SuppressWarnings("unchecked")
    public BeanClass<T> getType() {
        return (BeanClass<T>) BeanClass.create(GenericType.createGenericType(getClass().getGenericSuperclass())).getTypeArguments(0)
                .orElseThrow(() -> new IllegalArgumentException("Cannot guess type, use new SpecReference<T>(spec) {}"));
    }
}
