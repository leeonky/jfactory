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
    private final Set<String> mixIns = new LinkedHashSet<>();
    private final KeyValueCollection properties = new KeyValueCollection();

    public DefaultBuilder(ObjectFactory<T> objectFactory, FactorySet factorySet) {
        this.factorySet = factorySet;
        this.objectFactory = objectFactory;
    }

    @Override
    public T create() {
        return createProducer(false).processDependencyAndLink().getValue();
    }

    @Override
    public ObjectProducer<T> createProducer(boolean intently) {
        Instance<T> instance = objectFactory.createInstance(factorySet.newSequence(objectFactory.getType()));
        ObjectProducer<T> objectProducer = new ObjectProducer<>(factorySet, objectFactory, this, intently, instance);
        establishChildProducers(objectProducer, instance);
        return objectProducer;
    }

    @Override
    public Builder<T> mixIn(String... mixIns) {
        DefaultBuilder<T> newBuilder = copy();
        newBuilder.mixIns.addAll(asList(mixIns));
        return newBuilder;
    }

    private DefaultBuilder<T> copy() {
        DefaultBuilder<T> builder = new DefaultBuilder<>(objectFactory, factorySet);
        builder.properties.merge(properties);
        builder.mixIns.addAll(mixIns);
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
        return hash(DefaultBuilder.class, properties, mixIns);
    }

    //TODO missing type
    @Override
    public boolean equals(Object another) {
        return cast(another, DefaultBuilder.class)
                .map(builder -> properties.equals(builder.properties) && mixIns.equals(builder.mixIns))
                .orElseGet(() -> super.equals(another));
    }

    public void establishChildProducers(ObjectProducer<T> parent, Instance<T> instance) {
        forDefaultValue(parent, instance);
        forSpec(parent, instance);
        forInputProperties(parent);
    }

    private void forSpec(ObjectProducer<T> parent, Instance<T> instance) {
        objectFactory.collectSpec(mixIns, instance);
        instance.spec().apply(factorySet, parent);
    }

    private void forInputProperties(ObjectProducer<T> parent) {
        properties.toExpressions(objectFactory.getType())
                .forEach(exp -> parent.addChild(exp.getProperty(), exp.buildProducer(factorySet, parent)));
    }

    private void forDefaultValue(ObjectProducer<T> parent, Instance<T> instance) {
        parent.getType().getPropertyWriters().forEach((name, propertyWriter) ->
                factorySet.getObjectFactorySet().queryDefaultValueBuilder(propertyWriter.getType()).ifPresent(propertyValueFactory ->
                        parent.addChild(name, new DefaultValueProducer<>(parent.getType(), propertyValueFactory, instance.sub(name)))));
    }
}
