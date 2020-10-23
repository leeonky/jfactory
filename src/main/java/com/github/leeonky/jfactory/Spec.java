package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.leeonky.jfactory.PropertyChain.createChain;

public class Spec<T> {
    private List<BiConsumer<FactorySet, ObjectProducer<T>>> operations = new ArrayList<>();

    public Spec() {
        main();
    }

    public void main() {
    }

    public PropertySpec<T> property(String property) {
        return new PropertySpec<>(this, createChain(property));
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
        return (Class<T>) BeanClass.create(getClass()).getSuper(Spec.class).getTypeArguments(0)
                .orElseThrow(() -> new IllegalStateException("Cannot guess type via generic type argument, please override Spec::getType"))
                .getType();
    }

    String getName() {
        return getClass().getSimpleName();
    }

    public Spec<T> link(String property, String... others) {
        List<PropertyChain> linkProperties = Stream.concat(Stream.of(property), Stream.of(others))
                .map(PropertyChain::createChain).collect(Collectors.toList());
        append((factorySet, objectProducer) -> objectProducer.link(linkProperties));
        return this;
    }
}
