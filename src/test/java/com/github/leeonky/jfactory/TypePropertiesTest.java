package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TypePropertiesTest {

    @Test
    void should_same_hashcode_with_same_type_and_properties() {
        assertThat(typeProperties(String.class).hashCode())
                .isEqualTo(typeProperties(String.class).hashCode());

        assertThat(typeProperties(String.class).hashCode())
                .isNotEqualTo(typeProperties(Integer.class).hashCode());
    }

    @Test
    void should_same_with_same_type_and_properties() {
        assertThat(typeProperties(String.class))
                .isEqualTo(typeProperties(String.class));

        assertThat(typeProperties(String.class))
                .isNotEqualTo(typeProperties(Integer.class));
    }

    private <T> TypeProperties<T> typeProperties(Class<T> type) {
        return new TypeProperties<>(BeanClass.create(type));
    }
}