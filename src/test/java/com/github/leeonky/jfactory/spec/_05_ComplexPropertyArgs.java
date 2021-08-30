package com.github.leeonky.jfactory.spec;

import com.github.leeonky.jfactory.Builder;
import com.github.leeonky.jfactory.JFactory;
import com.github.leeonky.jfactory.Spec;
import com.github.leeonky.jfactory.Trait;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.leeonky.jfactory.spec._05_ComplexPropertyArgs.Enums.A;
import static com.github.leeonky.jfactory.spec._05_ComplexPropertyArgs.Enums.B;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class _05_ComplexPropertyArgs {

    private JFactory jFactory = new JFactory();

    public enum Enums {
        A, B
    }

    @Getter
    @Setter
    public static class Bean {
        private String content, stringValue;
        private int intValue;
        private long longValue;
    }

    @Getter
    @Setter
    public static class Beans {
        private Bean bean;
    }

    @Getter
    @Setter
    public static class BeanArray {
        private Bean[] beanArray;
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Strings {
        public String[] strings;
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class EnumArray {
        public Enums[] enums;
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

        @Trait
        public ABean long1000() {
            property("longValue").value(1000);
            return this;
        }

        @Trait
        public ABean hello() {
            property("stringValue").value("hello");
            return this;
        }
    }

    public static class AnotherBean extends Spec<Bean> {

        @Override
        public void main() {
            property("content").value("this is another bean");
        }

        @Trait
        public void int200() {
            property("intValue").value(200);
        }

    }

    public static class ABeans extends Spec<Beans> {
        @Override
        public void main() {
            property("bean").from(ABean.class).which(ABean::int100);
        }
    }

    @Getter
    @Setter
    public static class BeansPair {
        private Beans beans1, beans2;
    }

    @Getter
    @Setter
    public static class BeanCollection {
        private List<Bean> list;
    }

    @Getter
    @Setter
    public static class BeanCollections {
        private List<BeanCollection> list;
    }

    @Getter
    @Setter
    public static class BeansWrapper {
        private Beans beans;
    }

    @Nested
    class MergeProperty {

        @Test
        void support_specify_multi_properties_in_nested_property_creation_and_query() {
            Builder<BeansWrapper> builder = jFactory.type(BeansWrapper.class)
                    .property("beans.bean.content", "hello")
                    .property("beans.bean.intValue", 100);

            BeansWrapper beansWrapper = builder.create();

            assertThat(beansWrapper.getBeans().getBean())
                    .hasFieldOrPropertyWithValue("content", "hello")
                    .hasFieldOrPropertyWithValue("intValue", 100)
            ;

            assertThat(builder.query()).isEqualTo(beansWrapper);
        }

        @Test
        void should_raise_error_when_property_has_different_spec() {
            jFactory.register(ABean.class);
            jFactory.register(AnotherBean.class);

            assertThrows(IllegalArgumentException.class, () -> jFactory.type(BeansWrapper.class)
                    .property("beans.bean(ABean).content", "hello")
                    .property("beans.bean(AnotherBean).intValue", 100)
                    .create());
        }

        @Test
        void support_merge_with_has_spec_and_no_spec() {
            jFactory.register(ABean.class);
            jFactory.register(AnotherBean.class);

            assertThat(jFactory.type(BeansWrapper.class)
                    .property("beans.bean(ABean).stringValue", "hello")
                    .property("beans.bean.intValue", 100)
                    .create().getBeans().getBean())
                    .hasFieldOrPropertyWithValue("content", "this is a bean")
                    .hasFieldOrPropertyWithValue("stringValue", "hello")
                    .hasFieldOrPropertyWithValue("intValue", 100)
            ;

            assertThat(jFactory.type(BeansWrapper.class)
                    .property("beans.bean.stringValue", "hello")
                    .property("beans.bean(ABean).intValue", 100)
                    .create().getBeans().getBean())
                    .hasFieldOrPropertyWithValue("content", "this is a bean")
                    .hasFieldOrPropertyWithValue("stringValue", "hello")
                    .hasFieldOrPropertyWithValue("intValue", 100)
            ;
        }

        @Test
        void should_raise_error_when_property_has_different_trait() {
            jFactory.register(ABean.class);

            assertThrows(IllegalArgumentException.class, () -> jFactory.type(BeansWrapper.class)
                    .property("beans.bean(hello ABean).content", "xxx")
                    .property("beans.bean(int100 ABean).intValue", 10)
                    .create());
        }

        @Test
        void support_merge_with_trait_and_empty_trait() {
            jFactory.register(ABean.class);

            assertThat(jFactory.type(BeansWrapper.class)
                    .property("beans.bean(hello ABean).content", "any")
                    .property("beans.bean(ABean).intValue", 10)
                    .create().getBeans().getBean())
                    .hasFieldOrPropertyWithValue("stringValue", "hello")
                    .hasFieldOrPropertyWithValue("intValue", 10)
            ;

            assertThat(jFactory.type(BeansWrapper.class)
                    .property("beans.bean(ABean).intValue", 10)
                    .property("beans.bean(hello ABean).content", "any")
                    .create().getBeans().getBean())
                    .hasFieldOrPropertyWithValue("stringValue", "hello")
                    .hasFieldOrPropertyWithValue("intValue", 10)
            ;
        }

        @Nested
        class OverrideThroughMerge {

            @Test
            void new_property_should_override_old_property_1() {
                BeanCollection beans = jFactory.type(BeanCollection.class)
                        .property("list[0]", null)
                        .property("list[0].intValue", 1)
                        .create();

                assertThat(beans.list.get(0))
                        .hasFieldOrPropertyWithValue("intValue", 1);
            }

            @Test
            void new_property_should_override_old_property_2() {
                BeanCollection beans = jFactory.type(BeanCollection.class)
                        .property("list[0].intValue", 1)
                        .property("list[0]", null)
                        .create();

                assertThat(beans.list.get(0)).isNull();
            }

            @Test
            void new_property_should_override_old_property_3() {
                BeanCollections beans = jFactory.type(BeanCollections.class)
                        .property("list[0].list[0].intValue", 1)
                        .property("list[0].list", null)
                        .create();

                assertThat(beans.list.get(0).getList()).isNull();
            }
        }
    }

    @Nested
    class SupportCollection {

        @Test
        void support_specify_element_in_property() {
            assertThat(jFactory.type(Strings.class).property("strings[0]", "hello").create().getStrings())
                    .containsExactly("hello");
        }

        @Test
        void should_create_default_value_type_element_when_not_specified() {
            assertThat(jFactory.type(Strings.class).property("strings[1]", "hello").create().getStrings())
                    .containsExactly("strings#1[0]", "hello");
        }

        @Test
        void default_class_type_collection_element_is_null() {
            BeanCollection beanCollection = jFactory.type(BeanCollection.class)
                    .property("list[1].stringValue", "world")
                    .create();

            assertThat(beanCollection.getList().get(0)).isNull();
        }

        @Test
        void support_nested_element_creation_in_collection() {
            BeanCollection beanCollection = jFactory.type(BeanCollection.class)
                    .property("list[0].stringValue", "hello")
                    .property("list[0].intValue", 100)
                    .create();

            assertThat(beanCollection.getList().size()).isEqualTo(1);

            assertThat(beanCollection.getList().get(0))
                    .hasFieldOrPropertyWithValue("stringValue", "hello")
                    .hasFieldOrPropertyWithValue("intValue", 100)
            ;
        }

        @Test
        void support_query_with_collection_element() {
            Builder<BeanCollection> builder = jFactory.type(BeanCollection.class)
                    .property("list[0].stringValue", "hello")
                    .property("list[0].intValue", 100)
                    .property("list[1].stringValue", "world")
                    .property("list[1].intValue", 200);

            BeanCollection beanCollection = builder.create();

            assertThat(builder.queryAll()).containsExactly(beanCollection);
        }

        @Test
        void also_support_spec_and_trait_in_element() {
            jFactory.spec(ABean.class);

            BeanCollection beanCollection = jFactory.type(BeanCollection.class)
                    .property("list[0](int100 long1000 ABean).stringValue", "hello")
                    .create();

            assertThat(beanCollection.getList().get(0))
                    .hasFieldOrPropertyWithValue("content", "this is a bean")
                    .hasFieldOrPropertyWithValue("stringValue", "hello")
                    .hasFieldOrPropertyWithValue("intValue", 100)
                    .hasFieldOrPropertyWithValue("longValue", 1000L)
            ;

            assertThat(jFactory.type(BeanCollection.class)
                    .property("list[0](int100 long1000 ABean).stringValue", "hello").queryAll())
                    .containsExactly(beanCollection);
        }

        @Test
        void support_different_type_in_each_element() {
            Bean bean = new Bean();
            Builder<BeanCollection> builder = jFactory.type(BeanCollection.class)
                    .property("list[0].stringValue", "hello")
                    .property("list[1]", bean);

            BeanCollection beanCollection = builder.create();

            assertThat(beanCollection.getList().get(0))
                    .hasFieldOrPropertyWithValue("stringValue", "hello")
            ;

            assertThat(beanCollection.getList().get(1))
                    .isEqualTo(bean)
            ;

            assertThat(builder.queryAll()).containsExactly(beanCollection);
        }

        @Test
        void should_create_default_enum_value_type_element_when_not_specified() {
            assertThat(jFactory.type(EnumArray.class).property("enums[1]", B).create().getEnums())
                    .containsExactly(A, B);
        }
    }

    @Nested
    class IntentlyCreate {

        @Test
        void intently_query_should_return_empty() {
            jFactory.type(Bean.class)
                    .property("stringValue", "hello")
                    .create();

            assertThat(jFactory.type(Bean.class)
                    .property("stringValue!", "hello")
                    .queryAll()).isEmpty();

            jFactory.type(BeanCollection.class)
                    .property("list[0]!.stringValue", "hello")
                    .create();

            assertThat(jFactory.type(BeanCollection.class)
                    .property("list[0]!.stringValue", "hello")
                    .queryAll()).isEmpty();
        }

        @Test
        void create_nested_object_intently() {
            Bean bean = jFactory.type(Bean.class)
                    .property("stringValue", "hello")
                    .property("intValue", 100)
                    .create();

            assertThat(jFactory.type(Beans.class)
                    .property("bean!.stringValue", "hello")
                    .create().getBean()).isNotEqualTo(bean);

            assertThat(jFactory.type(BeanCollection.class)
                    .property("list[0]!.stringValue", "hello")
                    .property("list[0].intValue", 100).create().getList().get(0)).isNotEqualTo(bean);

            assertThat(jFactory.type(BeanCollection.class)
                    .property("list[0].stringValue", "hello")
                    .property("list[0]!.intValue", 100).create().getList().get(0)).isNotEqualTo(bean);
        }

        @Test
        void support_create_object_ignore_value() {
            jFactory.register(ABean.class);

            assertThat(jFactory.type(Beans.class)
                    .property("bean(int100 ABean)!", "")
                    .create().getBean())
                    .hasFieldOrPropertyWithValue("content", "this is a bean")
                    .hasFieldOrPropertyWithValue("intValue", 100)
            ;

            assertThat(jFactory.type(Beans.class)
                    .property("bean(ABean)!", "")
                    .create().getBean())
                    .hasFieldOrPropertyWithValue("content", "this is a bean")
            ;

            jFactory.factory(Bean.class).spec(instance -> instance.spec().property("content").value("content"));
            assertThat(jFactory.type(Beans.class)
                    .property("bean!", "")
                    .create().getBean())
                    .hasFieldOrPropertyWithValue("content", "content")
            ;
        }

        @Test
        void should_not_uniq_creation_in_sub_creation() {
            jFactory.type(BeanCollection.class)
                    .property("list[0]!.stringValue", "hello")
                    .property("list[1]!.stringValue", "hello")
                    .create();

            assertThat(jFactory.type(Bean.class)
                    .queryAll()).hasSize(2);
        }

        @Test
        void should_not_uniq_creation_in_sub_creation2() {
            jFactory.type(BeansPair.class)
                    .property("beans1.bean!", null)
                    .property("beans2.bean!", null)
                    .create();

            assertThat(jFactory.type(Beans.class)
                    .queryAll()).hasSize(2);
        }
    }

    @Nested
    class OverrideDefinition {

        @Test
        void should_use_pre_define_spec_when_property_not_specify_spec() {
            assertThat(jFactory.spec(ABeans.class).property("bean.stringValue", "hello").create().getBean())
                    .hasFieldOrPropertyWithValue("stringValue", "hello")
                    .hasFieldOrPropertyWithValue("content", "this is a bean")
                    .hasFieldOrPropertyWithValue("intValue", 100);
        }

        @Test
        void should_use_specified_spec_when_property_specify_spec() {
            jFactory.spec(AnotherBean.class);

            assertThat(jFactory.spec(ABeans.class).property("bean(int200 AnotherBean).stringValue", "hello").create().getBean())
                    .hasFieldOrPropertyWithValue("stringValue", "hello")
                    .hasFieldOrPropertyWithValue("content", "this is another bean")
                    .hasFieldOrPropertyWithValue("intValue", 200);
        }

        @Test
        void use_spec_chain_in_property() {
            jFactory.register(ABean.class);
            jFactory.register(ABeans.class);

            BeansWrapper beansWrapper = jFactory.type(BeansWrapper.class)
                    .property("beans(ABeans).bean(int100 ABean).stringValue", "hello").create();

            assertThat(beansWrapper.getBeans().getBean())
                    .hasFieldOrPropertyWithValue("stringValue", "hello")
                    .hasFieldOrPropertyWithValue("intValue", 100);
        }
    }
}
