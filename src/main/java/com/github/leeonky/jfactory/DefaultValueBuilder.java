package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

public interface DefaultValueBuilder<V> {
    <T> V create(BeanClass<T> type, Instance<T> instance);

    @SuppressWarnings("unchecked")
    default Class<V> getType() {
        return (Class<V>) BeanClass.create(getClass()).getSuper(DefaultValueBuilder.class).getTypeArguments(0)
                .orElseThrow(() -> new IllegalStateException("Cannot guess type via generic type argument, please override DefaultValueBuilder::getType"))
                .getType();
    }
}
