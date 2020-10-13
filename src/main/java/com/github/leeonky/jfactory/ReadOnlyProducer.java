package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;
import com.github.leeonky.util.PropertyReader;

class ReadOnlyProducer<T, P> extends Producer<T> {
    private final PropertyReader<P> propertyReader;
    private final Producer<P> parent;

    @SuppressWarnings("unchecked")
    public ReadOnlyProducer(Producer<P> parent, String property) {
        super((BeanClass<T>) (parent.getType().getPropertyReader(property)).getType());
        this.parent = parent;
        propertyReader = parent.getType().getPropertyReader(property);
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
