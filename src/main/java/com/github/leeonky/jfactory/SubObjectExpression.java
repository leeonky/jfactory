package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;
import com.github.leeonky.util.Property;

import java.util.Optional;

class SubObjectExpression<P> extends Expression<P> {
    private final KeyValueCollection properties;
    private final TraitsSpec traitsSpec;

    public SubObjectExpression(KeyValueCollection properties, TraitsSpec traitsSpec, Property<P> property) {
        super(property);
        this.properties = properties;
        this.traitsSpec = traitsSpec;
    }

    @Override
    protected boolean isPropertyMatch(Object propertyValue) {
        return properties.matcher(property.getReaderType()).matches(propertyValue);
    }

    @Override
    public Producer<?> buildProducer(JFactory JFactory, Producer<P> parent) {
        return query(JFactory).<Producer<?>>map(object -> new FixedValueProducer<>(property.getWriterType(), object))
                .orElseGet(() -> toBuilder(JFactory, property.getWriterType()).createProducer());
    }

    private Optional<?> query(JFactory JFactory) {
        if (intently)
            return Optional.empty();
        return toBuilder(JFactory, property.getReaderType()).queryAll().stream().findFirst();
    }

    private Builder<?> toBuilder(JFactory JFactory, BeanClass<?> propertyType) {
        return properties.apply(traitsSpec.toBuilder(JFactory, propertyType));
    }

    @Override
    public Expression<P> merge(Expression<P> another) {
        return another.mergeBy(this);
    }

    @Override
    protected Expression<P> mergeBy(SubObjectExpression<P> another) {
        properties.merge(another.properties);
        traitsSpec.merge(another.traitsSpec, property.toString());
        setIntently(intently || another.intently);
        return this;
    }
}
