package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

public class BuilderValueProducer<T> extends Producer<T> {
    private final Builder<T> builder;

    public BuilderValueProducer(BeanClass<T> type, Builder<T> builder) {
        super(type);
        this.builder = builder;
    }

    @Override
    protected T produce() {
        return BeanClass.getConverter().convert(getType().getType(), builder.query());
    }

    @Override
    public Producer<T> changeTo(Producer<T> newProducer) {
        if (newProducer instanceof BuilderValueProducer) {
            if (builder instanceof DefaultBuilder && ((BuilderValueProducer<Object>) newProducer).builder instanceof DefaultBuilder) {
                DefaultBuilder<T> marge = ((DefaultBuilder<T>) builder).marge((DefaultBuilder<T>) ((BuilderValueProducer<Object>) newProducer).builder);
                return new BuilderValueProducer<>(getType(), marge);
            }
//        TODO need test
            return newProducer;
        }
        if (newProducer instanceof ObjectProducer)
            return builder.createProducer().changeTo(newProducer);
//        TODO need test
        return this;
    }

    public Producer<?> getProducer() {
        Object[] objects = builder.queryAll().toArray();
        if (objects.length != 0)
            return new FixedValueProducer<>(getType(), objects[0]);
//        TODO need test
        return builder.createProducer();
    }
}
