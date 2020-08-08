package com.github.leeonky.jfactory.spec;

import com.github.leeonky.jfactory.FactorySet;
import com.github.leeonky.jfactory.MixIn;
import com.github.leeonky.jfactory.Spec;
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
    public static class Beans {
        private Bean bean;
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

    @Nested
    class SpecifySpec {

        @Test
        void support_specify_spec_class() {
            factorySet.factory(Beans.class).spec(instance ->
                    instance.spec().property("bean").spec(ABean.class));

            assertThat(factorySet.create(Beans.class).getBean())
                    .hasFieldOrPropertyWithValue("content", "this is a bean")
            ;
        }

        @Test
        void support_specify_spec_instance() {
            factorySet.factory(Beans.class).spec(instance ->
                    instance.spec().property("bean").spec(new ABean().int100()));

            assertThat(factorySet.create(Beans.class).getBean())
                    .hasFieldOrPropertyWithValue("content", "this is a bean")
            ;
        }

        @Test
        void support_specify_spec_name() {
            factorySet.registerSpec(ABean.class);

            factorySet.factory(Beans.class).spec(instance ->
                    instance.spec().property("bean").spec("int100", "ABean"));

            assertThat(factorySet.create(Beans.class).getBean())
                    .hasFieldOrPropertyWithValue("intValue", 100)
            ;
        }

        @Test
        void support_specify_customized_builder_args() {
            factorySet.factory(Beans.class).spec(instance ->
                    instance.spec().property("bean").spec(ABean.class, builder -> builder.mixIn("int100")));

            assertThat(factorySet.create(Beans.class).getBean())
                    .hasFieldOrPropertyWithValue("content", "this is a bean")
                    .hasFieldOrPropertyWithValue("intValue", 100);
        }
    }
}