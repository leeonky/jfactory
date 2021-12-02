package com.github.leeonky.jfactory.spec;

import com.github.leeonky.jfactory.Builder;
import com.github.leeonky.jfactory.JFactory;
import com.github.leeonky.jfactory.PropertyValue;
import com.github.leeonky.jfactory.TypeReference;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;

import static com.github.leeonky.dal.extension.assertj.DALAssert.expect;
import static org.assertj.core.api.Assertions.assertThat;

public class _08_TypeReference {
    private final JFactory jFactory = new JFactory();

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Bean {
        private String value;
    }

    @Nested
    class JavaBean {
        @Test
        void support_create_java_class_by_type_reference() {
            String string = jFactory.type(new TypeReference<String>() {
            }).create();

            assertThat(string).isEmpty();
        }
    }

    @Nested
    class Collection {

        @Test
        void support_create_empty_array_list_from_given_collection_type() {
            ArrayList<String> strings = jFactory.type(new TypeReference<ArrayList<String>>() {
            }).create();

            assertThat(strings).isEmpty();
        }

        @Test
        void support_create_empty_hash_set_from_given_collection_type() {
            HashSet<String> strings = jFactory.type(new TypeReference<HashSet<String>>() {
            }).create();

            assertThat(strings).isEmpty();
        }

        @Test
        void support_create_empty_array() {
            String[] strings = jFactory.type(String[].class).create();

            assertThat(strings).isEmpty();
        }

        @Nested
        class BuildWithElementProperty {

            @Test
            void build_collection_with_element_property() {
                Bean bean = new Bean();
                assertThat(jFactory.type(new TypeReference<ArrayList<Bean>>() {
                }).property("[0]", bean).create()).containsExactly(bean);
            }

            @Test
            void build_array_with_element_property() {
                Bean bean = new Bean();
                assertThat(jFactory.type(Bean[].class).property("[0]", bean).create()).containsExactly(bean);
            }

            @Test
            void build_with_one_default_bean_element() {
                Bean inputBean = new Bean();
                assertThat(jFactory.type(Bean[].class).property("[1]", inputBean).create())
                        .containsExactly(null, inputBean);
            }

            @Test
            void build_with_one_default_value_element() {
                assertThat(jFactory.type(String[].class).property("[1]", "hello").create()[0])
                        .isInstanceOf(String.class);
            }


            @Test
            void build_list_with_property_value() {
                expect(jFactory.type(Bean[].class).properties(new PropertyValue() {
                    @Override
                    public <T> Builder<T> setToBuilder(String property, Builder<T> builder) {
                        return builder.property("[0].value", "hello");
                    }
                }).create()).match("| value |\n| 'hello' |");
            }
        }
    }

//    @Nested
//    class BuildWithSpecAndElementProperty {
//
//// with element
//// one default , one given
//    }
}
