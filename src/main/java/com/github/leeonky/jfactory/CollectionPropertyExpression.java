package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class CollectionPropertyExpression<T> extends PropertyExpression<T> {
    private final Map<Integer, PropertyExpression> conditionValueIndexMap = new LinkedHashMap<>();

    public CollectionPropertyExpression(int index, PropertyExpression<?> propertyExpression, String property, BeanClass<T> beanClass, String field) {
        super(property, beanClass, field);
        conditionValueIndexMap.put(index, propertyExpression);
    }

    @Override
    protected boolean isMatch(BeanClass<?> propertyType, Object propertyValue) {
        List<Object> elements = BeanClass.arrayCollectionToStream(propertyValue).collect(Collectors.toList());
        return conditionValueIndexMap.entrySet().stream()
                .allMatch(e -> elements.get(e.getKey()) != null && !e.getValue().isIntently() && e.getValue().isMatch(propertyType.getElementType(), elements.get(e.getKey())));
    }

    @Override
    public Producer<?> buildProducer(FactorySet factorySet, Instance<T> instance) {
        CollectionProducer<?, ?> producer = new CollectionProducer<>(factorySet.getObjectFactorySet(), beanClass, beanClass.getPropertyWriter(property).getType(), instance);
        conditionValueIndexMap.forEach((k, v) -> producer.addChild(k, v.buildProducer(factorySet, instance)));
        return producer;
    }

    @Override
    public PropertyExpression<T> merge(PropertyExpression<T> propertyExpression) {
        return propertyExpression.mergeTo(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected PropertyExpression<T> mergeTo(CollectionPropertyExpression<T> collectionConditionValue) {
        collectionConditionValue.conditionValueIndexMap.forEach((k, v) ->
                conditionValueIndexMap.put(k, conditionValueIndexMap.containsKey(k) ?
                        conditionValueIndexMap.get(k).merge(v)
                        : v));
        return this;
    }
}
