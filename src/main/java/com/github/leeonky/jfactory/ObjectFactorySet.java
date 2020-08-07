package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;

class ObjectFactorySet {
    private final Map<Class<?>, PropertyValueBuilder<?>> propertyValueBuilders = new HashMap<Class<?>, PropertyValueBuilder<?>>() {{
        put(String.class, (type, instance) -> String.format("%s#%d", instance.getProperty(), instance.getSequence()));
    }};
    private final Map<Class<?>, ObjectFactory<?>> objectFactories = new HashMap<>();
    private final Map<Class<?>, SpecClassFactory<?>> specClassFactoriesWithType = new HashMap<>();
    private final Map<String, SpecClassFactory<?>> specClassFactoriesWithName = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> ObjectFactory<T> queryObjectFactory(Class<T> type) {
        return (ObjectFactory<T>) objectFactories.computeIfAbsent(type, key -> new ObjectFactory<>(BeanClass.create(key)));
    }

    @SuppressWarnings("unchecked")
    public <T> SpecClassFactory<T> registerSpecClassFactory(Class<? extends Spec<T>> specClass) {
        Spec<T> spec = BeanClass.newInstance(specClass);
        SpecClassFactory<?> specClassFactory = specClassFactoriesWithType.computeIfAbsent(specClass,
                type -> new SpecClassFactory<>(queryObjectFactory(spec.getType()), specClass));
        specClassFactoriesWithName.put(spec.getName(), specClassFactory);
        return (SpecClassFactory<T>) specClassFactory;
    }

    @SuppressWarnings("unchecked")
    public <T> SpecClassFactory<T> querySpecClassFactory(String specName) {
        return (SpecClassFactory<T>) specClassFactoriesWithName.computeIfAbsent(specName, key -> {
            throw new IllegalArgumentException("Spec `" + specName + "` not exist");
        });
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<PropertyValueBuilder<T>> queryPropertyValueFactory(Class<T> propertyType) {
        return ofNullable((PropertyValueBuilder<T>) propertyValueBuilders.get(propertyType));
    }
}
