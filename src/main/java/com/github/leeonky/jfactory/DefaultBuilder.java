package com.github.leeonky.jfactory;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.leeonky.util.BeanClass.cast;
import static java.util.Arrays.asList;
import static java.util.Objects.hash;

class DefaultBuilder<T> implements Builder<T> {
    private final ObjectFactory<T> objectFactory;
    private final FactorySet factorySet;
    private final Set<String> traits = new LinkedHashSet<>();
    private final KeyValueCollection properties = new KeyValueCollection();

    public DefaultBuilder(ObjectFactory<T> objectFactory, FactorySet factorySet) {
        this.factorySet = factorySet;
        this.objectFactory = objectFactory;
    }

    @Override
    public T create() {
        return createProducer(false).doDependenciesAndLinks().getValue();
    }

    @Override
    public ObjectProducer<T> createProducer(boolean intently) {
        return new ObjectProducer<>(factorySet, objectFactory, this, intently);
    }

    @Override
    public Builder<T> trait(String... traits) {
        DefaultBuilder<T> newBuilder = copy();
        newBuilder.traits.addAll(asList(traits));
        return newBuilder;
    }

    private DefaultBuilder<T> copy() {
        DefaultBuilder<T> builder = new DefaultBuilder<>(objectFactory, factorySet);
        builder.properties.merge(properties);
        builder.traits.addAll(traits);
        return builder;
    }

    @Override
    public Builder<T> properties(Map<String, ?> properties) {
        DefaultBuilder<T> newBuilder = copy();
        properties.forEach(newBuilder.properties::add);
        return newBuilder;
    }

    @Override
    public Collection<T> queryAll() {
        KeyValueCollection.Matcher<T> matcher = properties.matcher(objectFactory.getType());
        return factorySet.getDataRepository().queryAll(objectFactory.getType().getType()).stream()
                .filter(matcher::matches).collect(Collectors.toList());
    }

    //TODO missing type
    @Override
    public int hashCode() {
        return hash(DefaultBuilder.class, properties, traits);
    }

    //TODO missing type
    @Override
    public boolean equals(Object another) {
        return cast(another, DefaultBuilder.class)
                .map(builder -> properties.equals(builder.properties) && traits.equals(builder.traits))
                .orElseGet(() -> super.equals(another));
    }

    public void establishSpecProducers(ObjectProducer<T> objectProducer, RootInstance<T> instance) {
        forSpec(objectProducer, instance);
        forInputProperties(objectProducer);
    }

    private void forSpec(ObjectProducer<T> objectProducer, RootInstance<T> instance) {
        objectFactory.collectSpec(traits, instance);
        instance.spec().apply(factorySet, objectProducer);
    }

    private void forInputProperties(ObjectProducer<T> objectProducer) {
        properties.expressions(objectFactory.getType()).forEach(exp ->
                objectProducer.addChild(exp.getProperty(), exp.buildProducer(factorySet, objectProducer)));
    }
}
