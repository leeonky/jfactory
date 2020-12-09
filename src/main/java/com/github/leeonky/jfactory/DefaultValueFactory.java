package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import static java.lang.String.format;

public interface DefaultValueFactory<V> {
    <T> V create(BeanClass<T> beanType, SubInstance<T> instance);

    @SuppressWarnings("unchecked")
    default Class<V> getType() {
        return (Class<V>) BeanClass.create(getClass()).getSuper(DefaultValueFactory.class).getTypeArguments(0)
                .orElseThrow(() -> new IllegalStateException(format("Cannot guess type `%s` via generic type argument,"
                        + " please override DefaultValueFactory::getType", getClass().getName())))
                .getType();
    }
}
