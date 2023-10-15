package com.github.leeonky.jfactory;

import com.github.leeonky.util.Property;

import java.util.List;

abstract class Expression<P> {
    protected final Property<P> property;
    protected boolean intently = false;

    public Expression(Property<P> property) {
        this.property = property;
    }

    protected String getProperty() {
        return property.getName();
    }

    public boolean isMatch(P object) {
        return object != null && !intently && isPropertyMatch(property.getValue(object));
    }

    protected abstract boolean isPropertyMatch(Object propertyValue);

    public abstract Producer<?> buildProducer(JFactory jFactory, Producer<P> parent);

    static <T> Expression<T> merge(List<Expression<T>> expressions) {
        return expressions.stream().reduce(Expression::mergeTo).get();
    }

    protected Expression<P> mergeTo(Expression<P> newExpression) {
        return newExpression;
    }

    protected Expression<P> mergeFrom(SubObjectExpression<P> origin) {
        return this;
    }

    protected Expression<P> mergeFrom(CollectionExpression<P, ?> origin) {
        return this;
    }

    protected Expression<P> setIntently(boolean intently) {
        this.intently = intently;
        return this;
    }
}
