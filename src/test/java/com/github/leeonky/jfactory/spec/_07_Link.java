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
//
//        private void assertLink(String property, String value, String... properties) {
//            Bean bean = factorySet.type(Bean.class).property(property, value).create();
//            for (String p : properties)
//                assertThat(bean).hasFieldOrPropertyWithValue(p, value);
//        }
//
//        @Test
//        void support_link_link_producer() {
//            factorySet.factory(Bean.class).define((argument, spec) -> {
//                spec.link("str1", "str2");
//                spec.link("str2", "str3");
//            });
//
//            assertLink("str1", "string", "str1", "str2", "str3");
//            assertLink("str2", "string", "str1", "str2", "str3");
//            assertLink("str3", "string", "str1", "str2", "str3");
//        }
//
//        @Test
//        void support_producer_link_link() {
//            factorySet.factory(Bean.class).define((argument, spec) -> {
//                spec.link("str1", "str2");
//                spec.link("str3", "str2");
//            });
//
//            assertLink("str1", "string", "str1", "str2", "str3");
//            assertLink("str2", "string", "str1", "str2", "str3");
//            assertLink("str3", "string", "str1", "str2", "str3");
//        }
//
//        @Test
//        void support_link_link_link() {
//            factorySet.factory(Bean.class).define((argument, spec) -> {
//                spec.link("str1", "str2");
//                spec.link("str3", "str4");
//                spec.link("str2", "str3");
//            });
//
//            Bean bean = factorySet.create(Bean.class);
//
//            assertThat(bean.str1).isEqualTo(bean.str2);
//            assertThat(bean.str2).isEqualTo(bean.str3);
//            assertThat(bean.str3).isEqualTo(bean.str4);
//        }
//
//        @Test
//        void support_link_with_bean_object() {
//            factorySet.factory(BeanWrapper.class).define((argument, spec) -> {
//                spec.link("bean", "another");
//            });
//
//            Bean bean = new Bean();
//            assertThat(factorySet.type(BeanWrapper.class).property("another", bean).create())
//                    .hasFieldOrPropertyWithValue("bean", bean);
//        }
    }

}
