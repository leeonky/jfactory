package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

class Dependency<T> {
    private final Function<Object[], T> function;
    private final PropertyChain property;
    private final List<PropertyChain> dependencyProperties;

    public Dependency(Function<Object[], T> rule, PropertyChain property, List<PropertyChain> dependencyProperties) {
        function = rule;
        this.property = property;
        this.dependencyProperties = dependencyProperties;
    }

    @SuppressWarnings("unchecked")
    public void process(Producer<?> producer) {
        producer.changeChild(property, (host, property) -> new DependencyProducer<>(dependencySuppliers(producer),
                function, (BeanClass<T>) host.getType().getPropertyWriter(property).getType()));
    }

    private List<Supplier<Object>> dependencySuppliers(Producer<?> producer) {
        return dependencyProperties.stream().map(dependency ->
                // TODO need to check producer.getChild(dependency) is replaced by linker
                (Supplier<Object>) () -> producer.getChild(dependency).getValue()).collect(Collectors.toList());
    }
}
