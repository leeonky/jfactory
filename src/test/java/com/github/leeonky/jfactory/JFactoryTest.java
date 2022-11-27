package com.github.leeonky.jfactory;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Test
    void global_spec_should_not_has_super_spec() {
        JFactory jFactory = new JFactory();
        assertThatThrownBy(() -> jFactory.register(InvalidGlobalSpec.class)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Global Spec com.github.leeonky.jfactory.JFactoryTest$InvalidGlobalSpec should not have super Spec com.github.leeonky.jfactory.JFactoryTest$BeanSpec.");
    }

    public static class Bean {
    }

    public static class BeanSpec extends Spec<Bean> {
    }

    @Global
    public static class InvalidGlobalSpec extends BeanSpec {
    }
}