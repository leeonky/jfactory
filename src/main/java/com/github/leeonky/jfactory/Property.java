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
        return getBeanType().getPropertyReader(getProperty());
    }

    public Object getValue(Bean object) {
        return getBeanType().getPropertyValue(object, getProperty());
    }

    public String getProperty() {
        return property;
    }

    public PropertyWriter<Bean> getWriter() {
        return getBeanType().getPropertyWriter(property);
    }

    public BeanClass<Bean> getBeanType() {
        return beanType;
    }
}