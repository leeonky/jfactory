package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;
import com.github.leeonky.util.PropertyReader;
import com.github.leeonky.util.PropertyWriter;

public class Property<Bean> {
    private final String property;
    private final BeanClass<Bean> beanType;

    Property(BeanClass<Bean> beanType, String property) {
        this.beanType = beanType;
        this.property = property;
    }

    public PropertyReader<Bean> getReader() {
        return beanType.getPropertyReader(property);
    }

    public Object getValue(Bean object) {
        return beanType.getPropertyValue(object, property);
    }

    public String getName() {
        return property;
    }

    public PropertyWriter<Bean> getWriter() {
        return beanType.getPropertyWriter(property);
    }

    public BeanClass<Bean> getBeanType() {
        return beanType;
    }

    @SuppressWarnings("unchecked")
    public <P> BeanClass<P> getReaderType() {
        return (BeanClass<P>) getReader().getType();
    }

    @SuppressWarnings("unchecked")
    public <P> BeanClass<P> getWriterType() {
        return (BeanClass<P>) getWriter().getType();
    }
}