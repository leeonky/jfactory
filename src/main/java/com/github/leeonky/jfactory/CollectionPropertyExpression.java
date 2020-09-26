package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class CollectionPropertyExpression<H, E, B> extends PropertyExpression<H, B> {
    private final Map<Integer, PropertyExpression<E, B>> conditionValueIndexMap = new LinkedHashMap<>();

    public CollectionPropertyExpression(int index, PropertyExpression<E, B> propertyExpression, String property,
                                        BeanClass<H> hostClass, BeanClass<B> beanClass) {
        super(property, hostClass, beanClass);
        conditionValueIndexMap.put(index, propertyExpression);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <P> boolean isMatch(BeanClass<P> propertyType, P propertyValue) {
        List<Object> elements = BeanClass.arrayCollectionToStream(propertyValue).collect(Collectors.toList());
        return conditionValueIndexMap.entrySet().stream()
                .allMatch(e -> elements.get(e.getKey()) != null && !e.getValue().isIntently()
                        && e.getValue().isMatch((BeanClass) propertyType.getElementType(), elements.get(e.getKey())));
    }

    @Override
    public Producer<?> buildProducer(FactorySet factorySet, Instance<B> instance) {
        CollectionProducer<?, ?> producer = new CollectionProducer<>(factorySet.getObjectFactorySet(), beanClass,
                hostClass.getPropertyWriter(property).getType(), instance);
        conditionValueIndexMap.forEach((k, v) -> producer.addChild(k.toString(), v.buildProducer(factorySet, instance)));
        return producer;
    }

    @Override
    public PropertyExpression<H, B> merge(PropertyExpression<H, B> propertyExpression) {
        return propertyExpression.mergeTo(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected PropertyExpression<H, B> mergeTo(CollectionPropertyExpression<H, ?, B> collectionConditionValue) {
        collectionConditionValue.conditionValueIndexMap.forEach((k, v) ->
                conditionValueIndexMap.put(k, conditionValueIndexMap.containsKey(k) ?
                        conditionValueIndexMap.get(k).merge((PropertyExpression<E, B>) v)
                        : (PropertyExpression<E, B>) v));
        return this;
    }
}
