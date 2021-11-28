package com.github.leeonky.jfactory;

public interface PropertyValue {

    <T> Builder<T> setToBuilder(String property, Builder<T> builder);
}
