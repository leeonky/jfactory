package com.github.leeonky.jfactory;

import com.github.leeonky.util.CollectionHelper;
import com.github.leeonky.util.Property;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        List<Object> elements = CollectionHelper.toStream(collection).collect(Collectors.toList());
        return elements.size() > children.keySet().stream().reduce(Integer::max).orElse(0) &&
                children.entrySet().stream().allMatch(e -> isMatch(e.getValue(), elements.get(e.getKey())));
    }

    private boolean isMatch(Expression<E> expression, Object value) {
        return value != null && !expression.intently && expression.isPropertyMatch(value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Producer<?> buildProducer(JFactory jFactory, Producer<P> parent) {
        CollectionProducer<?, E> producer = cast(parent.childOrDefault(property.getName()),
                CollectionProducer.class).orElseThrow(IllegalArgumentException::new);
        groupByAdjustedPositiveAndNegativeIndexExpression(producer).forEach((index, expressions) ->
                producer.changeChild(index.toString(), merge(expressions).buildProducer(jFactory, producer)));
        return producer;
    }

    private Map<Integer, List<Expression<E>>> groupByAdjustedPositiveAndNegativeIndexExpression(
            CollectionProducer<?, E> collectionProducer) {
        Map<Integer, List<Expression<E>>> result = new LinkedHashMap<>();
        for (Map.Entry<Integer, Expression<E>> entry : children.entrySet()) {
            int index = entry.getKey();
            int addedProducerCount = collectionProducer.fillCollectionWithDefaultValue(index);
            if (index < 0) {
                index = collectionProducer.childrenCount() + index;
                result = adjustIndexByInserted(result, addedProducerCount);
            }
            result.computeIfAbsent(index, k -> new ArrayList<>()).add(entry.getValue());
        }
        return result;
    }

    private LinkedHashMap<Integer, List<Expression<E>>> adjustIndexByInserted(
            Map<Integer, List<Expression<E>>> result, int addedProducerCount) {
        return result.entrySet().stream().collect(LinkedHashMap::new,
                (m, e) -> m.put(e.getKey() + addedProducerCount, e.getValue()), LinkedHashMap::putAll);
    }

    @Override
    public Expression<P> mergeTo(Expression<P> newExpression) {
        return newExpression.mergeFrom(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Expression<P> mergeFrom(CollectionExpression<P, ?> origin) {
        children.forEach((index, expression) ->
                origin.children.put(index, origin.children.containsKey(index) ?
                        origin.children.get(index).mergeTo((Expression) expression) : (Expression) expression));
        return origin;
    }

    @Override
    protected Expression<P> setIntently(boolean intently) {
        if (intently)
            children.values().forEach(c -> c.setIntently(true));
        return super.setIntently(intently);
    }
}
