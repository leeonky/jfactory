package com.github.leeonky.jfactory;

public interface PropertyValue {
    static PropertyValue table(String table) {
        return new TablePropertyValue(table);
    }

    <T> Builder<T> setToBuilder(String property, Builder<T> builder);
}
