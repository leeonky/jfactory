package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.github.leeonky.util.Suppressor.run;

class SpecClassFactory<T> extends ObjectFactory<T> {
    private final Class<? extends Spec<T>> specClass;
    private final Supplier<ObjectFactory<T>> base;

    public SpecClassFactory(Class<? extends Spec<T>> specClass, FactorySet factorySet, boolean globalSpec) {
        super(BeanClass.create(BeanClass.newInstance(specClass).getType()), factorySet);
        this.specClass = specClass;
        base = guessBaseFactory(factorySet, globalSpec);
        registerTraits();
        constructor(getBase()::create);
    }

    private Supplier<ObjectFactory<T>> guessBaseFactory(FactorySet factorySet, boolean globalSpec) {
        if (!globalSpec)
            return () -> factorySet.queryObjectFactory(getType());
        ObjectFactory<T> typeBaseFactory = factorySet.queryObjectFactory(getType()); // DO NOT INLINE
        return () -> typeBaseFactory;
    }

    @Override
    protected Spec<T> createSpec() {
        return BeanClass.newInstance(specClass);
    }

    private void registerTraits() {
        Stream.of(specClass.getMethods())
                .filter(this::isTraitMethod)
                .forEach(method -> spec(getTraitName(method), instance -> run(() -> method.invoke(instance.spec()))));
    }

    private boolean isTraitMethod(Method method) {
        return method.getAnnotation(Trait.class) != null;
    }

    private String getTraitName(Method method) {
        Trait annotation = method.getAnnotation(Trait.class);
        return annotation.value().isEmpty() ? method.getName() : annotation.value();
    }

    @Override
    public void collectSpec(Collection<String> traits, Instance<T> instance) {
        super.collectSpec(traits, instance);
        getBase().collectSpec(Collections.emptyList(), instance);
        collectClassSpec(instance, Spec::main);
    }

    protected void collectClassSpec(Instance<T> instance, Consumer<Spec<T>> consumer) {
        if (instance.spec().getClass().equals(specClass))
            consumer.accept(instance.spec());
        else {
            Spec<T> spec = createSpec().setInstance(instance);
            consumer.accept(spec);
            instance.spec().append(spec);
        }
    }

    @Override
    public ObjectFactory<T> getBase() {
        return base.get();
    }

    public Class<? extends Spec<T>> getSpecClass() {
        return specClass;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Supplier<Transformer> fallback(String name, Supplier<Transformer> fallback) {
        return () -> specClass.getSuperclass().equals(Spec.class) ? getBase().queryTransformer(name, fallback)
                : factorySet.querySpecClassFactory((Class<? extends Spec<T>>) specClass.getSuperclass())
                .queryTransformer(name, fallback);
    }
}
