package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

public interface PropertyValueBuilder<V> {
    <T> V create(BeanClass<T> type, Instance<T> instance);

    @SuppressWarnings("unchecked")
    default Class<V> getType() {
        return (Class<V>) BeanClass.create(getClass()).getSuper(PropertyValueBuilder.class).getTypeArguments(0)
                .orElseThrow(() -> new IllegalStateException("Cannot guess type via generic type argument, please override PropertyValueBuilder::getType"))
                .getType();
    }
}
