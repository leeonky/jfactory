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
    }

    @Getter
    @Setter
    public static class BeanCollection {
        private List<Bean> list;
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
        void also_support_definition_and_mix_in_in_element() {
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

//        @Test
//        void support_different_type_in_each_element() {
//            Bean bean = new Bean();
//            Builder<BeanCollection> builder = factorySet.type(BeanCollection.class)
//                    .property("list[0].stringValue", "hello")
//                    .property("list[1]", bean);
//
//            BeanCollection beanCollection = builder.create();
//
//            assertThat(beanCollection.getList().get(0))
//                    .hasFieldOrPropertyWithValue("stringValue", "hello")
//            ;
//
//            assertThat(beanCollection.getList().get(1))
//                    .isEqualTo(bean)
//            ;
//
//            assertThat(builder.queryAll()).containsOnly(beanCollection);
//        }
    }
}
