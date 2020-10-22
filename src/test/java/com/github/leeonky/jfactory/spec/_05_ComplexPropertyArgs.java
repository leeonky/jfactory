package com.github.leeonky.jfactory.spec;

import com.github.leeonky.jfactory.Builder;
import com.github.leeonky.jfactory.FactorySet;
import com.github.leeonky.jfactory.MixIn;
import com.github.leeonky.jfactory.Spec;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class _05_ComplexPropertyArgs {

    private FactorySet factorySet = new FactorySet();

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

        @MixIn
        public ABean long1000() {
            property("longValue").value(1000);
            return this;
        }

        @MixIn
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

        @MixIn
        public void int200() {
            property("intValue").value(200);
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
            Builder<BeansWrapper> builder = factorySet.type(BeansWrapper.class)
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
            factorySet.register(ABean.class);
            factorySet.register(AnotherBean.class);

            assertThrows(IllegalArgumentException.class, () -> factorySet.type(BeansWrapper.class)
                    .property("beans.bean(ABean).content", "hello")
                    .property("beans.bean(AnotherBean).intValue", 100)
                    .create());
        }

        @Test
        void support_merge_with_has_spec_and_no_spec() {
            factorySet.register(ABean.class);
            factorySet.register(AnotherBean.class);

            assertThat(factorySet.type(BeansWrapper.class)
                    .property("beans.bean(ABean).stringValue", "hello")
                    .property("beans.bean.intValue", 100)
                    .create().getBeans().getBean())
                    .hasFieldOrPropertyWithValue("content", "this is a bean")
                    .hasFieldOrPropertyWithValue("stringValue", "hello")
                    .hasFieldOrPropertyWithValue("intValue", 100)
            ;

            assertThat(factorySet.type(BeansWrapper.class)
                    .property("beans.bean.stringValue", "hello")
                    .property("beans.bean(ABean).intValue", 100)
                    .create().getBeans().getBean())
                    .hasFieldOrPropertyWithValue("content", "this is a bean")
                    .hasFieldOrPropertyWithValue("stringValue", "hello")
                    .hasFieldOrPropertyWithValue("intValue", 100)
            ;
        }

        @Test
        void should_raise_error_when_property_has_different_mix_in() {
            factorySet.register(ABean.class);

            assertThrows(IllegalArgumentException.class, () -> factorySet.type(BeansWrapper.class)
                    .property("beans.bean(hello ABean).content", "xxx")
                    .property("beans.bean(int100 ABean).intValue", 10)
                    .create());
        }

        @Test
        void support_merge_with_mix_in_and_empty_mix_in() {
            factorySet.register(ABean.class);

            assertThat(factorySet.type(BeansWrapper.class)
                    .property("beans.bean(hello ABean).content", "any")
                    .property("beans.bean(ABean).intValue", 10)
                    .create().getBeans().getBean())
                    .hasFieldOrPropertyWithValue("stringValue", "hello")
                    .hasFieldOrPropertyWithValue("intValue", 10)
            ;

            assertThat(factorySet.type(BeansWrapper.class)
                    .property("beans.bean(ABean).intValue", 10)
                    .property("beans.bean(hello ABean).content", "any")
                    .create().getBeans().getBean())
                    .hasFieldOrPropertyWithValue("stringValue", "hello")
                    .hasFieldOrPropertyWithValue("intValue", 10)
            ;
        }

        @Test
        void should_not_merge_intently_default_creation() {
            factorySet.factory(BeansPair.class).spec(instance -> instance.spec()
                    .property("beans1").asDefault(true)
                    .property("beans2").asDefault(true));

            BeansPair beansPair = factorySet.create(BeansPair.class);

            assertThat(beansPair.beans1).isNotEqualTo(beansPair.beans2);
        }

        @Nested
        class OverrideThroughMerge {

            @Test
            void new_property_should_override_old_property_1() {
                BeanCollection beans = factorySet.type(BeanCollection.class)
                        .property("list[0]", null)
                        .property("list[0].intValue", 1)
                        .create();

                assertThat(beans.list.get(0))
                        .hasFieldOrPropertyWithValue("intValue", 1);
            }

            @Test
            void new_property_should_override_old_property_2() {
                BeanCollection beans = factorySet.type(BeanCollection.class)
                        .property("list[0].intValue", 1)
                        .property("list[0]", null)
                        .create();

                assertThat(beans.list.get(0)).isNull();
            }

            @Test
            void new_property_should_override_old_property_3() {
                BeanCollections beans = factorySet.type(BeanCollections.class)
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
            assertThat(factorySet.type(Strings.class).property("strings[0]", "hello").create().getStrings())
                    .containsOnly("hello");
        }

        @Test
        void should_create_default_value_type_element_when_not_specified() {
            assertThat(factorySet.type(Strings.class).property("strings[1]", "hello").create().getStrings())
                    .containsOnly("strings#1[0]", "hello");
        }

        @Test
        void default_class_type_collection_element_is_null() {
            BeanCollection beanCollection = factorySet.type(BeanCollection.class)
                    .property("list[1].stringValue", "world")
                    .create();

            assertThat(beanCollection.getList().get(0)).isNull();
        }

        @Test
        void support_nested_element_creation_in_collection() {
            BeanCollection beanCollection = factorySet.type(BeanCollection.class)
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
            Builder<BeanCollection> builder = factorySet.type(BeanCollection.class)
                    .property("list[0].stringValue", "hello")
                    .property("list[0].intValue", 100)
                    .property("list[1].stringValue", "world")
                    .property("list[1].intValue", 200);

            BeanCollection beanCollection = builder.create();

            assertThat(builder.queryAll()).containsOnly(beanCollection);
        }

        @Test
        void also_support_spec_and_mix_in_in_element() {
            factorySet.spec(ABean.class);

            BeanCollection beanCollection = factorySet.type(BeanCollection.class)
                    .property("list[0](int100 long1000 ABean).stringValue", "hello")
                    .create();

            assertThat(beanCollection.getList().get(0))
                    .hasFieldOrPropertyWithValue("content", "this is a bean")
                    .hasFieldOrPropertyWithValue("stringValue", "hello")
                    .hasFieldOrPropertyWithValue("intValue", 100)
                    .hasFieldOrPropertyWithValue("longValue", 1000L)
            ;

            assertThat(factorySet.type(BeanCollection.class)
                    .property("list[0](int100 long1000 ABean).stringValue", "hello").queryAll())
                    .containsOnly(beanCollection);
        }

        @Test
        void support_different_type_in_each_element() {
            Bean bean = new Bean();
            Builder<BeanCollection> builder = factorySet.type(BeanCollection.class)
                    .property("list[0].stringValue", "hello")
                    .property("list[1]", bean);

            BeanCollection beanCollection = builder.create();

            assertThat(beanCollection.getList().get(0))
                    .hasFieldOrPropertyWithValue("stringValue", "hello")
            ;

            assertThat(beanCollection.getList().get(1))
                    .isEqualTo(bean)
            ;

            assertThat(builder.queryAll()).containsOnly(beanCollection);
        }
    }

    @Nested
    class IntentlyCreate {

        @Test
        void intently_query_should_return_empty() {
            factorySet.type(Bean.class)
                    .property("stringValue", "hello")
                    .create();

            assertThat(factorySet.type(Bean.class)
                    .property("stringValue!", "hello")
                    .queryAll()).isEmpty();

            factorySet.type(BeanCollection.class)
                    .property("list[0]!.stringValue", "hello")
                    .create();

            assertThat(factorySet.type(BeanCollection.class)
                    .property("list[0]!.stringValue", "hello")
                    .queryAll()).isEmpty();
        }

        @Test
        void create_nested_object_intently() {
            Bean bean = factorySet.type(Bean.class)
                    .property("stringValue", "hello")
                    .property("intValue", 100)
                    .create();

            assertThat(factorySet.type(Beans.class)
                    .property("bean!.stringValue", "hello")
                    .create().getBean()).isNotEqualTo(bean);

            assertThat(factorySet.type(BeanCollection.class)
                    .property("list[0]!.stringValue", "hello")
                    .property("list[0].intValue", 100).create().getList().get(0)).isNotEqualTo(bean);

            assertThat(factorySet.type(BeanCollection.class)
                    .property("list[0].stringValue", "hello")
                    .property("list[0]!.intValue", 100).create().getList().get(0)).isNotEqualTo(bean);
        }

        @Test
        void support_create_object_ignore_value() {
            factorySet.register(ABean.class);

            assertThat(factorySet.type(Beans.class)
                    .property("bean(int100 ABean)!", "")
                    .create().getBean())
                    .hasFieldOrPropertyWithValue("content", "this is a bean")
                    .hasFieldOrPropertyWithValue("intValue", 100)
            ;

            assertThat(factorySet.type(Beans.class)
                    .property("bean(ABean)!", "")
                    .create().getBean())
                    .hasFieldOrPropertyWithValue("content", "this is a bean")
            ;

            factorySet.factory(Bean.class).spec(instance -> instance.spec().property("content").value("content"));
            assertThat(factorySet.type(Beans.class)
                    .property("bean!", "")
                    .create().getBean())
                    .hasFieldOrPropertyWithValue("content", "content")
            ;
        }

        @Test
        void should_not_uniq_creation_in_sub_creation() {
            factorySet.type(BeanCollection.class)
                    .property("list[0]!.stringValue", "hello")
                    .property("list[1]!.stringValue", "hello")
                    .create();

            assertThat(factorySet.type(Bean.class)
                    .queryAll()).hasSize(2);
        }

        @Test
        void should_not_uniq_creation_in_sub_creation2() {
            factorySet.type(BeansPair.class)
                    .property("beans1.bean!", null)
                    .property("beans2.bean!", null)
                    .create();

            assertThat(factorySet.type(Beans.class)
                    .queryAll()).hasSize(2);
        }
    }

    @Nested
    class UniqCreation {

        @Test
        void uniq_build_in_nested_duplicated_object_creation() {
            BeansPair beansPair = factorySet.type(BeansPair.class)
                    .property("beans1.bean.stringValue", "hello")
                    .property("beans2.bean.stringValue", "hello")
                    .create();

            assertThat(factorySet.type(Bean.class).queryAll()).hasSize(1);
            assertThat(beansPair.beans1).isEqualTo(beansPair.beans2);
        }

        @Test
        void uniq_build_in_collection_element_duplicated_object_creation() {
            BeanArray beanArray = factorySet.type(BeanArray.class)
                    .property("beanArray[0].stringValue", "hello")
                    .property("beanArray[1].stringValue", "hello")
                    .create();

            assertThat(factorySet.type(Bean.class).queryAll()).hasSize(1);
            assertThat(beanArray.beanArray[0]).isEqualTo(beanArray.beanArray[1]);
        }
    }
}
