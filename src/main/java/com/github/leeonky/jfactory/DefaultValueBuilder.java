package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import static java.lang.String.format;

public interface DefaultValueBuilder<V> {
    <T> V create(BeanClass<T> beanType, SubInstance instance);

    @SuppressWarnings("unchecked")
    default Class<V> getType() {
        return (Class<V>) BeanClass.create(getClass()).getSuper(DefaultValueBuilder.class).getTypeArguments(0)
                .orElseThrow(() -> new IllegalStateException(format("Cannot guess type `%s` via generic type argument,"
                        + " please override DefaultValueBuilder::getType", getClass().getName())))
                .getType();
    }
}
