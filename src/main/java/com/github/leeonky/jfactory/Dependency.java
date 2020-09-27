package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

class Dependency<T> {
    // TODO test for property chain
    private final List<String> property;

    // TODO test for dependency chain
    private final List<List<String>> dependencies;
    private final Function<Object[], T> function;

    public Dependency(String property, List<String> dependencies, Function<Object[], T> function) {
        this.property = toChain(property);
        this.dependencies = dependencies.stream().map(this::toChain).collect(Collectors.toList());
        this.function = function;
    }

    private List<String> toChain(String s) {
        return asList(s);
    }

    public List<String> getProperty() {
        return property;
    }

    @SuppressWarnings("unchecked")
    public <B> void process(ObjectProducer<B> objectProducer, Instance<B> instance) {

        LinkedList<String> linkedProperty = new LinkedList<>(property);
        String p = linkedProperty.removeLast();

        // TODO property in sub is readonly: no producer
        Producer<?> producer = objectProducer.getChild(linkedProperty).get();

        BeanClass<T> type = (BeanClass<T>) producer.getType().getPropertyWriter(p).getType();
        producer.addChild(p, new DependencyProducer<>(dependencies.stream().map(dependency ->
                // TODO dependency is read from instance: no producer
                (Supplier<Object>) () -> objectProducer.getChild(new LinkedList<>(dependency)).get().getValue()).collect(Collectors.toList()),
                function, type));
    }
}
