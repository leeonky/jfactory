package com.github.leeonky.jfactory;

import com.github.leeonky.util.Property;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.leeonky.util.BeanClass.arrayCollectionToStream;
import static com.github.leeonky.util.BeanClass.cast;

class CollectionExpression<P, E> extends Expression<P> {
    private final Map<Integer, Expression<E>> children = new LinkedHashMap<>();

    public CollectionExpression(Property<P> property, int index, Expression<E> elementExpression) {
        super(property);
        children.put(index, elementExpression);
    }

    @Override
    protected boolean isPropertyMatch(Object collection) {
        if (collection == null)
            return false;
        List<Object> elements = arrayCollectionToStream(collection).collect(Collectors.toList());
        return elements.size() > children.keySet().stream().reduce(Integer::max).orElse(0) &&
                children.entrySet().stream().allMatch(e -> isMatch(e.getValue(), elements.get(e.getKey())));
    }

    private boolean isMatch(Expression<E> expression, Object value) {
        return value != null && !expression.intently && expression.isPropertyMatch(value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Producer<?> buildProducer(JFactory jFactory, Producer<P> parent) {
        CollectionProducer<?, E> collectionProducer = cast(parent.childOrDefault(property.getName()),
                CollectionProducer.class).orElseThrow(IllegalArgumentException::new);
        children.forEach((k, v) ->
                collectionProducer.setChild(k.toString(), v.buildProducer(jFactory, collectionProducer)));
        return collectionProducer;
    }

    @Override
    public Expression<P> merge(Expression<P> another) {
        return another.mergeBy(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Expression<P> mergeBy(CollectionExpression<P, ?> another) {
        another.children.forEach((index, expression) ->
                children.put(index, mergeOrAssign(index, (Expression<E>) expression)));
        return this;
    }

    private Expression<E> mergeOrAssign(Integer index, Expression<E> expression) {
        return children.containsKey(index) ? expression.merge(children.get(index)) : expression;
    }
}
