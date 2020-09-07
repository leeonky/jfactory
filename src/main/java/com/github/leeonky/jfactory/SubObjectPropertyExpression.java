package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.*;

import static java.util.Arrays.asList;

class SubObjectPropertyExpression<T> extends PropertyExpression<T> {
    private final Map<String, Object> conditionValues = new LinkedHashMap<>();
    private String[] mixIns;
    private String definition;

    public SubObjectPropertyExpression(String condition, Object value, String[] mixIns, String definition, String property, BeanClass<T> beanClass, String field) {
        super(property, beanClass, field);
        this.mixIns = mixIns;
        this.definition = definition;
        conditionValues.put(condition, value);
    }

    @Override
    protected boolean isMatch(BeanClass<?> propertyType, Object propertyValue) {
        return conditionValues.entrySet().stream()
                .map(conditionValue -> ExpressionParser.parse(propertyType, conditionValue.getKey(), conditionValue.getValue()))
                .allMatch(queryExpression -> queryExpression.isMatch(propertyValue));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Producer<?> buildProducer(FactorySet factorySet, Producer<T> parent, Instance<T> instance) {
        if (isIntently())
            return toBuilder(factorySet, beanClass.getPropertyWriter(property).getType().getElementOrPropertyType()).createProducer(property);
        Collection<?> collection = toBuilder(factorySet, beanClass.getPropertyReader(property).getType().getElementOrPropertyType()).queryAll();
        if (collection.isEmpty())
            return toBuilder(factorySet, beanClass.getPropertyWriter(property).getType().getElementOrPropertyType()).createProducer(property);
        else
            return new FixedValueProducer(parent.getType().getPropertyWriter(property).getType(), collection.iterator().next());
    }

    private Builder<?> toBuilder(FactorySet factorySet, BeanClass<?> propertyType) {
        return (definition != null ? factorySet.spec(definition) : factorySet.type(propertyType.getType()))
                .mixIn(mixIns).properties(conditionValues);
    }

    @Override
    public PropertyExpression<T> merge(PropertyExpression<T> propertyExpression) {
        return propertyExpression.mergeTo(this);
    }

    @Override
    protected PropertyExpression<T> mergeTo(SubObjectPropertyExpression<T> conditionValueSet) {
        conditionValueSet.conditionValues.putAll(conditionValues);
        conditionValues.clear();
        conditionValues.putAll(conditionValueSet.conditionValues);
        mergeMixIn(conditionValueSet);
        mergeDefinition(conditionValueSet);
        setIntently(isIntently() || conditionValueSet.isIntently());
        return this;
    }

    private void mergeMixIn(SubObjectPropertyExpression another) {
        if (mixIns.length != 0 && another.mixIns.length != 0
                && !new HashSet<>(asList(mixIns)).equals(new HashSet<>(asList(another.mixIns))))
            throw new IllegalArgumentException(String.format("Cannot merge different mix-in %s and %s for %s.%s",
                    Arrays.toString(mixIns), Arrays.toString(another.mixIns), beanClass.getName(), property));
        if (mixIns.length == 0)
            mixIns = another.mixIns;
    }

    private void mergeDefinition(SubObjectPropertyExpression<T> another) {
        if (definition != null && another.definition != null
                && !Objects.equals(definition, another.definition))
            throw new IllegalArgumentException(String.format("Cannot merge different definition `%s` and `%s` for %s.%s",
                    definition, another.definition, beanClass.getName(), property));
        if (definition == null)
            definition = another.definition;
    }
}
