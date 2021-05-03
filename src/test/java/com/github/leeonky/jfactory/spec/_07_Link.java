package com.github.leeonky.jfactory.spec;

import com.github.leeonky.jfactory.JFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static java.util.function.Function.identity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class _07_Link {
    private JFactory jFactory = new JFactory();

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Bean {
        public String str1, str2, str3, str4;
        public String s1, s2, s3, s4;
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Beans {
        public Bean[] beans;
        public Bean bean;
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class BeanWrapper {
        public Bean bean;
        public String str, str2;
        public Bean another;
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Strings {
        public String[] strings;
        public String value;
    }

    @Nested
    class FlattenLink {

        @Test
        void producer_link_producer() {
            jFactory.factory(Bean.class).spec(instance -> instance.spec()
                    .link("str1", "str2"));

            Bean bean = jFactory.create(Bean.class);

            assertThat(bean.str1).isEqualTo(bean.str2);
        }

        @Test
        void support_merge_link() {
            jFactory.factory(Bean.class).spec(instance -> instance.spec()
                    .link("str1", "str2")
                    .link("str3", "str4")
                    .link("str2", "str3"));

            Bean bean = jFactory.create(Bean.class);

            assertThat(bean.str1).isEqualTo(bean.str2);
            assertThat(bean.str2).isEqualTo(bean.str3);
            assertThat(bean.str3).isEqualTo(bean.str4);
        }

        @Test
        void support_link_with_bean_object() {
            jFactory.factory(BeanWrapper.class).spec(instance -> instance.spec()
                    .link("bean", "another"));

            Bean bean = new Bean();
            assertThat(jFactory.type(BeanWrapper.class).property("another", bean).create())
                    .hasFieldOrPropertyWithValue("bean", bean);
        }
    }

    @Nested
    class CollectionLink {

        @Test
        void link_property() {
            jFactory.factory(Beans.class).spec(instance -> instance.spec()
                    .property("beans[0]").asDefault()
                    .property("beans[1]").asDefault()
                    .property("beans[2]").asDefault()
                    .link("beans[0].str1", "beans[1].str1", "beans[2].str1"));

            Beans beans = jFactory.create(Beans.class);

            assertThat(beans.beans[0].str1).isEqualTo(beans.beans[1].str1);
            assertThat(beans.beans[1].str1).isEqualTo(beans.beans[2].str1);
        }

        @Test
        void support_link_with_bean_object() {
            jFactory.factory(Beans.class).spec(instance -> instance.spec()
                    .link("beans[0]", "beans[1]")
                    .link("beans[1]", "bean"));

            Bean bean = new Bean();
            Beans beans = jFactory.type(Beans.class).property("bean", bean).create();
            assertThat(beans.getBeans())
                    .containsExactly(bean, bean);
        }

        @Test
        void support_link_with_value_type_collection_element_and_property() {
            jFactory.factory(Strings.class).spec(instance -> instance.spec()
                    .property("value").value("hello")
                    .property("strings[0]").asDefault()
                    .link("strings[0]", "value"));

            Strings strings = jFactory.create(Strings.class);
            assertThat(strings.getStrings()[0]).isEqualTo(strings.getValue()).isEqualTo("hello");
        }
    }

    @Nested
    class NestedLink {

        @Test
        void support_nest_object_link() {
            jFactory.factory(Bean.class).spec(instance -> instance.spec()
                    .property("str1").value("hello")
                    .link("str1", "str2"));

            jFactory.factory(BeanWrapper.class).spec(instance -> instance.spec()
                    .property("bean").asDefault()
                    .link("str", "bean.str1"));

            BeanWrapper beanWrapper = jFactory.type(BeanWrapper.class).create();

            assertThat(beanWrapper.getStr())
                    .isEqualTo(beanWrapper.getBean().getStr1())
                    .isEqualTo(beanWrapper.getBean().getStr2())
                    .isEqualTo("hello");
        }

        @Test
        void support_nest_object_link_in_collection() {
            jFactory.factory(Bean.class).spec(instance -> instance.spec().link("str1", "str2"));
            jFactory.factory(Beans.class).spec(instance -> instance.spec()
                    .property("beans[0]").asDefault());

            Beans beans = jFactory.create(Beans.class);

            assertThat(beans.beans[0].getStr1()).isEqualTo(beans.beans[0].getStr2());
        }

        @Test
        void linked_producer_was_changed_by_other_link() {
            jFactory.factory(Bean.class).spec(instance -> instance.spec().property("str1").value("hello"));

            jFactory.factory(BeanWrapper.class).spec(instance -> instance.spec()
                    .property("bean").asDefault()
                    .link("bean.str1", "str")
                    .link("bean", "another"));

            assertThat(jFactory.type(BeanWrapper.class).property("another", new Bean().setStr1("world")).create())
                    .hasFieldOrPropertyWithValue("str", "world");
        }
    }

    @Nested
    class LinkPriority {

        @Test
        void use_property_value_in_link_and_keey_readonly_value() {
            jFactory.factory(BeanWrapper.class).spec(instance -> instance.spec()
                    .link("bean.str1", "str"));

            BeanWrapper beanWrapper = jFactory.type(BeanWrapper.class)
                    .property("str", "input")
                    .property("bean", new Bean().setStr1("read only value"))
                    .create();
            assertThat(beanWrapper)
                    .hasFieldOrPropertyWithValue("str", "input");
            assertThat(beanWrapper.getBean())
                    .hasFieldOrPropertyWithValue("str1", "read only value");
        }

        @Test
        void use_readonly_value_in_link() {
            jFactory.factory(BeanWrapper.class).spec(instance -> instance.spec()
                    .property("str").dependsOn("str2", identity())
                    .link("str", "bean.str1")
            );

            assertThat(jFactory.type(BeanWrapper.class)
                    .property("bean", new Bean().setStr1("read only value")).create())
                    .hasFieldOrPropertyWithValue("str", "read only value")
            ;
        }

        @Test
        void use_dependency_value_in_link() {
            jFactory.factory(Bean.class).spec(instance -> instance.spec()
                    .link("str1", "str3", "str2", "str4")
                    .property("str2").dependsOn("s1", obj -> obj)
                    .property("str3").value("hello"));

            assertThat(jFactory.type(Bean.class).property("s1", "input").create())
                    .hasFieldOrPropertyWithValue("str1", "input")
                    .hasFieldOrPropertyWithValue("str2", "input")
                    .hasFieldOrPropertyWithValue("str3", "input")
                    .hasFieldOrPropertyWithValue("str4", "input")
            ;
        }

        @Test
        void use_suppose_value_in_link() {
            jFactory.factory(Bean.class).spec(instance -> instance.spec()
                    .property("str2").value("suppose")
                    .link("str1", "str2"));

            Bean bean = jFactory.type(Bean.class).create();

            assertThat(bean.str1).isEqualTo(bean.str2).isEqualTo("suppose");
        }

        @Test
        void should_raise_error_when_has_ambiguous() {
            jFactory.factory(Bean.class).spec(instance -> instance.spec()
                    .link("str1", "str2", "str3"));

            assertThrows(RuntimeException.class, () -> jFactory.type(Bean.class)
                    .property("str1", "input1")
                    .property("str2", "input2")
                    .create());
        }
    }

    @Nested
    class LinkToReadOnlyProducer {

        @Test
        void link_with_read_only_value_from_input() {
            jFactory.factory(BeanWrapper.class).spec(instance -> instance.spec()
                    .link("bean.str1", "str"));

            assertThat(jFactory.type(BeanWrapper.class).property("bean", new Bean().setStr1("hello")).create().getStr())
                    .isEqualTo("hello");
        }

        @Test
        void should_user_default_type_value_when_parent_object_is_null() {
            jFactory.factory(BeanWrapper.class).spec(instance -> instance.spec()
                    .link("bean.str1", "str"));

            assertThat(jFactory.type(BeanWrapper.class).create().getStr()).isNull();
        }

        @Test
        void link_with_read_only_value_from_suggestion_value() {
            jFactory.factory(BeanWrapper.class).spec(instance -> instance.spec()
                    .property("bean").value(new Bean().setStr1("hello"))
                    .link("bean.str1", "str"));

            assertThat(jFactory.type(BeanWrapper.class).create().getStr()).isEqualTo("hello");
        }

        @Test
        void link_with_read_only_value_from_link() {
            jFactory.factory(BeanWrapper.class).spec(instance -> instance.spec()
                    .link("bean", "another")
                    .link("bean.str1", "str"));

            assertThat(jFactory.type(BeanWrapper.class).property("another", new Bean().setStr1("hello")).create().getStr())
                    .isEqualTo("hello");
        }
    }
}
