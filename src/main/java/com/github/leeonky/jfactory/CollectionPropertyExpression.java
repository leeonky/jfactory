package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.leeonky.util.BeanClass.cast;

class CollectionPropertyExpression<H, E> extends PropertyExpression<H> {
    private final Map<Integer, PropertyExpression<E>> conditionValueIndexMap = new LinkedHashMap<>();

    public CollectionPropertyExpression(BeanClass<H> hostClass, String property,
                                        int index, PropertyExpression<E> propertyExpression) {
        super(property, hostClass);
        conditionValueIndexMap.put(index, propertyExpression);
    }

    @Override
    protected <P> boolean isMatch(BeanClass<P> propertyType, P propertyValue) {
        List<Object> elements = BeanClass.arrayCollectionToStream(propertyValue).collect(Collectors.toList());
        return conditionValueIndexMap.entrySet().stream()
                .allMatch(e -> isMatch(propertyType.getElementType(), e.getValue(), elements.get(e.getKey())));
    }

    @SuppressWarnings("unchecked")
    private boolean isMatch(BeanClass elementType, PropertyExpression<E> expression, Object value) {
        return value != null && !expression.isIntently() && expression.isMatch(elementType, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Producer<?> buildProducer(FactorySet factorySet, Producer<H> host) {
        CollectionProducer<?, E> collectionProducer = cast(host.getChildOrDefault(property), CollectionProducer.class)
                .orElseThrow(IllegalArgumentException::new);
        conditionValueIndexMap.forEach((k, v) -> collectionProducer.addChild(k.toString(),
                v.buildProducer(factorySet, collectionProducer)));
        return collectionProducer;
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
