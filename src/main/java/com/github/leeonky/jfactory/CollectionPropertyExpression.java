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
    @SuppressWarnings("unchecked")
    public Producer<?> buildProducer(FactorySet factorySet, Producer<H> host, Instance<B> instance) {
        Producer<E> existProducer = (Producer<E>) host.getChildOrDefault(property);
        if (!(existProducer instanceof CollectionProducer))
            throw new IllegalArgumentException();
        conditionValueIndexMap.forEach((k, v) -> existProducer.addChild(k.toString(),
                v.buildProducer(factorySet, existProducer, instance)));
        return existProducer;
    }

    @Override
    public PropertyExpression<H, B> merge(PropertyExpression<H, B> propertyExpression) {
        return propertyExpression.mergeBy(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected PropertyExpression<H, B> mergeBy(CollectionPropertyExpression<H, ?, B> collectionConditionValue) {
        collectionConditionValue.conditionValueIndexMap.forEach((index, expression) ->
                conditionValueIndexMap.put(index, mergeOrAssign(index, (PropertyExpression<E, B>) expression)));
        return this;
    }

    private PropertyExpression<E, B> mergeOrAssign(Integer k, PropertyExpression<E, B> v) {
        return conditionValueIndexMap.containsKey(k) ? v.merge(conditionValueIndexMap.get(k)) : v;
    }
}
