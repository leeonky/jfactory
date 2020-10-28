package com.github.leeonky.jfactory;

import com.github.leeonky.util.Property;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.leeonky.util.BeanClass.arrayCollectionToStream;
import static com.github.leeonky.util.BeanClass.cast;

class CollectionPropertyExpression<H, E> extends PropertyExpression<H> {
    private final Map<Integer, PropertyExpression<E>> conditionValueIndexMap = new LinkedHashMap<>();

    public CollectionPropertyExpression(int index, PropertyExpression<E> elementExpression, Property<H> property) {
        super(property);
        conditionValueIndexMap.put(index, elementExpression);
    }

    @Override
    protected boolean isPropertyMatch(Object propertyValue) {
        List<Object> elements = arrayCollectionToStream(propertyValue).collect(Collectors.toList());
        return conditionValueIndexMap.entrySet().stream().allMatch(e -> isMatch(e.getValue(), elements.get(e.getKey())));
    }

    private boolean isMatch(PropertyExpression<E> expression, Object value) {
        return value != null && !expression.isIntently() && expression.isPropertyMatch(value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Producer<?> buildProducer(FactorySet factorySet, Producer<H> host) {
        CollectionProducer<?, E> producer = cast(host.getChildOrDefault(property.getName()), CollectionProducer.class)
                .orElseThrow(IllegalArgumentException::new);
        conditionValueIndexMap.forEach((k, v) -> producer.addChild(k.toString(), v.buildProducer(factorySet, producer)));
        return producer;
    }

    @Override
    public PropertyExpression<H> merge(PropertyExpression<H> propertyExpression) {
        return propertyExpression.mergeBy(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected PropertyExpression<H> mergeBy(CollectionPropertyExpression<H, ?> collectionConditionValue) {
        collectionConditionValue.conditionValueIndexMap.forEach((index, expression) ->
                conditionValueIndexMap.put(index, mergeOrAssign(index, (PropertyExpression<E>) expression)));
        return this;
    }

    private PropertyExpression<E> mergeOrAssign(Integer index, PropertyExpression<E> propertyExpression) {
        return conditionValueIndexMap.containsKey(index) ?
                propertyExpression.merge(conditionValueIndexMap.get(index)) : propertyExpression;
    }
}
