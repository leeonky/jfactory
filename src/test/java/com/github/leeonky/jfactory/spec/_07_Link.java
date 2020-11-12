package com.github.leeonky.jfactory.spec;

import com.github.leeonky.jfactory.FactorySet;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class _07_Link {
    private FactorySet factorySet = new FactorySet();

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
        public String str;
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
            factorySet.factory(Bean.class).spec(instance -> instance.spec()
                    .link("str1", "str2"));

            Bean bean = factorySet.create(Bean.class);

            assertThat(bean.str1).isEqualTo(bean.str2);
        }

        @Test
        void should_use_input_property_in_link() {
            factorySet.factory(Bean.class).spec(instance -> instance.spec()
                    .link("str1", "str2"));

            Bean bean = factorySet.type(Bean.class).property("str2", "string").create();

            assertThat(bean.str1).isEqualTo("string");
            assertThat(bean.str2).isEqualTo("string");
        }

        @Test
        void support_merge_link() {
            factorySet.factory(Bean.class).spec(instance -> instance.spec()
                    .link("str1", "str2")
                    .link("str3", "str4")
                    .link("str2", "str3"));

            Bean bean = factorySet.create(Bean.class);

            assertThat(bean.str1).isEqualTo(bean.str2);
            assertThat(bean.str2).isEqualTo(bean.str3);
            assertThat(bean.str3).isEqualTo(bean.str4);
        }

        @Test
        void support_link_with_bean_object() {
            factorySet.factory(BeanWrapper.class).spec(instance -> instance.spec()
                    .link("bean", "another"));

            Bean bean = new Bean();
            assertThat(factorySet.type(BeanWrapper.class).property("another", bean).create())
                    .hasFieldOrPropertyWithValue("bean", bean);
        }
    }

    @Nested
    class CollectionLink {

        @Test
        void link_property() {
            factorySet.factory(Beans.class).spec(instance -> instance.spec()
                    .property("beans[0]").asDefault()
                    .property("beans[1]").asDefault()
                    .property("beans[2]").asDefault()
                    .link("beans[0].str1", "beans[1].str1", "beans[2].str1"));

            Beans beans = factorySet.create(Beans.class);

            assertThat(beans.beans[0].str1).isEqualTo(beans.beans[1].str1);
            assertThat(beans.beans[1].str1).isEqualTo(beans.beans[2].str1);
        }

        @Test
        void support_link_with_bean_object() {
            factorySet.factory(Beans.class).spec(instance -> instance.spec()
                    .link("beans[0]", "beans[1]")
                    .link("beans[1]", "bean"));

            Bean bean = new Bean();
            Beans beans = factorySet.type(Beans.class).property("bean", bean).create();
            assertThat(beans.getBeans())
                    .containsExactly(bean, bean);
        }

//        @Test
//        void support_link_with_value_type_collection_element_and_property() {
//            factorySet.factory(Strings.class).spec(instance -> instance.spec()
//                    .property("value").value("hello")
//                    .link("strings[0]", "value"));
//
//            Strings strings = factorySet.create(Strings.class);
//            assertThat(strings.getStrings()[0]).isEqualTo(strings.getValue()).isEqualTo("hello");
//        }
    }

    @Nested
    class NestedLink {

        @Test
        void support_nest_object_link() {
            factorySet.factory(Bean.class).spec(instance -> instance.spec().link("str1", "str2"));

            factorySet.factory(BeanWrapper.class).spec(instance -> instance.spec()
                    .property("bean").asDefault()
                    .link("str", "bean.str1"));

            BeanWrapper beanWrapper = factorySet.create(BeanWrapper.class);

            assertThat(beanWrapper.getBean().getStr1()).isEqualTo(beanWrapper.getBean().getStr2());
            assertThat(beanWrapper.getBean().getStr1()).isEqualTo(beanWrapper.getStr());
        }

        @Test
        void support_nest_object_link_in_collection() {
            factorySet.factory(Bean.class).spec(instance -> instance.spec().link("str1", "str2"));
            factorySet.factory(Beans.class).spec(instance -> instance.spec()
                    .property("beans[0]").asDefault());

            Beans beans = factorySet.create(Beans.class);

            assertThat(beans.beans[0].getStr1()).isEqualTo(beans.beans[0].getStr2());
        }
    }
}
