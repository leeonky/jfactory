package com.github.leeonky.jfactory.spec;

import com.github.leeonky.jfactory.JFactory;
import com.github.leeonky.jfactory.MemoryDataRepository;
import com.github.leeonky.jfactory.Spec;
import com.github.leeonky.jfactory.Trait;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class _04_Spec {
    private JFactory JFactory = new JFactory();

    @Getter
    @Setter
    public static class Bean {
        private String content;
        private String stringValue;
        private String[] stringValues;
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

        @Trait
        public ABean int100() {
            property("intValue").value(100);
            return this;
        }
    }

    @Getter
    @Setter
    public static class Table {
        private List<Row> rows;
        private String name;
    }

    @Getter
    @Setter
    public static class Row {
        private Table table;
        private List<Cell> cells;
        private int number;
    }

    @Getter
    @Setter
    public static class Cell {
        private Row row;
        private int value;
    }


    @Getter
    @Setter
    public static class Person {
        private ID id;
    }

    @Getter
    @Setter
    public static class ID {
        private Person person;
        private String number;
    }

    @Nested
    class SpecifyValue {

        @Test
        void support_specify_value_in_spec() {
            JFactory.factory(Bean.class).spec(instance -> instance.spec()
                    .property("content").value("hello"));

            assertThat(JFactory.create(Bean.class))
                    .hasFieldOrPropertyWithValue("content", "hello")
            ;
        }

        @Test
        void support_specify_value_supplier_in_spc() {
            JFactory.factory(Bean.class).spec(instance ->
                    instance.spec().property("content").value(() -> "hello"));

            assertThat(JFactory.create(Bean.class))
                    .hasFieldOrPropertyWithValue("content", "hello")
            ;
        }

        @Test
        void support_specify_current_object_in_nested_property() {
            JFactory.factory(Bean.class).spec(instance ->
                    instance.spec().property("self").value(instance.reference()));

            Bean bean = JFactory.create(Bean.class);
            assertThat(bean).isEqualTo(bean.getSelf());
        }

        @Test
        void should_process_null_as_null_value() {
            JFactory.factory(Bean.class).spec(instance ->
                    instance.spec().property("content").value(null));

            assertThat(JFactory.create(Bean.class))
                    .hasFieldOrPropertyWithValue("content", null)
            ;
        }
    }

    @Nested
    class SpecifyDefaultValue {

        @Test
        void support_specify_property_default_value() {
            JFactory.factory(Bean.class).spec(instance ->
                    instance.spec().property("stringValue").asDefaultValue(instance.getSequence()));

            assertThat(JFactory.create(Bean.class))
                    .hasFieldOrPropertyWithValue("stringValue", "1")
            ;
        }

        @Test
        void support_specify_property_default_value_supplier() {
            JFactory.factory(Bean.class).spec(instance ->
                    instance.spec().property("stringValue").asDefaultValue(instance.spec().instance()::getSequence));

            assertThat(JFactory.create(Bean.class))
                    .hasFieldOrPropertyWithValue("stringValue", "1")
            ;
        }

        @Test
        void support_specify_property_default_value_with_null() {
            JFactory.factory(Bean.class).spec(instance ->
                    instance.spec().property("stringValue").asDefaultValue(null));

            assertThat(JFactory.create(Bean.class))
                    .hasFieldOrPropertyWithValue("stringValue", null)
            ;
        }

        @Test
        void default_value_should_only_override_default_value_spec() {
            JFactory.factory(Bean.class).spec(instance -> instance.spec()
                    .property("stringValue").value("hello")
                    .property("stringValue").asDefaultValue(instance.getSequence())
            );

            assertThat(JFactory.create(Bean.class))
                    .hasFieldOrPropertyWithValue("stringValue", "hello")
            ;
        }
    }

    @Nested
    class SpecifySpec {

        @Test
        void support_specify_spec_class() {
            JFactory.factory(Beans.class).spec(instance ->
                    instance.spec().property("bean").as(ABean.class));

            assertThat(JFactory.create(Beans.class).getBean())
                    .hasFieldOrPropertyWithValue("content", "this is a bean")
            ;
        }

        @Test
        void support_specify_spec_instance() {
            JFactory.factory(Beans.class).spec(instance ->
                    instance.spec().property("bean").as(ABean.class, ABean::int100));

            assertThat(JFactory.create(Beans.class).getBean())
                    .hasFieldOrPropertyWithValue("content", "this is a bean")
            ;
        }

        @Test
        void support_specify_spec_name() {
            JFactory.register(ABean.class);

            JFactory.factory(Beans.class).spec(instance ->
                    instance.spec().property("bean").as("int100", "ABean"));

            assertThat(JFactory.create(Beans.class).getBean())
                    .hasFieldOrPropertyWithValue("intValue", 100)
            ;
        }

        @Test
        void support_specify_customized_builder_args() {
            JFactory.factory(Beans.class).spec(instance ->
                    instance.spec().property("bean").asWith(ABean.class, builder -> builder.traits("int100")));

            assertThat(JFactory.create(Beans.class).getBean())
                    .hasFieldOrPropertyWithValue("content", "this is a bean")
                    .hasFieldOrPropertyWithValue("intValue", 100);
        }
    }

    @Nested
    class DefaultTypeBuild {

        @Test
        void support_create_property_with_default() {
            JFactory.factory(Beans.class).spec(instance ->
                    instance.spec().property("bean").asDefault());

            assertThat(JFactory.create(Beans.class).getBean())
                    .isInstanceOf(Bean.class)
            ;
        }

        @Test
        void support_specify_customized_builder_args() {
            JFactory.factory(Beans.class).spec(instance ->
                    instance.spec().property("bean").asDefault(builder -> builder.property("intValue", 100)));

            assertThat(JFactory.create(Beans.class).getBean())
                    .hasFieldOrPropertyWithValue("intValue", 100);
        }

        @Test
        void support_default_primitive_type_spec() {
            JFactory.factory(Bean.class).spec(instance ->
                    instance.spec().property("stringValue").asDefault());

            assertThat(JFactory.create(Bean.class).stringValue).isEqualTo("stringValue#1");
        }
    }

    @Nested
    class CollectionProperty {

        @Test
        void support_define_collection_element_spec() {
            JFactory.factory(Table.class).spec(instance ->
                    instance.spec().property("rows[0]").asDefault(builder -> builder.property("number", 100)));

            Table table = JFactory.create(Table.class);

            assertThat(table.getRows())
                    .hasSize(1);

            assertThat(table.getRows().get(0))
                    .hasFieldOrPropertyWithValue("number", 100);
        }

        @Test
        void should_raise_error_when_collection_property_is_null() {
            JFactory.factory(Table.class).spec(instance -> instance.spec()
                    .property("rows").asDefault());

            assertThrows(IllegalArgumentException.class, () ->
                    JFactory.type(Table.class).property("rows[0]", null).create());
        }

        @Test
        void support_default_primitive_type_spec() {
            JFactory.factory(Bean.class).spec(instance ->
                    instance.spec().property("stringValues[0]").asDefault());

            assertThat(JFactory.create(Bean.class).stringValues[0]).isEqualTo("stringValues#1[0]");
        }
    }

    @Nested
    class NotSupportPropertyChain {

        @Test
        void should_raise_error_when_use_property_chain_in_spec_definition() {
            JFactory.factory(Beans.class).spec(instance ->
                    instance.spec().property("bean.intValue").value(0));

            assertThat(assertThrows(IllegalArgumentException.class, () -> JFactory.create(Beans.class)))
                    .hasMessageContaining("Not support property chain 'bean.intValue' in current operation");
        }
    }

    @Nested
    class SupportReverseAssociation {

        @Test
        void should_support_define_collection_element_reverse_association_in_parent_spec() {
            JFactory.factory(Table.class).spec(instance -> instance.spec()
                    .property("rows").reverseAssociation("table")
            );

            Table table = JFactory.type(Table.class)
                    .property("name", "a table")
                    .property("rows[0].number", 1)
                    .create();

            assertThat(table)
                    .hasFieldOrPropertyWithValue("name", "a table");
            assertThat(table.getRows())
                    .hasSize(1);
            assertThat(table.getRows().get(0))
                    .hasFieldOrPropertyWithValue("table", table)
                    .hasFieldOrPropertyWithValue("number", 1);
        }

        @Test
        void should_save_parent_bean_first() {
            List<Object> cached = new ArrayList<>();
            JFactory = new JFactory(new MemoryDataRepository() {
                @Override
                public void save(Object object) {
                    super.save(object);
                    cached.add(object);
                }
            });

            JFactory.factory(Table.class).spec(instance -> instance.spec()
                    .property("rows").reverseAssociation("table")
                    .property("rows[0]").asDefault()
            );

            JFactory.factory(Row.class).spec(instance -> instance.spec()
                    .property("cells").reverseAssociation("row")
                    .property("cells[0]").asDefault()
            );

            Table table = JFactory.create(Table.class);

            assertThat(cached).containsExactly(table, table.getRows().get(0), table.getRows().get(0).getCells().get(0));
        }

        @Test
        void should_support_define_single_reverse_association_in_parent_spec() {
            JFactory.factory(Person.class).spec(instance -> instance.spec()
                    .property("id").reverseAssociation("person")
            );

            Person person = JFactory.type(Person.class)
                    .property("id.number", "007")
                    .create();

            assertThat(person.getId())
                    .hasFieldOrPropertyWithValue("number", "007")
                    .hasFieldOrPropertyWithValue("person", person);
        }
    }
}
