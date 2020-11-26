package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static com.github.leeonky.jfactory.PropertyChain.createChain;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

public class Spec<T> {
    private List<BiConsumer<FactorySet, ObjectProducer<T>>> operations = new ArrayList<>();
    private Instance<T> instance;

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
        List<PropertyChain> linkProperties = concat(of(property), of(others)).map(PropertyChain::createChain).collect(toList());
        append((factorySet, objectProducer) -> objectProducer.link(linkProperties));
        return this;
    }

    public Instance<T> instance() {
        return instance;
    }

    Spec<T> setInstance(Instance<T> instance) {
        this.instance = instance;
        return this;
    }
}
