package com.github.leeonky.jfactory;

import java.util.Collection;
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
    public void collectSpec(Collection<String> traits, Instance<T> instance) {
        super.collectSpec(traits, instance);
        collectClassSpec(instance, spec -> trait.accept((S) spec));
    }
}
