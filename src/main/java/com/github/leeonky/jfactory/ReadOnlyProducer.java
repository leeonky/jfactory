package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;
import com.github.leeonky.util.PropertyReader;

import static java.util.Optional.ofNullable;

class ReadOnlyProducer<T, P> extends Producer<T> {
    private final PropertyReader<P> reader;
    private final Producer<P> parent;

    public ReadOnlyProducer(Producer<P> parent, String property) {
        this(parent, parent.getType().getPropertyReader(property));
    }

    @SuppressWarnings("unchecked")
    private ReadOnlyProducer(Producer<P> parent, PropertyReader<P> reader) {
        super((BeanClass<T>) reader.getType());
        this.parent = parent;
        this.reader = reader;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected T produce() {
        return (T) ofNullable(parent.getValue()).map(reader::getValue).orElseGet(() -> reader.getType().createDefault());
    }
}
