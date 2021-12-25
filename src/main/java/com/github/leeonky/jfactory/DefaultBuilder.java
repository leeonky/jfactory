package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.*;
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
    private int collectionSize = 0;

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
        properties.forEach((key, value) -> {
            String property = replaceStartsWithIndexBracket(
                    jFactory.aliasSetStore.evaluate(objectFactory.getType(), key, isCollection(value)), newBuilder);
            if (isCollection(value)) {
                List<Object> objects = BeanClass.arrayCollectionToStream(value).collect(Collectors.toList());
                if (objects.isEmpty() || !property.contains("$"))
                    newBuilder.properties.append(trimIndexAlias(property), objects);
                else for (int i = 0; i < objects.size(); i++)
                    newBuilder.properties.append(property.replaceFirst("\\$", String.valueOf(i)), objects.get(i));
            } else
                newBuilder.properties.append(property, value);
        });
        return newBuilder;
    }

    private String trimIndexAlias(String property) {
        if (property.contains("[$]"))
            return property.substring(0, property.indexOf("[$]"));
        return property;
    }

    private boolean isCollection(Object value) {
        return value != null && BeanClass.createFrom(value).isCollection();
    }

    private String replaceStartsWithIndexBracket(String key, DefaultBuilder<T> newBuilder) {
        if (key.startsWith("[")) {
            String[] indexAndSub = key.substring(1).split("]", 2);
            newBuilder.collectionSize = Math.max(newBuilder.collectionSize, Integer.parseInt(indexAndSub[0]) + 1);
            return indexAndSub[0] + indexAndSub[1];
        }
        return key;
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

    public void processSpecAndInputProperty(ObjectProducer<T> objectProducer, RootInstance<T> instance) {
        collectSpec(objectProducer, instance);
        processInputProperty(objectProducer);
        instance.setCollectionSize(collectionSize);
    }

    private void collectSpec(ObjectProducer<T> objectProducer, Instance<T> instance) {
        objectFactory.collectSpec(traits, instance);
        instance.spec().apply(jFactory, objectProducer);
        objectProducer.processSpecIgnoreProperties();
    }

    private void processInputProperty(ObjectProducer<T> producer) {
        properties.expressions(objectFactory.getType()).forEach(exp -> producer.changeChild(exp.getProperty(),
                intentlyCreateWhenReverseAssociation(producer, exp).buildProducer(jFactory, producer)));
    }

    private Expression<T> intentlyCreateWhenReverseAssociation(ObjectProducer<T> producer, Expression<T> exp) {
        return producer.isReverseAssociation(exp.getProperty()) ? exp.setIntently(true) : exp;
    }

    public DefaultBuilder<T> clone(DefaultBuilder<T> another) {
        if (another.objectFactory instanceof SpecClassFactory)
            return another;
        DefaultBuilder<T> newBuilder = clone();
        newBuilder.properties.merge(another.properties);
        newBuilder.traits.addAll(another.traits);
        newBuilder.collectionSize = collectionSize;
        return newBuilder;
    }

    public DefaultArguments getArguments() {
        return arguments;
    }
}
