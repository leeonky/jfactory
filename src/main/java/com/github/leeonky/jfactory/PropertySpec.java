package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

public class PropertySpec<T> {
    private final List<String> property;
    private final Spec<T> spec;

    PropertySpec(String property, Spec<T> spec) {
        this.spec = spec;
        this.property = toChain(property);
    }

    private List<String> toChain(String property) {
        return Arrays.stream(property.split("[\\[\\].]")).filter(s -> !s.isEmpty()).collect(Collectors.toList());
    }

    public Spec<T> value(Object value) {
        return value(() -> value);
    }

    @SuppressWarnings("unchecked")
    public <V> Spec<T> value(Supplier<V> value) {
        return appendProducer((factorySet, producer, property) ->
                new UnFixedValueProducer<>(value, (BeanClass<V>) producer.getType().getPropertyWriter(property).getType()));
    }

    public <V> Spec<T> spec(Class<? extends Spec<V>> specClass) {
        return appendProducer((factorySet, producer, property) ->
                factorySet.spec(specClass).createProducer(property));
    }

    public <V> Spec<T> spec(Spec<V> spec) {
        return appendProducer((factorySet, producer, property) ->
                factorySet.spec(spec).createProducer(property));
    }

    public Spec<T> spec(String... mixInsAndSpec) {
        return appendProducer((factorySet, producer, property) ->
                factorySet.spec(mixInsAndSpec).createProducer(property));
    }

    public <V> Spec<T> spec(Class<? extends Spec<V>> specClass, Function<Builder<V>, Builder<V>> builder) {
        return appendProducer((factorySet, producer, property) ->
                builder.apply(factorySet.spec(specClass)).createProducer(property));
    }

    public Spec<T> asDefault() {
        return asDefault(b -> b);
    }

    public Spec<T> asDefault(Function<Builder<?>, Builder<?>> builder) {
        return appendProducer((factorySet, producer, property) ->
                builder.apply(factorySet.type(producer.getType().getPropertyWriter(property).getTypeClass())).createProducer(property));
    }

    public Spec<T> dependsOn(String dependency, Function<Object, Object> function) {
        return dependsOn(singletonList(dependency), objs -> function.apply(objs[0]));
    }

    public Spec<T> dependsOn(List<String> dependencies, Function<Object[], Object> function) {
        return spec.append((factorySet, objectProducer) ->
                objectProducer.addDependency(property, dependencies.stream().map(this::toChain).collect(Collectors.toList()), function));
    }

    private Spec<T> appendProducer(Fuc<FactorySet, Producer<?>, String, Producer<?>> producerGenerator) {
        return spec.append((factorySet, objectProducer) -> objectProducer.changeChild(property, ((producer, property) ->
                producerGenerator.apply(factorySet, producer, property))));
    }

    @FunctionalInterface
    interface Fuc<P1, P2, P3, R> {
        R apply(P1 p1, P2 p2, P3 p3);
    }
}
