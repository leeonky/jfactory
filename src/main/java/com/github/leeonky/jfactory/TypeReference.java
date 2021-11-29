package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;
import com.github.leeonky.util.GenericType;

public class TypeReference<T> {
    @SuppressWarnings("unchecked")
    public BeanClass<T> getType() {
        return (BeanClass<T>) BeanClass.create(GenericType.createGenericType(getClass().getGenericSuperclass())).getTypeArguments(0)
                .orElseThrow(() -> new IllegalArgumentException("Cannot guess type, use new TypeReference<T>() {}"));
    }
}
