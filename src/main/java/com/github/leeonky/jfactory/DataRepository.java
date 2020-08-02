package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface DataRepository {
    void save(Object object);

    <T> Collection<T> queryAll(Class<T> type);

    default <T> Collection<T> query(BeanClass<T> beanClass, Map<String, Object> criteria) {
        List<QueryExpression<T>> expressions = QueryExpression.createQueryExpressions(beanClass, criteria);
        return queryAll(beanClass.getType()).stream()
                .filter(o -> expressions.stream().allMatch(e -> e.matches(o)))
                .collect(Collectors.toList());
    }

    void clear();
}
