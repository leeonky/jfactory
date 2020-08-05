package com.github.leeonky.jfactory.spec;

import com.github.leeonky.jfactory.FactorySet;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class _01_ValueType {
    private FactorySet factorySet = new FactorySet();

    @Test
    void create_string_values_sequentially() {
        assertCreate(String.class, "string#1");
        assertCreate(String.class, "string#2");
    }

    @Test
    void create_int_values_sequentially() {
        assertCreate(int.class, 1);
        assertCreate(Integer.class, 2);
        System.err.println(
                Base64.getEncoder().encodeToString(("xtloyi:20200719xt@loyi").getBytes()));
    }

    private void assertCreate(Class<?> type, Object expect) {
        assertThat(factorySet.create(type)).isEqualTo(expect);
    }
}
