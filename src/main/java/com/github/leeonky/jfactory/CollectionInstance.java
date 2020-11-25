package com.github.leeonky.jfactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class CollectionInstance<T> extends SubInstance<T> {
    private final List<Integer> indexes;

    public CollectionInstance(List<Integer> indexes, String property, int sequence, Spec<T> spec, DefaultArguments argument) {
        super(property, sequence, spec, argument);
        this.indexes = new ArrayList<>(indexes);
    }

    @Override
    public String propertyInfo() {
        return String.format("%s%s", super.propertyInfo(),
                indexes.stream().map(i -> String.format("[%d]", i)).collect(Collectors.joining()));
    }

    public CollectionInstance<T> element(int index) {
        CollectionInstance<T> collection = new CollectionInstance<>(indexes, property, sequence, spec, arguments);
        collection.indexes.add(index);
        return collection;
    }
}
