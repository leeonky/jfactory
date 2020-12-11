package com.github.leeonky.jfactory;

import java.util.Collection;

public interface DataRepository extends Persistable {
    <T> Collection<T> queryAll(Class<T> type);

    void clear();
}
