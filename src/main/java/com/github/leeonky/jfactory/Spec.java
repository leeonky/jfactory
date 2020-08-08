package com.github.leeonky.jfactory;

import com.github.leeonky.util.GenericType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class Spec<T> {
    private List<BiConsumer<FactorySet, ObjectProducer<T>>> operations = new ArrayList<>();

    public Spec() {
        main();
    }

    public void main() {
    }

    public PropertySpec<T> property(String name) {
        return new PropertySpec<>(name, this);
    }

    Spec<T> append(BiConsumer<FactorySet, ObjectProducer<T>> operation) {
        operations.add(operation);
        return this;
    }

    void apply(FactorySet factorySet, ObjectProducer<T> producer) {
        operations.forEach(o -> o.accept(factorySet, producer));
    }

    @SuppressWarnings("unchecked")
    Class<T> getType() {
        return (Class<T>) GenericType.createGenericType(getClass().getGenericSuperclass()).getGenericTypeParameter(0)
                .orElseThrow(() -> new IllegalStateException(String.format("Invalid Spec '%s', should specify generic type or override getType() method", getClass().getName())))
                .getRawType();
    }

    String getName() {
        return getClass().getSimpleName();
    }
}
