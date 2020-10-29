package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultValueBuildersTest {

    @Test
    void create_string() {
        assertValue(String.class, 1, "str", "str#1");
        assertValue(String.class, 3, "name", "name#3");
    }

    @Test
    void create_int() {
        assertValue(int.class, 1, 1);
        assertValue(Integer.class, 3, 3);
    }

    @Test
    void should_raise_error_when_invalid_generic_args() {
        assertThrows(IllegalStateException.class, () -> new InvalidGenericArgDefaultValueBuilder<>().getType());
    }

    private void assertValue(Class<?> type, int sequence, String property, Object expected) {
        assertThat(new DefaultValueBuilders().queryDefaultValueFactory(type).get()
                .create(null, new Instance<>(sequence, null).sub(property))).isEqualTo(expected);
    }

    private void assertValue(Class<?> type, int sequence, Object expected) {
        assertThat(new DefaultValueBuilders().queryDefaultValueFactory(type).get()
                .create(null, new Instance<>(sequence, null))).isEqualTo(expected);
    }

    @Test
    void default_value_builder_create_default_value() {
        assertThat(new DefaultValueBuilders.DefaultTypeBuilder<>(BeanClass.create(int.class)).create(null, null))
                .isInstanceOf(Integer.class);
    }

    public static class InvalidGenericArgDefaultValueBuilder<V> implements DefaultValueBuilder<V> {
        @Override
        public <T> V create(BeanClass<T> type, Instance<T> instance) {
            return null;
        }
    }
}