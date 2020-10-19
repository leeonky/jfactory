package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;
import com.github.leeonky.util.PropertyReader;

class ReadOnlyProducer<T, P> extends Producer<T> {
    private final PropertyReader<P> propertyReader;
    private final Producer<P> parent;

    public ReadOnlyProducer(Producer<P> parent, String property) {
        this(parent, parent.getType().getPropertyReader(property));
    }

    @SuppressWarnings("unchecked")
    private ReadOnlyProducer(Producer<P> parent, PropertyReader<P> propertyReader) {
        super((BeanClass<T>) propertyReader.getType());
        this.parent = parent;
        this.propertyReader = propertyReader;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected T produce() {
        P parentValue = parent.getValue();
        if (parentValue == null)
            return (T) propertyReader.getType().createDefault();
        return (T) propertyReader.getValue(parentValue);
    }
}
