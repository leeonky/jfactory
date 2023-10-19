package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class ObjectFactory<T> implements Factory<T> {
    protected final FactorySet factorySet;
    private final BeanClass<T> type;
    private final Map<String, Consumer<Instance<T>>> traits = new LinkedHashMap<>();
    private final Map<String, Pattern> traitPatterns = new LinkedHashMap<>();
    private final Map<String, Transformer> transformers = new LinkedHashMap<>();
    private final Transformer passThrough = input -> input;
    private Function<Instance<T>, T> constructor = this::defaultConstruct;
    private Consumer<Instance<T>> spec = (instance) -> {
    };

    public ObjectFactory(BeanClass<T> type, FactorySet factorySet) {
        this.type = type;
        this.factorySet = factorySet;
    }

    @SuppressWarnings("unchecked")
    private T defaultConstruct(Instance<T> instance) {
        return getType().isCollection()
                ? (T) getType().createCollection(Collections.nCopies(instance.collectionSize(), null))
                : getType().newInstance();
    }

    protected Spec<T> createSpec() {
        return new Spec<>();
    }

    @Override
    public Factory<T> constructor(Function<Instance<T>, T> constructor) {
        this.constructor = Objects.requireNonNull(constructor);
        return this;
    }

    @Override
    public Factory<T> spec(Consumer<Instance<T>> spec) {
        this.spec = Objects.requireNonNull(spec);
        return this;
    }

    @Override
    public Factory<T> spec(String name, Consumer<Instance<T>> trait) {
        traits.put(name, Objects.requireNonNull(trait));
        traitPatterns.put(name, Pattern.compile(name));
        return this;
    }

    public final T create(Instance<T> instance) {
        return constructor.apply(instance);
    }

    @Override
    public BeanClass<T> getType() {
        return type;
    }

    @Override
    public Factory<T> transformer(String property, Transformer transformer) {
        transformers.put(property, transformer);
        return this;
    }

    private static class TraitExecutor<T> {
        private final Consumer<Instance<T>> action;
        private final List<Object> args = new ArrayList<>();

        public TraitExecutor(Matcher matcher, Consumer<Instance<T>> action) {
            for (int i = 0; i < matcher.groupCount(); i++)
                args.add(matcher.group(i + 1));
            this.action = action;
        }

        public TraitExecutor(Consumer<Instance<T>> action) {
            this.action = action;
        }

        public void execute(Instance<T> instance) {
            ((RootInstance<T>) instance).runTraitWithParams(args.toArray(), action);
        }
    }

    public void collectSpec(Collection<String> traits, Instance<T> instance) {
        spec.accept(instance);
        collectSubSpec(instance);
        for (String name : traits)
            queryTrait(name).execute(instance);
    }

    private TraitExecutor<T> queryTrait(String name) {
        Consumer<Instance<T>> action = traits.get(name);
        if (action != null)
            return new TraitExecutor<>(action);
        List<Matcher> matchers = traitPatterns.values().stream().map(p -> p.matcher(name)).filter(Matcher::matches)
                .collect(Collectors.toList());
        if (matchers.size() == 1)
            return new TraitExecutor<>(matchers.get(0), traits.get(matchers.get(0).pattern().pattern()));
        if (matchers.size() > 1)
            throw new IllegalArgumentException(String.format("Ambiguous trait pattern: %s, candidates are:\n%s", name,
                    matchers.stream().map(p -> "  " + p.pattern().pattern()).collect(Collectors.joining("\n"))));
        throw new IllegalArgumentException("Trait `" + name + "` not exist");
    }

    protected void collectSubSpec(Instance<T> instance) {
    }

    public RootInstance<T> createInstance(DefaultArguments argument) {
        Spec<T> spec = createSpec();
        RootInstance<T> rootInstance = new RootInstance<>(factorySet.nextSequence(type.getType()), spec, argument);
        spec.setInstance(rootInstance);
        return rootInstance;
    }

    public FactorySet getFactorySet() {
        return factorySet;
    }

    public ObjectFactory<T> getBase() {
        return this;
    }

    public Object transform(String name, Object value) {
        return queryTransformer(name, () -> passThrough).checkAndTransform(value);
    }

    protected Transformer queryTransformer(String name, Supplier<Transformer> fallback) {
        return transformers.getOrDefault(name, fallback(name, fallback).get());
    }

    protected Supplier<Transformer> fallback(String name, Supplier<Transformer> fallback) {
        return () -> type.getType().getSuperclass() == null ? fallback.get()
                : factorySet.queryObjectFactory(BeanClass.create(type.getType().getSuperclass()))
                .queryTransformer(name, fallback);
    }
}
