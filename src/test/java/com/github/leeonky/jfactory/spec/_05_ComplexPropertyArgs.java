package com.github.leeonky.jfactory.spec;

import com.github.leeonky.jfactory.FactorySet;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class _05_ComplexPropertyArgs {

    private FactorySet factorySet = new FactorySet();

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Strings {
        public String[] strings;
    }

    @Nested
    class SupportCollection {

        @Test
        void support_specify_element_in_property() {
            assertThat(factorySet.type(Strings.class).property("strings[0]", "hello").create().getStrings())
                    .containsOnly("hello");
        }

//        @Test
//        void default_element_value_should_generated_from_value_factory() {
//            assertThat(factorySet.type(Strings.class).property("strings[1]", "hello").create().getStrings()[0])
//                    .isInstanceOf(String.class);
//        }

//        @Test
//        void support_element_in_build() {
//            BeanCollection beanCollection = factorySet.type(BeanCollection.class)
//                    .property("list[0].stringValue", "hello")
//                    .property("list[0].intValue", 100)
//                    .property("list[1].stringValue", "world")
//                    .property("list[1].intValue", 200)
//                    .create();
//
//            assertThat(beanCollection.getList().size()).isEqualTo(2);
//
//            assertThat(beanCollection.getList().get(0))
//                    .hasFieldOrPropertyWithValue("stringValue", "hello")
//                    .hasFieldOrPropertyWithValue("intValue", 100)
//            ;
//            assertThat(beanCollection.getList().get(1))
//                    .hasFieldOrPropertyWithValue("stringValue", "world")
//                    .hasFieldOrPropertyWithValue("intValue", 200)
//            ;
//        }
//
//        @Test
//        void support_element_in_query() {
//            Builder<BeanCollection> builder1 = factorySet.type(BeanCollection.class)
//                    .property("list[0].stringValue", "hello")
//                    .property("list[0].intValue", 100)
//                    .property("list[1].stringValue", "world")
//                    .property("list[1].intValue", 200);
//
//            Builder<BeanCollection> builder2 = factorySet.type(BeanCollection.class)
//                    .property("list[0].stringValue", "goodbye")
//                    .property("list[0].intValue", 300)
//                    .property("list[1].stringValue", "world")
//                    .property("list[1].intValue", 400);
//
//            BeanCollection beanCollection1 = builder1.create();
//            BeanCollection beanCollection2 = builder2.create();
//
//            assertThat(builder1.queryAll()).containsOnly(beanCollection1);
//            assertThat(builder2.queryAll()).containsOnly(beanCollection2);
//        }
//
//        @Test
//        void also_support_definition_and_mix_in_in_element() {
//            factorySet.define(ABean.class);
//
//            BeanCollection beanCollection = factorySet.type(BeanCollection.class)
//                    .property("list[0](int100 ABean).stringValue", "hello")
//                    .create();
//
//            assertThat(beanCollection.getList().get(0))
//                    .hasFieldOrPropertyWithValue("content", "this is a bean")
//                    .hasFieldOrPropertyWithValue("stringValue", "hello")
//                    .hasFieldOrPropertyWithValue("intValue", 100)
//            ;
//
//            assertThat(factorySet.type(BeanCollection.class)
//                    .property("list[0](int100 ABean).stringValue", "hello").queryAll())
//                    .containsOnly(beanCollection);
//        }
//
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
