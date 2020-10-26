package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.leeonky.jfactory.PropertyExpression.createPropertyExpressions;

public interface DataRepository {
    void save(Object object);

    <T> Collection<T> queryAll(Class<T> type);

    default <T> Collection<T> query(BeanClass<T> type, Map<String, Object> criteria) {
        Collection<PropertyExpression<T>> expressions = createPropertyExpressions(type, criteria).values();
        return queryAll(type.getType()).stream()
                .filter(o -> expressions.stream().allMatch(e -> e.isMatch(o))).collect(Collectors.toList());
    }

    void clear();
}
