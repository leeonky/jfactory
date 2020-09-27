package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Collections.singletonList;

public class PropertySpec<T> {
    private final String property;
    private final Spec<T> spec;

    PropertySpec(String property, Spec<T> spec) {
        this.property = property;
        this.spec = spec;
    }

    public Spec<T> value(Object value) {
        return value(() -> value);
    }

    @SuppressWarnings("unchecked")
    public <V> Spec<T> value(Supplier<V> value) {
        return spec.append((factorySet, objectProducer) -> objectProducer.addChild(property, new UnFixedValueProducer<>(value,
                (BeanClass<V>) objectProducer.getType().getPropertyWriter(property).getType())));
    }

    public <V> Spec<T> spec(Class<? extends Spec<V>> specClass) {
        return spec.append((factorySet, objectProducer) ->
                objectProducer.addChild(property, factorySet.spec(specClass).createProducer(property)));
    }

    public <V> Spec<T> spec(Spec<V> spec) {
        return this.spec.append((factorySet, objectProducer) ->
                objectProducer.addChild(property, factorySet.spec(spec).createProducer(property)));
    }

    public Spec<T> spec(String... mixInsAndSpec) {
        return spec.append((factorySet, objectProducer) ->
                objectProducer.addChild(property, factorySet.spec(mixInsAndSpec).createProducer(property)));
    }

    public <V> Spec<T> spec(Class<? extends Spec<V>> specClass, Function<Builder<V>, Builder<V>> builder) {
        return spec.append((factorySet, objectProducer) ->
                objectProducer.addChild(property, builder.apply(factorySet.spec(specClass)).createProducer(property)));
    }

    public Spec<T> asDefault() {
        return asDefault(b -> b);
    }

    public Spec<T> asDefault(Function<Builder<?>, Builder<?>> builder) {
        return spec.append((factorySet, objectProducer) -> objectProducer.addChild(property,
                builder.apply(factorySet.type(objectProducer.getType().getPropertyWriter(property).getTypeClass())).createProducer(property)));
    }

    public Spec<T> dependsOn(String dependency, Function<Object, Object> function) {
        return dependsOn(singletonList(dependency), objs -> function.apply(objs[0]));
    }

    public Spec<T> dependsOn(List<String> dependencies, Function<Object[], Object> function) {
        return spec.append((factorySet, objectProducer) -> objectProducer.addDependency(property, dependencies, function::apply));
    }
}
