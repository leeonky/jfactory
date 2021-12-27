package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

import static com.github.leeonky.util.Suppressor.run;

class SpecClassFactory<T> extends ObjectFactory<T> {
    private final ObjectFactory<T> base;
    private final Class<? extends Spec<T>> specClass;

    public SpecClassFactory(ObjectFactory<T> base, Class<? extends Spec<T>> specClass, FactorySet factorySet) {
        super(BeanClass.create(BeanClass.newInstance(specClass).getType()), factorySet);
        this.specClass = specClass;
        this.base = base;
        registerTraits();
        constructor(base::create);
    }

    @Override
    public Spec<T> createSpec() {
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
        collectClassSpec(instance);
        base.collectSpec(Collections.emptyList(), instance);
        super.collectSpec(traits, instance);
    }

    protected void collectClassSpec(Instance<T> instance) {
        instance.spec().main();
    }

    @Override
    public ObjectFactory<T> getBase() {
        return base;
    }
}
