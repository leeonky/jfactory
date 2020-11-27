package com.github.leeonky.jfactory.spec;

import com.github.leeonky.jfactory.FactorySet;
import com.github.leeonky.jfactory.Spec;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    @AllArgsConstructor
    @NoArgsConstructor
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

    @Getter
    @Setter
    public static class BeanWrapper {
        private Bean bean;
    }

    public static class ABeanWrapper extends Spec<BeanWrapper> {

        @Override
        public void main() {
            property("bean").asDefault();
        }
    }

    public static class ABean extends Spec<Bean> {

        @Override
        public void main() {
            property("stringValue").value((Object) instance().param("p"));
        }
    }

    @Nested
    class Params {

        @Test
        void support_specify_params() {
            factorySet.factory(Bean.class).spec(instance -> instance.spec()
                    .property("stringValue").value((Object) instance.param("p"))
                    .property("intValue").value((Object) instance.param("i")));

            assertThat(factorySet.type(Bean.class).arg("p", "hello").arg("i", 100).create())
                    .hasFieldOrPropertyWithValue("stringValue", "hello")
                    .hasFieldOrPropertyWithValue("intValue", 100);
        }

        @Test
        void support_default_params() {
            factorySet.factory(Bean.class).spec(instance -> instance.spec()
                    .property("stringValue").value(instance.param("p", "default")));

            assertThat(factorySet.type(Bean.class).create())
                    .hasFieldOrPropertyWithValue("stringValue", "default");
        }

        @Test
        void pass_arg_to_nested_spec_type() {
            factorySet.factory(Bean.class).spec(instance -> instance.spec()
                    .property("stringValue").value((Object) instance.param("p")));

            assertThat(factorySet.spec(ABeanWrapper.class).arg("p", "hello").create().getBean())
                    .hasFieldOrPropertyWithValue("stringValue", "hello");
        }

        @Test
        void pass_arg_to_nested_spec_with_spec_class() {
            factorySet.factory(BeanWrapper.class).spec(instance -> instance.spec()
                    .property("bean").spec(ABean.class));

            assertThat(factorySet.type(BeanWrapper.class).arg("p", "hello").create().getBean())
                    .hasFieldOrPropertyWithValue("stringValue", "hello");
        }

        @Test
        void pass_arg_to_nested_spec_with_spec_instance() {
            factorySet.factory(BeanWrapper.class).spec(instance -> instance.spec()
                    .property("bean").specFrom(ABean.class, spec -> {
                    }));

            assertThat(factorySet.type(BeanWrapper.class).arg("p", "hello").create().getBean())
                    .hasFieldOrPropertyWithValue("stringValue", "hello");
        }

        @Test
        void pass_arg_to_nested_spec_with_spec_name() {
            factorySet.register(ABean.class);

            factorySet.factory(BeanWrapper.class).spec(instance -> instance.spec()
                    .property("bean").spec("ABean"));

            assertThat(factorySet.type(BeanWrapper.class).arg("p", "hello").create().getBean())
                    .hasFieldOrPropertyWithValue("stringValue", "hello");
        }

        @Test
        void pass_arg_to_nested_spec_with_spec_class_and_properties() {
            factorySet.register(ABean.class);

            factorySet.factory(BeanWrapper.class).spec(instance -> instance.spec()
                    .property("bean").spec(ABean.class, builder -> builder.property("intValue", 1)));

            assertThat(factorySet.type(BeanWrapper.class).arg("p", "hello").create().getBean())
                    .hasFieldOrPropertyWithValue("stringValue", "hello");
        }
    }
}
