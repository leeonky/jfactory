package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static java.util.function.Function.identity;

public class PropertySpec<T> {
    private final Spec<T> spec;
    private final PropertyChain property;

    PropertySpec(Spec<T> spec, PropertyChain property) {
        this.spec = spec;
        this.property = property;
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
        return spec(false, specClass);
    }

    public <V> Spec<T> spec(boolean intently, Class<? extends Spec<V>> specClass) {
        return appendProducer((factorySet, producer, property) ->
                factorySet.spec(specClass).createProducer(intently));
    }

    public <V> Spec<T> spec(Spec<V> spec) {
        return spec(false, spec);
    }

    public <V> Spec<T> spec(boolean intently, Spec<V> spec) {
        return appendProducer((factorySet, producer, property) ->
                factorySet.spec(spec).createProducer(intently));
    }

    public Spec<T> spec(String... mixInsAndSpec) {
        return spec(false, mixInsAndSpec);
    }

    public Spec<T> spec(boolean intently, String... mixInsAndSpec) {
        return appendProducer((factorySet, producer, property) ->
                factorySet.spec(mixInsAndSpec).createProducer(intently));
    }

    public <V> Spec<T> spec(Class<? extends Spec<V>> specClass, Function<Builder<V>, Builder<V>> builder) {
        return spec(false, specClass, builder);
    }

    public <V> Spec<T> spec(boolean intently, Class<? extends Spec<V>> specClass, Function<Builder<V>, Builder<V>> builder) {
        return appendProducer((factorySet, producer, property) ->
                builder.apply(factorySet.spec(specClass)).createProducer(intently));
    }

    public Spec<T> asDefault() {
        return asDefault(false, identity());
    }

    public Spec<T> asDefault(Function<Builder<?>, Builder<?>> builder) {
        return asDefault(false, builder);
    }

    public Spec<T> asDefault(boolean intently) {
        return asDefault(intently, identity());
    }

    private Spec<T> asDefault(boolean intently, Function<Builder<?>, Builder<?>> builder) {
        return appendProducer((factorySet, producer, property) ->
                builder.apply(factorySet.type(producer.getType().getPropertyWriter(property).getTypeClass())).createProducer(intently));
    }

    public Spec<T> dependsOn(String dependency, Function<Object, Object> function) {
        return dependsOn(singletonList(dependency), objs -> function.apply(objs[0]));
    }

    public Spec<T> dependsOn(List<String> dependencies, Function<Object[], Object> function) {
        return spec.append((factorySet, objectProducer) ->
                objectProducer.addDependency(property, function, dependencies.stream().map(PropertyChain::createChain).collect(Collectors.toList())));
    }

    private Spec<T> appendProducer(Fuc<FactorySet, Producer<?>, String, Producer<?>> producerGenerator) {
        if (property.isSingle() || property.isTopLevelPropertyCollection())
            return spec.append((factorySet, objectProducer) -> objectProducer.changeChild(property,
                    ((nextToLast, property) -> producerGenerator.apply(factorySet, nextToLast, property))));
        throw new IllegalArgumentException(String.format("Not support property chain '%s' in current operation", property.toString()));
    }

    @FunctionalInterface
    interface Fuc<P1, P2, P3, R> {
        R apply(P1 p1, P2 p2, P3 p3);
    }
}
