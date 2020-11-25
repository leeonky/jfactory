package com.github.leeonky.jfactory;

import static java.util.Collections.emptyList;

class SubInstance<T> extends RootInstance<T> {
    protected final String property;

    public SubInstance(String property, int sequence, Spec<T> spec, DefaultArguments argument) {
        super(sequence, spec, argument);
        this.property = property;
    }

    public String propertyInfo() {
        return String.format("%s#%d", property, getSequence());
    }

    public CollectionInstance<T> inCollection() {
        return new CollectionInstance<>(emptyList(), property, getSequence(), spec, arguments);
    }
}
