package com.github.leeonky.jfactory;

import java.util.function.Function;
import java.util.function.Supplier;

public class PropertySpecification<T> {
    private final String name;
    private final Spec<T> spec;

    PropertySpecification(String name, Spec<T> spec) {
        this.name = name;
        this.spec = spec;
    }

    public Spec<T> value(Object value) {
        return spec.append((factorySet, objectProducer) ->
                objectProducer.addChild(name, new UnFixedValueProducer<>(() -> value)));
    }

    public <V> Spec<T> value(Supplier<V> value) {
        return spec.append((factorySet, objectProducer) ->
                objectProducer.addChild(name, new UnFixedValueProducer<>(value)));
    }

    public <V> Spec<T> spec(Class<? extends Spec<V>> specClass) {
        return spec.append((factorySet, objectProducer) ->
                objectProducer.addChild(name, factorySet.spec(specClass).createProducer(name)));
    }

    public <V> Spec<T> spec(Spec<V> spec) {
        return this.spec.append((factorySet, objectProducer) ->
                objectProducer.addChild(name, factorySet.spec(spec).createProducer(name)));
    }

    public Spec<T> spec(String... mixInsAndSpec) {
        return spec.append((factorySet, objectProducer) ->
                objectProducer.addChild(name, factorySet.spec(mixInsAndSpec).createProducer(name)));
    }

    public <V> Spec<T> spec(Class<? extends Spec<V>> specClass, Function<Builder<V>, Builder<V>> builder) {
        return spec.append((factorySet, objectProducer) ->
                objectProducer.addChild(name, builder.apply(factorySet.spec(specClass)).createProducer(name)));
    }

    public Spec<T> asDefault() {
        return spec.append((factorySet, objectProducer) ->
                objectProducer.addChild(name, factorySet.type(objectProducer.getType().getPropertyWriter(name).getPropertyType()).createProducer(name)));
    }

    public Spec<T> asDefault(Function<Builder<?>, Builder<?>> builder) {
        return spec.append((factorySet, objectProducer) ->
                objectProducer.addChild(name, builder.apply(factorySet.type(objectProducer.getType().getPropertyWriter(name).getPropertyType())).createProducer(name)));
    }
}
