package com.github.leeonky.jfactory.spec;

import com.github.leeonky.jfactory.JFactory;
import com.github.leeonky.jfactory.Spec;
import com.github.leeonky.jfactory.Trait;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.github.leeonky.dal.Assertions.expect;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class _03_CreateWithSpec {
    private JFactory jFactory = new JFactory();

    @Getter
    @Setter
    public static class Bean {
        private String content;
        private String stringValue;
        private int intValue;
    }

    public static class Spec2<T> extends Spec<T> {

    }

    @Getter
    @Setter
    public static class BeanSub extends Bean {
    }

    public static class InvalidGenericArgSpec extends Spec2<String> {

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

    public static class TraitPriority extends Spec<Bean> {

        @Override
        public void main() {
            property("content").value("this is a bean");
        }

        @Trait
        public void hello() {
            property("content").value("hello");
        }
    }

    public static class TraitSpec extends Spec<Bean> {

        @Trait
        public void hello() {
            property("content").value("hello");
        }
    }

    @Nested
    class SpecInLambda {

        //        @Test
        void support_define_spec_of_type() {
            jFactory.factory(Bean.class).spec(instance -> {
                instance.spec().property("stringValue").value("hello");
            });

            assertThat(jFactory.type(Bean.class).create())
                    .hasFieldOrPropertyWithValue("stringValue", "hello");
        }

        //        @Test
        void support_define_trait_of_type() {
            jFactory.factory(Bean.class).spec("100", instance -> {
                instance.spec().property("intValue").value(100);
            });

            assertThat(jFactory.type(Bean.class).traits("100").create())
                    .hasFieldOrPropertyWithValue("intValue", 100);
        }

        //        @Test
        void support_method_chain_in_spec_definition() {
            jFactory.factory(Bean.class).spec(instance -> instance.spec()
                            .property("stringValue").value("hello"))
                    .spec("a bean", instance -> instance.spec()
                            .property("content").value("a bean"))
                    .constructor(instance -> new BeanSub());

            assertThat(jFactory.type(Bean.class).traits("a bean").create())
                    .isInstanceOf(BeanSub.class)
                    .hasFieldOrPropertyWithValue("stringValue", "hello")
                    .hasFieldOrPropertyWithValue("content", "a bean")
            ;
        }

        //        @Test
        void raise_error_when_trait_not_exist() {
            assertThrows(IllegalArgumentException.class, () -> jFactory.type(Bean.class).traits("not exist").create());
        }
    }

    @Nested
    class SpecInClass {

        @Test
        void support_define_spec_in_class() {
            assertThat(jFactory.spec(ABean.class).traits("int100", "hello").create())
                    .hasFieldOrPropertyWithValue("content", "this is a bean")
                    .hasFieldOrPropertyWithValue("stringValue", "hello")
                    .hasFieldOrPropertyWithValue("intValue", 100);

            assertThat(jFactory.createAs(ABean.class))
                    .hasFieldOrPropertyWithValue("content", "this is a bean");
        }

        @Test
        void support_pass_spec_arg_in_java_code() {
            assertThat(jFactory.createAs(ABean.class, spec -> spec.int100().strHello()))
                    .hasFieldOrPropertyWithValue("content", "this is a bean")
                    .hasFieldOrPropertyWithValue("stringValue", "hello")
                    .hasFieldOrPropertyWithValue("intValue", 100);
        }


        @Test
        void should_call_type_base_constructor_and_main_spec() {
            jFactory.factory(Bean.class).constructor(instance -> new BeanSub()).spec(instance -> instance.spec()
                    .property("intValue").value(50));

            assertThat(jFactory.createAs(ABean.class))
                    .isInstanceOf(BeanSub.class)
                    .hasFieldOrPropertyWithValue("intValue", 50);
        }

        @Test
        void support_build_through_spec_name() {
            jFactory.spec(ABean.class);

            assertThat((Bean) jFactory.createAs("hello", "ABean"))
                    .hasFieldOrPropertyWithValue("content", "this is a bean")
                    .hasFieldOrPropertyWithValue("stringValue", "hello");
        }

        @Test
        void should_raise_error_when_definition_or_trait_not_exist() {
            assertThrows(IllegalArgumentException.class, () -> jFactory.createAs("ABean"));
        }

        @Test
        void should_raise_error_when_invalid_generic_args() {
            assertThrows(IllegalStateException.class, () -> jFactory.createAs(InvalidGenericArgSpec.class));
        }

        @Test
        void trait_override_spec_in_instance_spec() {
            jFactory.register(TraitPriority.class);
            expect(jFactory.createAs(TraitPriority.class, TraitPriority::hello)).should("content: hello");
        }

        @Test
        void trait_override_spec_in_spec_class() {
            jFactory.register(TraitPriority.class);
            expect(jFactory.createAs("hello", "TraitPriority")).should("content: hello");
        }

        @Test
        void trait_override_spec_in_type_spec() {
            jFactory.factory(Bean.class).spec(instance -> instance.spec().property("content").value("in type"));

            jFactory.register(TraitSpec.class);

            expect(jFactory.createAs("hello", "TraitSpec")).should("content: hello");
        }

//        TODO override type spec, class spec, instance class spec
    }
}
