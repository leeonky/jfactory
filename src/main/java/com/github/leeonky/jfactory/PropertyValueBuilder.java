package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

public interface PropertyValueBuilder<V> {
    V create(BeanClass type, Instance instance);
}
