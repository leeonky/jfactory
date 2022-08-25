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
    class SpecInClass {

        //        @Test
        void should_call_type_base_constructor_and_main_spec() {
            jFactory.factory(Bean.class).constructor(instance -> new BeanSub()).spec(instance -> instance.spec()
                    .property("intValue").value(50));

            assertThat(jFactory.createAs(ABean.class))
                    .isInstanceOf(BeanSub.class)
                    .hasFieldOrPropertyWithValue("intValue", 50);
        }

        //        @Test
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
