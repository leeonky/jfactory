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
    public Producer<?> buildProducer(JFactory jFactory, Producer<P> parent) {
        Builder<?> builder = toBuilder(jFactory, property.getWriterType());
        return query(jFactory).<Producer<?>>map(object -> new BuilderValueProducer<>(property.getWriterType(), builder))
                .orElseGet(builder::createProducer);
    }

    private Optional<?> query(JFactory jFactory) {
        if (intently)
            return Optional.empty();
        return toBuilder(jFactory, property.getReaderType()).queryAll().stream().findFirst();
    }

    private Builder<?> toBuilder(JFactory jFactory, BeanClass<?> propertyType) {
        return properties.apply(traitsSpec.toBuilder(jFactory, propertyType));
    }

    @Override
    public Expression<P> mergeTo(Expression<P> newExpression) {
        return newExpression.mergeFrom(this);
    }

    @Override
    protected Expression<P> mergeFrom(SubObjectExpression<P> origin) {
        properties.insertAll(origin.properties);
        traitsSpec.merge(origin.traitsSpec, property.toString());
        setIntently(intently || origin.intently);
        return this;
    }
}
