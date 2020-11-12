package com.github.leeonky.jfactory.spec;

import com.github.leeonky.jfactory.FactorySet;
import com.github.leeonky.jfactory.Spec;
import com.github.leeonky.jfactory.Trait;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class _03_CreateWithSpec {
    private FactorySet factorySet = new FactorySet();

    @Getter
    @Setter
    public static class Bean {
        private String content;
        private String stringValue;
        private int intValue;
    }

    @Getter
    @Setter
    public static class BeanSub extends Bean {
    }

    public static class ABean extends Spec<Bean> {

        @Override
        public void main() {
            property("content").value("this is a bean");
        }

        @Trait
        public ABean int100() {
            property("intValue").value(100);
            return this;
        }

        @Trait("hello")
        public ABean strHello() {
            property("stringValue").value("hello");
            return this;
        }
    }

    class InvalidGenericArgSpec<T> extends Spec<T> {
    }

    @Nested
    class SpecInLambda {

        @Test
        void support_define_spec_of_type() {
            factorySet.factory(Bean.class).spec(instance -> {
                instance.spec().property("stringValue").value("hello");
            });

            assertThat(factorySet.type(Bean.class).create())
                    .hasFieldOrPropertyWithValue("stringValue", "hello");
        }

        @Test
        void support_define_trait_of_type() {
            factorySet.factory(Bean.class).spec("100", instance -> {
                instance.spec().property("intValue").value(100);
            });

            assertThat(factorySet.type(Bean.class).trait("100").create())
                    .hasFieldOrPropertyWithValue("intValue", 100);
        }

        @Test
        void support_method_chain_in_spec_definition() {
            factorySet.factory(Bean.class).spec(instance -> instance.spec()
                    .property("stringValue").value("hello"))
                    .spec("a bean", instance -> instance.spec()
                            .property("content").value("a bean"))
                    .constructor(instance -> new BeanSub());

            assertThat(factorySet.type(Bean.class).trait("a bean").create())
                    .isInstanceOf(BeanSub.class)
                    .hasFieldOrPropertyWithValue("stringValue", "hello")
                    .hasFieldOrPropertyWithValue("content", "a bean")
            ;
        }

        @Test
        void raise_error_when_trait_not_exist() {
            assertThrows(IllegalArgumentException.class, () -> factorySet.type(Bean.class).trait("not exist").create());
        }
    }

    @Nested
    class SpecInClass {

        @Test
        void support_define_spec_in_class() {
            assertThat(factorySet.spec(ABean.class).trait("int100", "hello").create())
                    .hasFieldOrPropertyWithValue("content", "this is a bean")
                    .hasFieldOrPropertyWithValue("stringValue", "hello")
                    .hasFieldOrPropertyWithValue("intValue", 100);
        }

        @Test
        void support_pass_spec_arg_in_java_code() {
            assertThat(factorySet.create(new ABean().int100().strHello()))
                    .hasFieldOrPropertyWithValue("content", "this is a bean")
                    .hasFieldOrPropertyWithValue("stringValue", "hello")
                    .hasFieldOrPropertyWithValue("intValue", 100);
        }


        @Test
        void should_call_type_base_constructor_and_main_spec() {
            factorySet.factory(Bean.class).constructor(instance -> new BeanSub()).spec(instance -> instance.spec()
                    .property("intValue").value(50));

            assertThat(factorySet.create(new ABean()))
                    .isInstanceOf(BeanSub.class)
                    .hasFieldOrPropertyWithValue("intValue", 50);
        }

        @Test
        void support_build_through_spec_name() {
            factorySet.spec(ABean.class);

            assertThat((Bean) factorySet.create("hello", "ABean"))
                    .hasFieldOrPropertyWithValue("content", "this is a bean")
                    .hasFieldOrPropertyWithValue("stringValue", "hello");
        }

        @Test
        void should_raise_error_when_definition_or_trait_not_exist() {
            assertThrows(IllegalArgumentException.class, () -> factorySet.create("ABean"));
        }

        @Test
        void should_raise_error_when_invalid_generic_args() {
            assertThrows(IllegalStateException.class, () -> factorySet.create(new InvalidGenericArgSpec<>()));
        }
    }
}
