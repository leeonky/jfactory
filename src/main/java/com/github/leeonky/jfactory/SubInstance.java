package com.github.leeonky.jfactory;

import com.github.leeonky.util.PropertyWriter;

import static java.util.Collections.emptyList;

public class SubInstance<T> extends RootInstance<T> {
    private final PropertyWriter<?> property;

    public SubInstance(PropertyWriter<?> property, int sequence, Spec<T> spec, DefaultArguments argument) {
        super(sequence, spec, argument);
        this.property = property;
    }

    String propertyInfo() {
        return String.format("%s#%d", property.getName(), getSequence());
    }

    CollectionInstance<T> inCollection() {
        return new CollectionInstance<>(emptyList(), property, getSequence(), spec, arguments);
    }

    public PropertyWriter<?> getProperty() {
        return property;
    }
}
