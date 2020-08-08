package com.github.leeonky.jfactory;

public class SpecFactory<T> extends SpecClassFactory<T> {
    private final Spec<T> spec;

    public <P extends Spec<T>> SpecFactory(ObjectFactory<T> base, P spec) {
        super(base, (Class<? extends Spec<T>>) spec.getClass());
        this.spec = spec;
    }

    @Override
    public Spec<T> createSpec() {
        return spec;
    }
}
