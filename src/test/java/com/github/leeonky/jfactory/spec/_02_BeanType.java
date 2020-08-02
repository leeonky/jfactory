package com.github.leeonky.jfactory.spec;

import com.github.leeonky.jfactory.FactorySet;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

class _02_BeanType {
    private FactorySet factorySet = new FactorySet();

    @Test
    void default_creation_with_default_value_producing() {
        assertThat(factorySet.create(Bean.class))
                .hasFieldOrPropertyWithValue("stringValue", "stringValue#1")
        ;

        assertThat(factorySet.create(Bean.class))
                .hasFieldOrPropertyWithValue("stringValue", "stringValue#2")
        ;
    }

    @Test
    void support_specify_properties_in_building() {
        assertThat(factorySet.type(Bean.class).property("stringValue", "hello").create())
                .hasFieldOrPropertyWithValue("stringValue", "hello");

        assertThat(factorySet.type(Bean.class).properties(new HashMap<String, Object>() {{
            put("stringValue", "hello");
            put("intValue", 100);
        }}).create())
                .hasFieldOrPropertyWithValue("stringValue", "hello")
                .hasFieldOrPropertyWithValue("intValue", 100);
    }

    @Getter
    @Setter
    public static class Bean {
        private String stringValue;
        private int intValue;
    }
}
