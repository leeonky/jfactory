package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.lang.String.format;
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
                new UnFixedValueProducer<>(value, (BeanClass<V>) producer.getPropertyWriterType(property)));
    }

    public <V, S extends Spec<V>> Spec<T> spec(Class<S> specClass, Consumer<S> trait) {
        return spec(false, specClass, trait);
    }

    public <V, S extends Spec<V>> Spec<T> spec(boolean intently, Class<S> specClass, Consumer<S> trait) {
        return appendProducer(factorySet -> createProducer(intently, factorySet.spec(specClass, trait)));
    }

    public <V> Spec<T> from(Class<? extends Spec<V>> specClass) {
        return from(false, specClass);
    }

    public <V> Spec<T> from(boolean intently, Class<? extends Spec<V>> specClass) {
        return appendProducer(factorySet -> createProducer(intently, factorySet.from(specClass)));
    }

    public Spec<T> from(String... traitsAndSpec) {
        return from(false, traitsAndSpec);
    }

    public Spec<T> from(boolean intently, String... traitsAndSpec) {
        return appendProducer(factorySet -> createProducer(intently, factorySet.from(traitsAndSpec)));
    }

    public <V> Spec<T> from(Class<? extends Spec<V>> specClass, Function<Builder<V>, Builder<V>> builder) {
        return from(false, specClass, builder);
    }

    public <V> Spec<T> from(boolean intently, Class<? extends Spec<V>> specClass, Function<Builder<V>, Builder<V>> builder) {
        return appendProducer(factorySet -> createProducer(intently, builder.apply(factorySet.from(specClass))));
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
        return appendProducer((factorySet, producer, property) -> producer.subDefaultValueProducer(property)
                .orElseGet(() -> createProducer(intently, builder.apply(factorySet.type(
                        producer.getPropertyWriterType(property).getType())))));
    }

    public Spec<T> dependsOn(String dependency, Function<Object, Object> rule) {
        return dependsOn(singletonList(dependency), objs -> rule.apply(objs[0]));
    }

    public Spec<T> dependsOn(List<String> dependencies, Function<Object[], Object> rule) {
        return spec.append((factorySet, objectProducer) ->
                objectProducer.addDependency(property, rule,
                        dependencies.stream().map(PropertyChain::createChain).collect(Collectors.toList())));
    }

    private Spec<T> appendProducer(Fuc<FactorySet, Producer<?>, String, Producer<?>> producerFactory) {
        if (property.isSingle() || property.isTopLevelPropertyCollection())
            return spec.append((factorySet, objectProducer) -> objectProducer.changeChild(property,
                    ((nextToLast, property) -> producerFactory.apply(factorySet, nextToLast, property))));
        throw new IllegalArgumentException(format("Not support property chain '%s' in current operation", property));
    }

    private Spec<T> appendProducer(Function<FactorySet, Producer<?>> producerFactory) {
        return appendProducer((factorySet, producer, s) -> producerFactory.apply(factorySet));
    }

    private <V> Producer<V> createProducer(boolean intently, Builder<V> builder) {
        return builder.args(spec.params(property.toString())).createProducer(intently);
    }

    @FunctionalInterface
    interface Fuc<P1, P2, P3, R> {
        R apply(P1 p1, P2 p2, P3 p3);
    }
}
