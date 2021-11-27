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
    private final JFactory jFactory;
    private final Set<String> traits = new LinkedHashSet<>();

    private final KeyValueCollection properties = new KeyValueCollection();
    private final DefaultArguments arguments = new DefaultArguments();

    public DefaultBuilder(ObjectFactory<T> objectFactory, JFactory jFactory) {
        this.jFactory = jFactory;
        this.objectFactory = objectFactory;
    }

    @Override
    public T create() {
        return createProducer().doDependenciesAndLinks().getValue();
    }

    @Override
    public ObjectProducer<T> createProducer() {
        return new ObjectProducer<>(jFactory, objectFactory, this);
    }

    @Override
    public Builder<T> arg(String key, Object value) {
        DefaultBuilder<T> newBuilder = clone();
        newBuilder.arguments.put(key, value);
        return newBuilder;
    }

    @Override
    public Builder<T> args(Arguments arguments) {
        DefaultBuilder<T> newBuilder = clone();
        newBuilder.arguments.merge((DefaultArguments) arguments);
        return newBuilder;
    }

    @Override
    public Builder<T> args(Map<String, ?> args) {
        DefaultBuilder<T> newBuilder = clone();
        args.forEach(newBuilder.arguments::put);
        return newBuilder;
    }

    @Override
    public Builder<T> args(String property, Map<String, Object> args) {
        DefaultBuilder<T> newBuilder = clone();
        args.forEach((key, value) -> newBuilder.arguments.put(property, key, value));
        return newBuilder;
    }

    @Override
    public Builder<T> propertyValue(String property, PropertyValue value) {
        return value.assignTo(property, this);
    }

    @Override
    public Builder<T> traits(String... traits) {
        DefaultBuilder<T> newBuilder = clone();
        newBuilder.traits.addAll(asList(traits));
        return newBuilder;
    }

    @Override
    public DefaultBuilder<T> clone() {
        DefaultBuilder<T> builder = new DefaultBuilder<>(objectFactory, jFactory);
        builder.properties.merge(properties);
        builder.traits.addAll(traits);
        builder.arguments.merge(arguments);
        return builder;
    }

    @Override
    public Builder<T> properties(Map<String, ?> properties) {
        DefaultBuilder<T> newBuilder = clone();
        properties.forEach(newBuilder.properties::append);
        return newBuilder;
    }

    @Override
    public Collection<T> queryAll() {
        KeyValueCollection.Matcher<T> matcher = properties.matcher(objectFactory.getType());
        return jFactory.getDataRepository().queryAll(objectFactory.getType().getType()).stream()
                .filter(matcher::matches).collect(Collectors.toList());
    }

    @Override
    public int hashCode() {
        return hash(DefaultBuilder.class, objectFactory, properties, traits);
    }

    @Override
    public boolean equals(Object another) {
        return cast(another, DefaultBuilder.class)
                .map(builder -> objectFactory.equals(builder.objectFactory) && properties.equals(builder.properties)
                        && traits.equals(builder.traits))
                .orElseGet(() -> super.equals(another));
    }

    public void establishSpecProducers(ObjectProducer<T> objectProducer, Instance<T> instance) {
        forSpec(objectProducer, instance);
        forInputProperties(objectProducer);
    }

    private void forSpec(ObjectProducer<T> objectProducer, Instance<T> instance) {
        objectFactory.collectSpec(traits, instance);
        instance.spec().apply(jFactory, objectProducer);
    }

    private void forInputProperties(ObjectProducer<T> objectProducer) {
        properties.expressions(objectFactory.getType()).forEach(exp ->
                objectProducer.changeChild(exp.getProperty(), exp.buildProducer(jFactory, objectProducer)));
    }

    public DefaultBuilder<T> clone(DefaultBuilder<T> another) {
        if (another.objectFactory instanceof SpecClassFactory)
            return another;
        DefaultBuilder<T> newBuilder = clone();
        newBuilder.properties.merge(another.properties);
        newBuilder.traits.addAll(another.traits);
        return newBuilder;
    }

    public DefaultArguments getArguments() {
        return arguments;
    }
}
