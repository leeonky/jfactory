package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

class Dependency<T> {
    private final Function<Object[], T> rule;
    private final PropertyChain property;
    private final List<PropertyChain> dependencies;

    public Dependency(PropertyChain property, List<PropertyChain> dependencies, Function<Object[], T> rule) {
        this.property = property;
        this.dependencies = dependencies;
        this.rule = rule;
    }

    @SuppressWarnings("unchecked")
    public void process(Producer<?> parent) {
        parent.changeChild(property, (nextToLast, property) -> new DependencyProducer<>(
                (BeanClass<T>) nextToLast.getPropertyWriterType(property), suppliers(parent), rule));
    }

    private List<Supplier<Object>> suppliers(Producer<?> producer) {
        return dependencies.stream().map(dependency -> (Supplier<Object>) () ->
                producer.child(dependency).getValue()).collect(Collectors.toList());
    }
}
