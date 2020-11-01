package com.github.leeonky.jfactory;

import com.github.leeonky.util.Property;

abstract class Expression<H> {
    protected final Property<H> property;
    private boolean intently = false;

    public Expression(Property<H> property) {
        this.property = property;
    }

    protected String getProperty() {
        return property.getName();
    }

    public boolean isMatch(H object) {
        return object != null && !isIntently() && isPropertyMatch(property.getValue(object));
    }

    protected abstract boolean isPropertyMatch(Object propertyValue);

    public abstract Producer<?> buildProducer(FactorySet factorySet, Producer<H> host);

    protected Expression<H> merge(Expression<H> expression) {
        return expression;
    }

    protected Expression<H> mergeBy(SubObjectExpression<H> conditionValueSet) {
        return this;
    }

    protected Expression<H> mergeBy(CollectionExpression<H, ?> collectionConditionValue) {
        return this;
    }

    protected boolean isIntently() {
        return intently;
    }

    protected Expression<H> setIntently(boolean intently) {
        this.intently = intently;
        return this;
    }
}
