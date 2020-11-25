package com.github.leeonky.jfactory.spec;

import com.github.leeonky.jfactory.FactorySet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class _01_BeanType {
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
        assertThat(factorySet.type(Bean.class)
                .property("stringValue", "hello")
                .property("intValue", 100).create())
                .hasFieldOrPropertyWithValue("stringValue", "hello")
                .hasFieldOrPropertyWithValue("intValue", 100);
    }

    @Test
    void should_raise_error_when_property_expression_invalid() {
        assertThrows(IllegalArgumentException.class, () -> factorySet.type(Bean.class)
                .property(".a", 100).create());
    }

    @Test
    void support_customized_constructor() {
        factorySet.factory(BeanWithNoDefaultConstructor.class).constructor(arg -> new BeanWithNoDefaultConstructor("hello", 100));

        assertThat(factorySet.type(BeanWithNoDefaultConstructor.class).create())
                .hasFieldOrPropertyWithValue("stringValue", "hello")
                .hasFieldOrPropertyWithValue("intValue", 100);
    }

    @Getter
    @Setter
    public static class Bean {
        private String stringValue;
        private int intValue;
    }

    @Getter
    @AllArgsConstructor
    public static class BeanWithNoDefaultConstructor {
        private final String stringValue;
        private int intValue;
    }

    @Nested
    class Params {

        @Test
        void support_specify_params() {
            factorySet.factory(BeanWithNoDefaultConstructor.class).constructor(instance ->
                    new BeanWithNoDefaultConstructor(instance.param("p"), instance.param("i")));

            assertThat(factorySet.type(BeanWithNoDefaultConstructor.class).arg("p", "hello").arg("i", 100).create())
                    .hasFieldOrPropertyWithValue("stringValue", "hello")
                    .hasFieldOrPropertyWithValue("intValue", 100);

            factorySet.factory(BeanWithNoDefaultConstructor.class).constructor(instance ->
                    new BeanWithNoDefaultConstructor(instance.param("p", "default"), instance.getSequence()));

            assertThat(factorySet.type(BeanWithNoDefaultConstructor.class).create())
                    .hasFieldOrPropertyWithValue("stringValue", "default");
        }
    }
}
