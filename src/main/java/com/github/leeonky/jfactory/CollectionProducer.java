package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class CollectionProducer<T> extends Producer<T> {
    private List<Producer<?>> children = new ArrayList<>();

    public CollectionProducer(BeanClass<T> type) {
        super(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected T produce() {
        return (T) getType().createCollection(children.stream().map(Producer::produce).collect(Collectors.toList()));
    }

    @Override
    public void addChild(Object index, Producer<?> producer) {
        children.add(producer);
    }

    @Override
    public Producer<?> getChild(Object index) {
        return children.get((int) index);
    }
}
