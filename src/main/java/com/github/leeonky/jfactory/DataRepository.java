package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public interface DataRepository {
    void save(Object object);

    <T> Collection<T> queryAll(Class<T> type);

    default <T> Collection<T> query(BeanClass<T> beanClass, Map<String, Object> criteria) {
        Collection<PropertyExpression<T>> expressions = PropertyExpression.createPropertyExpressions(beanClass, criteria).values();
        return queryAll(beanClass.getType()).stream()
                .filter(o -> expressions.stream().allMatch(e -> e.objectMatches(o)))
                .collect(Collectors.toList());
    }

    void clear();
}
