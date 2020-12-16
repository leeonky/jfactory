package com.github.leeonky.jfactory;

import java.util.function.Consumer;

public class SpecFactory<T, S extends Spec<T>> extends SpecClassFactory<T> {
    private final S spec;
    private final Consumer<S> trait;

    @SuppressWarnings("unchecked")
    public SpecFactory(ObjectFactory<T> base, S spec, FactorySet factorySet, Consumer<S> trait) {
        super(base, (Class<? extends Spec<T>>) spec.getClass(), factorySet);
        this.spec = spec;
        this.trait = trait;
    }

    @Override
    public Spec<T> createSpec() {
        return spec;
    }

    @Override
    protected void collectClassSpec(Instance<T> instance) {
        super.collectClassSpec(instance);
        trait.accept(spec);
    }
}
