package com.github.leeonky.jfactory;

import org.junit.jupiter.api.Test;

class JFactoryTest {

    @Test
    void supported_generic_signature() {
        JFactory jFactory = new JFactory();
        Builder<Bean> beanBuilder = jFactory.type(Bean.class);
        Builder<?> builderInAnyType = jFactory.type((Class<?>) Bean.class);
        Builder<Bean> beanSpecBuilder = jFactory.spec(BeanSpec.class);
        Builder<Bean> beanTypeReferenceBuilder = jFactory.type(new TypeReference<Bean>() {
        });
        Builder<?> builderInAnyTypeReference = jFactory.type((TypeReference<?>) new TypeReference<Bean>() {
        });
    }

    public static class Bean {
    }

    public static class BeanSpec extends Spec<Bean> {
    }
}