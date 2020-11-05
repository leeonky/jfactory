package com.github.leeonky.jfactory;

public class SpecFactory<T> extends SpecClassFactory<T> {
    private final Spec<T> spec;

    @SuppressWarnings("unchecked")
    public <P extends Spec<T>> SpecFactory(ObjectFactory<T> base, P spec, FactoryPool factoryPool) {
        super(base, (Class<? extends Spec<T>>) spec.getClass(), factoryPool);
        this.spec = spec;
    }

    @Override
    public Spec<T> createSpec() {
        return spec;
    }
}
