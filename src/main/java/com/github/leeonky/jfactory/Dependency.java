package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

class Dependency<T> {
    private final Function<Object[], T> function;
    private final PropertyChain property;
    private final List<PropertyChain> propertyChains;

    public Dependency(Function<Object[], T> function, PropertyChain property, List<PropertyChain> propertyChains) {
        this.function = function;
        this.property = property;
        this.propertyChains = propertyChains;
    }

    @SuppressWarnings("unchecked")
    public <B> void process(ObjectProducer<B> objectProducer, Instance<B> instance) {
        objectProducer.changeChild(property, (producer, property) -> new DependencyProducer<>(
                propertyChains.stream().map(dependency -> (Supplier<Object>)
                        () -> objectProducer.getChild(dependency).getValue()).collect(Collectors.toList()),
                function, (BeanClass<T>) producer.getType().getPropertyWriter(property).getType()));
    }
}
