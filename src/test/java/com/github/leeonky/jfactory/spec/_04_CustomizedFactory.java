package com.github.leeonky.jfactory.spec;

import com.github.leeonky.jfactory.FactorySet;
import com.github.leeonky.jfactory.MixIn;
import com.github.leeonky.jfactory.Spec;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class _04_CustomizedFactory {
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

        @MixIn
        public ABean int100() {
            property("intValue").value(100);
            return this;
        }

        @MixIn("hello")
        public ABean strHello() {
            property("stringValue").value("hello");
            return this;
        }
    }

    @Nested
    class SpecInLambda {

        @Test
        void support_define_specification_for_all_build_of_type() {
            factorySet.factory(Bean.class).spec(instance -> {
                instance.spec().property("stringValue").value("hello");
            });

            assertThat(factorySet.type(Bean.class).create())
                    .hasFieldOrPropertyWithValue("stringValue", "hello");
        }

        @Test
        void support_define_mix_in_of_type() {
            factorySet.factory(Bean.class).spec("100", instance -> {
                instance.spec().property("intValue").value(100);
            });

            assertThat(factorySet.type(Bean.class).mixIn("100").create())
                    .hasFieldOrPropertyWithValue("intValue", 100);
        }

        @Test
        void raise_error_when_mixin_not_exist() {
            assertThrows(IllegalArgumentException.class, () -> factorySet.type(Bean.class).mixIn("not exist").create());
        }
    }

    @Nested
    class SpecInClass {

        @Test
        void support_define_specification_in_class() {
            assertThat(factorySet.spec(ABean.class).mixIn("int100", "hello").create())
                    .hasFieldOrPropertyWithValue("content", "this is a bean")
                    .hasFieldOrPropertyWithValue("stringValue", "hello")
                    .hasFieldOrPropertyWithValue("intValue", 100);
        }

        @Test
        void support_use_mix_in_in_java_code() {
            assertThat(factorySet.create(new ABean().int100().strHello()))
                    .hasFieldOrPropertyWithValue("content", "this is a bean")
                    .hasFieldOrPropertyWithValue("stringValue", "hello")
                    .hasFieldOrPropertyWithValue("intValue", 100);
        }


        @Test
        void should_call_type_base_constructor_and_main_specification() {
            factorySet.factory(Bean.class).constructor(instance -> new BeanSub()).spec(instance -> {
                instance.spec().property("intValue").value(50);
            });

            assertThat(factorySet.create(new ABean()))
                    .isInstanceOf(BeanSub.class)
                    .hasFieldOrPropertyWithValue("intValue", 50);
        }

        @Test
        void support_build_through_spec_name() {
            factorySet.spec(ABean.class);

            assertThat((Bean) factorySet.create("ABean"))
                    .hasFieldOrPropertyWithValue("content", "this is a bean");
        }

        @Test
        void should_raise_error_when_definition_or_mix_in_not_exist() {
            assertThrows(IllegalArgumentException.class, () -> factorySet.create("ABean"));
        }
    }
}
