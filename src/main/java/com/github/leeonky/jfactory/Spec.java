package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static com.github.leeonky.jfactory.PropertyChain.createChain;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

public class Spec<T> {
    private List<BiConsumer<FactorySet, ObjectProducer<T>>> operations = new ArrayList<>();
    private Instance<T> instance;
    private Class<T> type = null;

    public void main() {
    }

    public PropertySpec<T> property(String property) {
        return new PropertySpec<>(this, createChain(property));
    }

    Spec<T> append(BiConsumer<FactorySet, ObjectProducer<T>> operation) {
        operations.add(operation);
        return this;
    }

    void apply(FactorySet factorySet, ObjectProducer<T> producer) {
        operations.forEach(o -> o.accept(factorySet, producer));
        type = producer.getType().getType();
    }

    @SuppressWarnings("unchecked")
    public Class<T> getType() {
        return getClass().equals(Spec.class) ? type :
                (Class<T>) BeanClass.create(getClass()).getSuper(Spec.class).getTypeArguments(0)
                        .orElseThrow(() -> new IllegalStateException("Cannot guess type via generic type argument, please override Spec::getType"))
                        .getType();
    }

    protected String getName() {
        return getClass().getSimpleName();
    }

    public Spec<T> link(String property, String... others) {
        List<PropertyChain> linkProperties = concat(of(property), of(others)).map(PropertyChain::createChain).collect(toList());
        append((factorySet, objectProducer) -> objectProducer.link(linkProperties));
        return this;
    }

    Spec<T> setInstance(Instance<T> instance) {
        this.instance = instance;
        return this;
    }

    public <P> P param(String key) {
        return instance.param(key);
    }

    public <P> P param(String key, P defaultValue) {
        return instance.param(key, defaultValue);
    }

    public Arguments params(String property) {
        return instance.params(property);
    }

    public Arguments params() {
        return instance.params();
    }
}
