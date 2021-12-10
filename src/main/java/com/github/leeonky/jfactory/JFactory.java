package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;
import com.github.leeonky.util.PropertyWriter;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class JFactory {
    final AliasSetStore aliasSetStore = new AliasSetStore();
    private final FactorySet factorySet = new FactorySet();
    private final DataRepository dataRepository;
    private final Set<Predicate<PropertyWriter<?>>> ignoreDefaultValues = new LinkedHashSet<>();

    public JFactory() {
        dataRepository = new MemoryDataRepository();
    }

    public JFactory(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    public static AliasSet alias(String alias, String target) {
        return new AliasSet(alias, target);
    }

    public DataRepository getDataRepository() {
        return dataRepository;
    }

    public <T> Factory<T> factory(Class<T> type) {
        return factory(BeanClass.create(type));
    }

    public <T> Factory<T> factory(BeanClass<T> type) {
        return factorySet.queryObjectFactory(type);
    }

    public <T> Builder<T> type(Class<T> type) {
        return type(BeanClass.create(type));
    }

    public <T> Builder<T> type(BeanClass<T> type) {
        return new DefaultBuilder<>(factorySet.queryObjectFactory(type), this);
    }

    public <T> Builder<T> type(TypeReference<T> type) {
        return type(type.getType());
    }

    public <T, S extends Spec<T>> Builder<T> spec(Class<S> specClass) {
        return new DefaultBuilder<>((ObjectFactory<T>) specFactory(specClass), this);
    }

    public <T, S extends Spec<T>> Builder<T> spec(Class<S> specClass, Consumer<S> trait) {
        return new DefaultBuilder<>(factorySet.createSpecFactory(specClass, trait), this);
    }

    public <T, S extends Spec<T>> JFactory register(Class<S> specClass) {
        factorySet.registerSpecClassFactory(specClass);
        return this;
    }

    public <T> Builder<T> spec(String... traitsAndSpec) {
        return new DefaultBuilder<>((ObjectFactory<T>) specFactory(traitsAndSpec[traitsAndSpec.length - 1]), this)
                .traits(Arrays.copyOf(traitsAndSpec, traitsAndSpec.length - 1));
    }

    public <T> Factory<T> specFactory(String specName) {
        return factorySet.querySpecClassFactory(specName);
    }

    public <T, S extends Spec<T>> Factory<T> specFactory(Class<S> specClass) {
        register(specClass);
        return factorySet.querySpecClassFactory(specClass);
    }

    public <T> T create(Class<T> type) {
        return type(type).create();
    }

    public <T, S extends Spec<T>> T createAs(Class<S> spec) {
        return spec(spec).create();
    }

    public <T, S extends Spec<T>> T createAs(Class<S> spec, Consumer<S> trait) {
        return spec(spec, trait).create();
    }

    public <T> T createAs(String... traitsAndSpec) {
        return this.<T>spec(traitsAndSpec).create();
    }

    public <T> JFactory registerDefaultValueFactory(Class<T> type, DefaultValueFactory<T> factory) {
        factorySet.registerDefaultValueFactory(type, factory);
        return this;
    }

    public JFactory ignoreDefaultValue(Predicate<PropertyWriter<?>> ignoreProperty) {
        ignoreDefaultValues.add(ignoreProperty);
        return this;
    }

    <T> boolean shouldCreateDefaultValue(PropertyWriter<T> propertyWriter) {
        return ignoreDefaultValues.stream().noneMatch(p -> p.test(propertyWriter));
    }

    public JFactory propertyAlias(Class<?> type, AliasSet aliasSet) {
        aliasSetStore.append(type, aliasSet);
        return this;
    }
}
