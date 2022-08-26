package com.github.leeonky.jfactory.spec;

import com.github.leeonky.jfactory.*;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.github.leeonky.dal.Assertions.expect;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

class _04_Spec {
    private JFactory jFactory = new JFactory();

    @Getter
    @Setter
    public static class Bean {
        private String content;
        private String stringValue;
        private String[] stringValues;
        private int intValue;
        private Bean self;
    }

    public static class IgnoreProperty {
        private boolean setterCalled = false;
        private String value;

        public boolean isSetterCalled() {
            return setterCalled;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            setterCalled = true;
            this.value = value;
        }
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

    @Global
    public static class BeanGlobal1 extends Spec<Bean> {

        @Override
        public void main() {
            property("stringValue").value("base 1");
        }
    }

    @Global
    public static class BeanGlobal2 extends Spec<Bean> {

        @Override
        public void main() {
            property("intValue").value(1000);
        }
    }

    @Nested
    class SpecifyValue {

        //        @Test
        void support_specify_value_in_spec() {
            jFactory.factory(Bean.class).spec(instance -> instance.spec()
                    .property("content").value("hello"));

            assertThat(jFactory.create(Bean.class))
                    .hasFieldOrPropertyWithValue("content", "hello")
            ;
        }

        //        @Test
        void support_specify_value_supplier_in_spc() {
            jFactory.factory(Bean.class).spec(instance ->
                    instance.spec().property("content").value(() -> "hello"));

            assertThat(jFactory.create(Bean.class))
                    .hasFieldOrPropertyWithValue("content", "hello")
            ;
        }

        //        @Test
        void support_specify_current_object_in_nested_property() {
            jFactory.factory(Bean.class).spec(instance ->
                    instance.spec().property("self").value(instance.reference()));

            Bean bean = jFactory.create(Bean.class);
            assertThat(bean).isEqualTo(bean.getSelf());
        }

        //        @Test
        void should_process_null_as_null_value() {
            jFactory.factory(Bean.class).spec(instance ->
                    instance.spec().property("content").value(null));

            assertThat(jFactory.create(Bean.class))
                    .hasFieldOrPropertyWithValue("content", null)
            ;
        }
    }

    @Nested
    class SpecifyDefaultValue {

        @Test
        void support_specify_property_default_value() {
            jFactory.factory(Bean.class).spec(instance ->
                    instance.spec().property("stringValue").defaultValue(instance.getSequence()));

            assertThat(jFactory.create(Bean.class))
                    .hasFieldOrPropertyWithValue("stringValue", "1")
            ;
        }

        @Test
        void support_specify_property_default_value_supplier() {
            jFactory.factory(Bean.class).spec(instance ->
                    instance.spec().property("stringValue").defaultValue(instance::getSequence));

            assertThat(jFactory.create(Bean.class))
                    .hasFieldOrPropertyWithValue("stringValue", "1")
            ;
        }

        @Test
        void support_specify_property_default_value_with_null() {
            jFactory.factory(Bean.class).spec(instance ->
                    instance.spec().property("stringValue").defaultValue(null));

            assertThat(jFactory.create(Bean.class))
                    .hasFieldOrPropertyWithValue("stringValue", null)
            ;
        }

        @Test
        void default_value_should_only_override_default_value_spec() {
            jFactory.factory(Bean.class).spec(instance -> instance.spec()
                    .property("stringValue").value("hello")
                    .property("stringValue").defaultValue(instance.getSequence())
            );

            assertThat(jFactory.create(Bean.class))
                    .hasFieldOrPropertyWithValue("stringValue", "hello")
            ;
        }
    }

    @Nested
    class SpecifySpec {

        @Test
        void support_specify_spec_class_method_is_will_always_create_object() {
            Bean aBean = jFactory.createAs(ABean.class);

            jFactory.factory(Beans.class).spec(instance ->
                    instance.spec().property("bean").is(ABean.class));

            assertThat(jFactory.create(Beans.class).getBean())
                    .hasFieldOrPropertyWithValue("content", "this is a bean")
                    .isNotSameAs(aBean)
            ;
        }

        @Test
        void support_specify_spec_instance() {
            jFactory.factory(Beans.class).spec(instance -> instance.spec().property("bean").from(ABean.class).which(ABean::int100));

            assertThat(jFactory.create(Beans.class).getBean())
                    .hasFieldOrPropertyWithValue("content", "this is a bean")
            ;
        }

        @Test
        void should_use_any_exist_object_in_specify_spec_instance_when_which_clause_is_empty() {
            Bean bean = jFactory.create(Bean.class);

            jFactory.factory(Beans.class).spec(instance -> instance.spec().property("bean").from(ABean.class).which(x -> {
            }));

            assertThat(jFactory.create(Beans.class).getBean()).isSameAs(bean);
        }

        @Test
        void support_specify_spec_name_method_is_will_always_create_object() {
            jFactory.createAs(ABean.class);

            jFactory.register(ABean.class);

            jFactory.factory(Beans.class).spec(instance ->
                    instance.spec().property("bean").is("int100", "ABean"));

            assertThat(jFactory.create(Beans.class).getBean())
                    .hasFieldOrPropertyWithValue("intValue", 100)
            ;
        }

        @Test
        void support_specify_customized_builder_args() {
            jFactory.factory(Beans.class).spec(instance -> instance.spec().property("bean").from((Class<? extends Spec<Bean>>) ABean.class).and(builder -> builder.traits("int100")));

            assertThat(jFactory.create(Beans.class).getBean())
                    .hasFieldOrPropertyWithValue("content", "this is a bean")
                    .hasFieldOrPropertyWithValue("intValue", 100);
        }

        @Test
        void should_use_exist_object_when_use_empty_builder_in_and_clause() {
            Bean bean = jFactory.create(Bean.class);

            jFactory.factory(Beans.class).spec(instance -> instance.spec()
                    .property("bean").from((Class<? extends Spec<Bean>>) ABean.class).and(builder -> builder));

            assertThat(jFactory.create(Beans.class).getBean())
                    .isSameAs(bean);
        }

        @Test
        void should_raise_error_when_invalid_call() {
            int currentLine = Thread.currentThread().getStackTrace()[1].getLineNumber();
            jFactory.factory(Beans.class).spec(instance -> instance.spec()
                    .property("bean").from(ABean.class));

            assertThat(assertThrows(InvalidSpecException.class, () -> jFactory.create(Beans.class)))
                    .hasMessageContaining("Invalid property spec:")
                    .hasMessageContaining("_04_Spec.java:" + (currentLine + 2))
                    .hasMessageContaining("Should finish method chain with `and` or `which`:")
                    .hasMessageContaining("property().from().which()")
                    .hasMessageContaining("property().from().and()")
                    .hasMessageContaining("Or use property().is() to create object with only spec directly.");
        }
    }

    @Nested
    class DefaultTypeBuild {

        @Test
        void support_create_property_with_default_no_args_by_factory_will_always_create_object() {
            Bean bean = jFactory.create(Bean.class);

            jFactory.factory(Beans.class).spec(instance ->
                    instance.spec().property("bean").byFactory());

            assertThat(jFactory.create(Beans.class).getBean())
                    .isInstanceOf(Bean.class)
                    .isNotSameAs(bean)
            ;
        }

        @Test
        void support_specify_customized_builder_args() {
            jFactory.factory(Beans.class).spec(instance ->
                    instance.spec().property("bean").byFactory(builder -> builder.property("intValue", 100)));

            assertThat(jFactory.create(Beans.class).getBean())
                    .hasFieldOrPropertyWithValue("intValue", 100);
        }

        @Test
        void should_use_created_object_in_customized_builder() {
            Bean bean = jFactory.type(Bean.class).property("intValue", 100).create();

            jFactory.factory(Beans.class).spec(instance ->
                    instance.spec().property("bean").byFactory(builder -> builder.property("intValue", 100)));

            assertThat(jFactory.create(Beans.class).getBean())
                    .isEqualTo(bean);
        }

        @Test
        void support_default_primitive_type_spec() {
            jFactory.factory(Bean.class).spec(instance ->
                    instance.spec().property("stringValue").byFactory());

            assertThat(jFactory.create(Bean.class).stringValue).isEqualTo("stringValue#1");
        }

        @Test
        void should_use_any_exist_object_when_no_properties_in_by_factory_builder() {
            Bean bean = jFactory.create(Bean.class);

            jFactory.factory(Beans.class).spec(instance ->
                    instance.spec().property("bean").byFactory(builder -> builder));

            assertThat(jFactory.create(Beans.class).getBean())
                    .isSameAs(bean);
        }
    }

    @Nested
    class CollectionProperty {

        @Test
        void support_define_collection_element_spec() {
            jFactory.factory(Table.class).spec(instance ->
                    instance.spec().property("rows[0]").byFactory(builder -> builder.property("number", 100)));

            Table table = jFactory.create(Table.class);

            assertThat(table.getRows())
                    .hasSize(1);

            assertThat(table.getRows().get(0))
                    .hasFieldOrPropertyWithValue("number", 100);
        }

        @Test
        void should_raise_error_when_collection_property_is_null() {
            jFactory.factory(Table.class).spec(instance -> instance.spec()
                    .property("rows").byFactory());

            assertThrows(IllegalArgumentException.class, () ->
                    jFactory.type(Table.class).property("rows[0]", null).create());
        }

        @Test
        void support_default_primitive_type_spec() {
            jFactory.factory(Bean.class).spec(instance ->
                    instance.spec().property("stringValues[0]").byFactory());

            assertThat(jFactory.create(Bean.class).stringValues[0]).isEqualTo("stringValues#1[0]");
        }

        @Test
        void support_empty_list_in_property() {
            assertThat(jFactory.type(Bean.class).property("stringValues", emptyList()).create().getStringValues())
                    .isEmpty();
        }

        @Test
        void support_use_list_in_property() {
            assertThat(jFactory.type(Bean.class).property("stringValues", asList("a", "b")).create().getStringValues())
                    .containsExactly("a", "b");
        }
    }

    @Nested
    class NotSupportPropertyChain {

        @Test
        void should_raise_error_when_use_property_chain_in_spec_definition() {
            jFactory.factory(Beans.class).spec(instance ->
                    instance.spec().property("bean.intValue").value(0));

            assertThat(assertThrows(IllegalArgumentException.class, () -> jFactory.create(Beans.class)))
                    .hasMessageContaining("Not support property chain 'bean.intValue' in current operation");
        }
    }

    @Nested
    class SupportReverseAssociation {

        @Test
        void should_support_define_collection_element_reverse_association_in_parent_spec() {
            jFactory.factory(Table.class).spec(instance -> instance.spec()
                    .property("rows").reverseAssociation("table")
            );

            Table table = jFactory.type(Table.class)
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
            jFactory = new JFactory(new MemoryDataRepository() {
                @Override
                public void save(Object object) {
                    super.save(object);
                    cached.add(object);
                }
            });

            jFactory.factory(Table.class).spec(instance -> instance.spec()
                    .property("rows").reverseAssociation("table")
                    .property("rows[0]").byFactory()
            );

            jFactory.factory(Row.class).spec(instance -> instance.spec()
                    .property("cells").reverseAssociation("row")
                    .property("cells[0]").byFactory()
            );

            Table table = jFactory.create(Table.class);

            assertThat(cached).containsExactly(table, table.getRows().get(0), table.getRows().get(0).getCells().get(0));
        }

        @Test
        void should_support_define_single_reverse_association_in_parent_spec() {
            jFactory.factory(Person.class).spec(instance -> instance.spec()
                    .property("id").reverseAssociation("person")
            );

            Person person = jFactory.type(Person.class)
                    .property("id.number", "007")
                    .create();

            assertThat(person.getId())
                    .hasFieldOrPropertyWithValue("number", "007")
                    .hasFieldOrPropertyWithValue("person", person);
        }

        @Test
        void should_always_create_sub_object_when_use_reverse_association() {
            jFactory.factory(Person.class).spec(instance -> instance.spec()
                    .property("id").reverseAssociation("person")
            );
            jFactory.type(Person.class).property("id.number", "007").create();
            jFactory.type(Person.class).property("id.number", "007").create();

            assertThat(jFactory.type(ID.class).queryAll()).hasSize(2);
        }

        @Test
        void should_always_create_sub_object_when_use_reverse_association_for_collection_property() {
            jFactory.factory(Table.class).spec(instance -> instance.spec()
                    .property("rows").reverseAssociation("table")
            );

            jFactory.type(Table.class).property("rows[0].number", 1).create();
            jFactory.type(Table.class).property("rows[0].number", 1).property("rows[1].number", 1).create();

            assertThat(jFactory.type(Row.class).queryAll()).hasSize(3);
        }
    }

    @Nested
    class Others {

        @Test
        void skip_property_in_spec() {
            jFactory.factory(IgnoreProperty.class).spec(instance ->
                    instance.spec().property("value").ignore());
            expect(jFactory.create(IgnoreProperty.class)).should("setterCalled: false");
        }
    }

    @Nested
    class SpecAsBaseType {

        //        @Test
        void use_spec_as_global_spec_and_use_origin_factory_as_base_base() {
            jFactory.factory(Bean.class).spec(instance -> instance.spec().property("content").value("base"));
            jFactory.register(BeanGlobal1.class);

            expect(jFactory.create(Bean.class)).match("{" +
                    "content: 'base'\n" +
                    "stringValue: 'base 1'\n" +
                    "}");
        }

        //        @Test
        void do_not_allow_two_global_spec() {
            jFactory.register(BeanGlobal1.class);
            assertThatThrownBy(() -> jFactory.register(BeanGlobal2.class))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(String.format("More than one @Global Spec class `%s` and `%s`",
                            BeanGlobal1.class.getName(), BeanGlobal2.class.getName()));
        }

        //        @Test
        void remove_global_spec_and_origin_type_factory_should_be_enabled() {
            jFactory.factory(Bean.class).spec(instance -> instance.spec().property("content").value("base"));
            jFactory.register(BeanGlobal1.class);
            jFactory.removeGlobalSpec(Bean.class);

            expect(jFactory.create(Bean.class)).match("{" +
                    "content: 'base'\n" +
                    "stringValue: /stringValue.*/\n" +
                    "}");
        }

        @Test
        void should_use_base_factory_at_runtime() {
            jFactory.factory(Bean.class).spec(instance -> instance.spec().property("stringValue").value("value1"));
            jFactory.register(ABean.class);
            jFactory.register(BeanGlobal1.class);

            expect(jFactory.createAs(ABean.class)).match("{" +
                    "stringValue: 'base 1'\n" +
                    "}");
        }
    }
}
