package com.github.leeonky.jfactory;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PropertyValueBuildersTest {

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

    private void assertValue(Class<?> type, int sequence, String property, Object expected) {
        assertThat(new PropertyValueBuilders().queryPropertyValueFactory(type).get()
                .create(null, new Instance<>(sequence, null).sub(property))).isEqualTo(expected);
    }

    private void assertValue(Class<?> type, int sequence, Object expected) {
        assertThat(new PropertyValueBuilders().queryPropertyValueFactory(type).get()
                .create(null, new Instance<>(sequence, null))).isEqualTo(expected);
    }
}