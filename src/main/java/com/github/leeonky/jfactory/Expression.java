package com.github.leeonky.jfactory;

import com.github.leeonky.util.Property;

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

    public abstract Producer<?> buildProducer(FactorySet factorySet, Producer<P> parent);

    protected Expression<P> merge(Expression<P> another) {
        return another;
    }

    protected Expression<P> mergeBy(SubObjectExpression<P> another) {
        return this;
    }

    protected Expression<P> mergeBy(CollectionExpression<P, ?> another) {
        return this;
    }

    protected Expression<P> setIntently(boolean intently) {
        this.intently = intently;
        return this;
    }
}
