package com.github.leeonky.jfactory.spec;

import com.github.leeonky.jfactory.FactorySet;
import com.github.leeonky.jfactory.MixIn;
import com.github.leeonky.jfactory.Spec;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Getter
    @Setter
    public static class Table {
        private List<Row> rows;
    }

    @Getter
    @Setter
    public static class Row {
        private Table table;
        private int value;
    }

    @Nested
    class SpecifyValue {

        @Test
        void support_specify_value_in_spec() {
            factorySet.factory(Bean.class).spec(instance -> instance.spec()
                    .property("content").value("hello"));

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
            factorySet.register(ABean.class);

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

    @Nested
    class DefaultTypeBuild {

        @Test
        void support_create_property_with_default() {
            factorySet.factory(Beans.class).spec(instance ->
                    instance.spec().property("bean").asDefault());

            assertThat(factorySet.create(Beans.class).getBean())
                    .isInstanceOf(Bean.class)
            ;
        }

        @Test
        void support_specify_customized_builder_args() {
            factorySet.factory(Beans.class).spec(instance ->
                    instance.spec().property("bean").asDefault(builder -> builder.property("intValue", 100)));

            assertThat(factorySet.create(Beans.class).getBean())
                    .hasFieldOrPropertyWithValue("intValue", 100);
        }
    }

    @Nested
    class CollectionProperty {

        @Test
        void support_define_collection_element_spec() {
            factorySet.factory(Table.class).spec(instance ->
                    instance.spec().property("rows[0]").asDefault(builder -> builder.property("value", 100)));

            Table table = factorySet.create(Table.class);

            assertThat(table.getRows())
                    .hasSize(1);

            assertThat(table.getRows().get(0))
                    .hasFieldOrPropertyWithValue("value", 100);
        }
    }

    @Nested
    class NotSupportPropertyChain {

        @Test
        void should_raise_error_when_use_property_chain_in_spec_definition() {
            factorySet.factory(Beans.class).spec(instance ->
                    instance.spec().property("bean.intValue").value(0));

            assertThat(assertThrows(IllegalArgumentException.class, () -> factorySet.create(Beans.class)))
                    .hasMessageContaining("Not support property chain 'bean.intValue' in current operation");
        }
    }
}
