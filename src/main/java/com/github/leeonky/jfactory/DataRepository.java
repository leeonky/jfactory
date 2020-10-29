package com.github.leeonky.jfactory;

import java.util.Collection;

public interface DataRepository {
    void save(Object object);

    <T> Collection<T> queryAll(Class<T> type);

    void clear();
}
