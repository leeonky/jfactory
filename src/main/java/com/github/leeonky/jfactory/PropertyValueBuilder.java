package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

public interface PropertyValueBuilder<V> {
    <T> V create(BeanClass<T> type, Instance<T> instance);

    Class<V> getType();
}
