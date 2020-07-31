package com.github.leeonky.jfactory.spec;

import com.github.leeonky.jfactory.FactorySet;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class _02_BeanType {
    private FactorySet factorySet = new FactorySet();

    @Test
    void default_creation() {
        assertThat(factorySet.create(Bean.class))
                .hasFieldOrPropertyWithValue("stringValue", "stringValue#1")
        ;

        assertThat(factorySet.create(Bean.class))
                .hasFieldOrPropertyWithValue("stringValue", "stringValue#2")
        ;
    }

    @Getter
    @Setter
    public static class Bean {
        private String stringValue;
    }
}
