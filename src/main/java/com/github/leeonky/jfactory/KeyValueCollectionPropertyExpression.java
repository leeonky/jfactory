package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.*;

import static java.util.Arrays.asList;

class KeyValueCollectionPropertyExpression<T> extends PropertyExpression<T> {
    private final Map<String, Object> conditionValues = new LinkedHashMap<>();
    private String[] mixIns;
    private String definition;

    public KeyValueCollectionPropertyExpression(String condition, Object value, String[] mixIns, String definition, String property, BeanClass<T> beanClass) {
        super(property, beanClass);
        this.mixIns = mixIns;
        this.definition = definition;
        conditionValues.put(condition, value);
    }

    @Override
    public boolean matches(BeanClass<?> type, Object propertyValue) {
        return conditionValues.entrySet().stream()
                .map(conditionValue -> create(type, conditionValue.getKey(), conditionValue.getValue()))
                .allMatch(queryExpression -> queryExpression.objectMatches(propertyValue));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Producer<?> buildProducer(FactorySet factorySet, Producer<T> parent, Instance<T> instance) {
//            if (isIntently())
//                return toBuilder(factorySet, beanClass.getPropertyWriter(property).getElementOrPropertyType()).producer(property);
        Collection<?> collection = toBuilder(factorySet, beanClass.getPropertyReader(property).getType().getElementOrPropertyType()).queryAll();
        if (collection.isEmpty())
            return toBuilder(factorySet, beanClass.getPropertyWriter(property).getType().getElementOrPropertyType()).createProducer(property);
        else
            return new FixedValueProducer(parent.getType().getPropertyWriter(property).getType(), collection.iterator().next());
    }

    private Builder<?> toBuilder(FactorySet factorySet, BeanClass<?> propertyType) {
//            return (definition != null ?
//                    factorySet.toBuild(definition)
//                    : factorySet.type(propertyType))
//                    .mixIn(mixIns).properties(conditionValues);
        return factorySet.type(propertyType.getType()).properties(conditionValues);
    }

    @Override
    public PropertyExpression<T> merge(PropertyExpression<T> propertyExpression) {
        return propertyExpression.mergeTo(this);
    }

    @Override
    protected PropertyExpression<T> mergeTo(KeyValueCollectionPropertyExpression<T> conditionValueSet) {
        conditionValueSet.conditionValues.putAll(conditionValues);
        conditionValues.clear();
        conditionValues.putAll(conditionValueSet.conditionValues);
        mergeMixIn(conditionValueSet);
        mergeDefinition(conditionValueSet);
        setIntently(isIntently() || conditionValueSet.isIntently());
        return this;
    }

    private void mergeMixIn(KeyValueCollectionPropertyExpression another) {
        if (mixIns.length != 0 && another.mixIns.length != 0
                && !new HashSet<>(asList(mixIns)).equals(new HashSet<>(asList(another.mixIns))))
            throw new IllegalArgumentException(String.format("Cannot merge different mix-in %s and %s for %s.%s",
                    Arrays.toString(mixIns), Arrays.toString(another.mixIns), beanClass.getName(), property));
        if (mixIns.length == 0)
            mixIns = another.mixIns;
    }

    private void mergeDefinition(KeyValueCollectionPropertyExpression<T> another) {
        if (definition != null && another.definition != null
                && !Objects.equals(definition, another.definition))
            throw new IllegalArgumentException(String.format("Cannot merge different definition `%s` and `%s` for %s.%s",
                    definition, another.definition, beanClass.getName(), property));
        if (definition == null)
            definition = another.definition;
    }
}
