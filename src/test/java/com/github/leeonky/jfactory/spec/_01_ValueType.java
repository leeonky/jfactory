package com.github.leeonky.jfactory.spec;

import com.github.leeonky.jfactory.FactorySet;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class _01_ValueType {
    private FactorySet factorySet = new FactorySet();

    @Test
    void create_default_string() {
        assertCreate(String.class, "string#1");
        assertCreate(String.class, "string#2");
    }

    private void assertCreate(Class<?> type, Object expect) {
        assertThat(factorySet.create(type)).isEqualTo(expect);
    }
}