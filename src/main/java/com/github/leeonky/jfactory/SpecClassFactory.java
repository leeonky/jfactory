package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;
import com.github.leeonky.util.Suppressor;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

class SpecClassFactory<T> extends ObjectFactory<T> {
    private final ObjectFactory<T> base;
    private final Class<? extends Spec<T>> specClass;

    public SpecClassFactory(ObjectFactory<T> base, Class<? extends Spec<T>> specClass) {
        super(BeanClass.create(BeanClass.newInstance(specClass).getType()));
        this.specClass = specClass;
        this.base = base;
        registerMixIns();
        constructor(base::create);
    }

    @Override
    public Spec<T> createSpec() {
        return BeanClass.newInstance(specClass);
    }

    private void registerMixIns() {
        Stream.of(specClass.getMethods())
                .filter(method -> method.getAnnotation(MixIn.class) != null)
                .forEach(method -> spec(getMixInName(method),
                        instance -> Suppressor.run(() -> method.invoke(instance.spec()))));
    }

    private String getMixInName(Method method) {
        MixIn annotation = method.getAnnotation(MixIn.class);
        return annotation.value().isEmpty() ? method.getName() : annotation.value();
    }

    @Override
    public void collectSpec(Collection<String> mixIns, Instance<T> instance) {
        base.collectSpec(Collections.emptyList(), instance);
        super.collectSpec(mixIns, instance);
    }
}
