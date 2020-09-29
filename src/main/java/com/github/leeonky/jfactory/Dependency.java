package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

class Dependency<T> {
    // TODO test for property chain
    private final List<String> property;

    // TODO test for dependency chain
    private final List<List<String>> dependencies;
    private final Function<Object[], T> function;

    public Dependency(List<String> property, List<List<String>> dependencies, Function<Object[], T> function) {
        this.property = property;
        this.dependencies = dependencies;
        this.function = function;
    }

    public List<String> getProperty() {
        return property;
    }

    @SuppressWarnings("unchecked")
    public <B> void process(ObjectProducer<B> objectProducer, Instance<B> instance) {
        objectProducer.changeChild(property, (producer, property) -> new DependencyProducer<>(dependencies.stream().map(dependency ->
                // TODO dependency is read from instance: no producer
                (Supplier<Object>) () -> objectProducer.getChild(new LinkedList<>(dependency)).get().getValue()).collect(Collectors.toList()),
                function, (BeanClass<T>) producer.getType().getPropertyWriter(property).getType()));
    }
}
