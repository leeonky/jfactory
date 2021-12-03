package com.github.leeonky.jfactory;

public class SpecReference<T> {
    private final Class<? extends Spec<T>> spec;

    public SpecReference(Class<? extends Spec<T>> spec) {
        this.spec = spec;
    }

    public Class<? extends Spec<T>> getSpec() {
        return spec;
    }
}
