package com.github.leeonky.jfactory.spec;

import com.github.leeonky.jfactory.FactorySet;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class _04_Spec {
    private FactorySet factorySet = new FactorySet();

    @Getter
    @Setter
    public static class Bean {
        private String content;
        private String stringValue;
        private int intValue;
        private Bean self;
    }

    @Getter
    @Setter
    public static class Father {
        private Son son;
    }

    @Getter
    @Setter
    public static class Son {
        private Father father;
        private String name;
    }

    @Nested
    class SpecifyValue {

        @Test
        void support_specify_value_in_spec() {
            factorySet.factory(Bean.class).spec(instance ->
                    instance.spec().property("content").value("hello"));

            assertThat(factorySet.create(Bean.class))
                    .hasFieldOrPropertyWithValue("content", "hello")
            ;
        }

        @Test
        void support_specify_value_supplier_in_spc() {
            factorySet.factory(Bean.class).spec(instance ->
                    instance.spec().property("content").value(() -> "hello"));

            assertThat(factorySet.create(Bean.class))
                    .hasFieldOrPropertyWithValue("content", "hello")
            ;
        }

        @Test
        void support_specify_current_object_in_nested_property() {
            factorySet.factory(Bean.class).spec(instance ->
                    instance.spec().property("self").value(instance.reference()));

            Bean bean = factorySet.create(Bean.class);
            assertThat(bean).isEqualTo(bean.getSelf());
        }
    }
}
