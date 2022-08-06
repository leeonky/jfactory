package com.github.leeonky.jfactory;

import java.util.function.Consumer;

public class SpecFactory<T, S extends Spec<T>> extends SpecClassFactory<T> {
    private final S spec;
    private final Consumer<S> trait;

    @SuppressWarnings("unchecked")
    public SpecFactory(S spec, FactorySet factorySet, Consumer<S> trait) {
        super((Class<? extends Spec<T>>) spec.getClass(), factorySet, false);
        this.spec = spec;
        this.trait = trait;
    }

    @Override
    protected Spec<T> createSpec() {
        return spec;
    }

    @Override
    protected void collectSubSpec(Instance<T> instance) {
        super.collectSubSpec(instance);
        collectClassSpec(instance, spec -> trait.accept((S) spec));
    }
}
