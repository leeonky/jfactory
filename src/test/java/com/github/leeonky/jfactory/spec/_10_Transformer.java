package com.github.leeonky.jfactory.spec;

import com.github.leeonky.jfactory.JFactory;
import com.github.leeonky.jfactory.Spec;
import com.github.leeonky.jfactory.Transformer;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class _10_Transformer {
    private final JFactory jFactory = new JFactory();

    @Getter
    @Setter
    public static class Bean {
        private String content;
        private String[] stringValues;
    }

    public static class ABean extends Spec<_04_Spec.Bean> {
    }

    @Nested
    class Create {

        @Nested
        class Single {
            @Test
            void matches() {
                jFactory.factory(Bean.class).transformer("content", String::toUpperCase);

                assertThat(jFactory.type(Bean.class).property("content", "abc").create().getContent()).isEqualTo("ABC");
            }

            @Test
            void not_match() {
                jFactory.factory(Bean.class).transformer("content", new Transformer() {
                    @Override
                    public Object transform(String input) {
                        fail();
                        return null;
                    }

                    @Override
                    public boolean matches(String input) {
                        return false;
                    }
                });

                assertThat(jFactory.type(Bean.class).property("content", "abc").create().getContent()).isEqualTo("abc");
            }

            @Test
            void property_is_list() {
                jFactory.factory(Bean.class).transformer("stringValues", input -> input.split(","));

                assertThat(jFactory.type(Bean.class).property("stringValues", "a,b,c").create().getStringValues())
                        .containsExactly("a", "b", "c");
            }
        }
    }

    @Nested
    class Query {

        @Nested
        class Single {

            @Test
            void matches() {
                Bean bean = jFactory.type(Bean.class).property("content", "ABC").create();
                jFactory.factory(Bean.class).transformer("content", String::toUpperCase);

                assertThat(jFactory.type(Bean.class).property("content", "abc").query()).isSameAs(bean);
            }

            @Test
            void not_match() {
                Bean bean = jFactory.type(Bean.class).property("content", "abc").create();

                jFactory.factory(Bean.class).transformer("content", new Transformer() {
                    @Override
                    public Object transform(String input) {
                        fail();
                        return null;
                    }

                    @Override
                    public boolean matches(String input) {
                        return false;
                    }
                });

                assertThat(jFactory.type(Bean.class).property("content", "abc").query()).isSameAs(bean);
            }

            //            @Test
//            TODO compare array not ok
            void property_is_list() {
                Bean bean = jFactory.type(Bean.class)
                        .property("stringValues[0]", "a")
                        .property("stringValues[1]", "b")
                        .property("stringValues[2]", "c").create();

                jFactory.factory(Bean.class).transformer("stringValues", input -> input.split(","));

                assertThat(jFactory.type(Bean.class).property("stringValues", "a,b,c").query()).isSameAs(bean);
            }
        }
    }

    @Nested
    class Legacy {

        @Nested
        class SingleValue {

            @Nested
            class TransformerOverride {
                @Test
                void transformer_on_spec() {
                    jFactory.specFactory(ABean.class).transformer("content", String::toUpperCase);
                    assertThat(jFactory.spec(ABean.class).property("content", "abc").create().getContent()).isEqualTo("ABC");
                }
            }
        }

        @Nested
        class ListValue {

            @Test
            void element_matches() {
                jFactory.factory(Bean.class).transformer("stringValues[]", String::toUpperCase);

                assertThat(jFactory.type(Bean.class).property("stringValues[0]", "a").create().getStringValues())
                        .containsExactly("A");
            }

            @Test
            void not_match() {
                jFactory.factory(Bean.class).transformer("stringValues[]", new Transformer() {
                    @Override
                    public Object transform(String input) {
                        fail();
                        return null;
                    }

                    @Override
                    public boolean matches(String input) {
                        return false;
                    }
                });

                assertThat(jFactory.type(Bean.class).property("stringValues[0]", "a").create().getStringValues())
                        .containsExactly("a");
            }

            @Test
            void should_convert_input_property_in_query() {
                jFactory.factory(Bean.class).transformer("stringValues[]", String::toUpperCase);
                Bean bean = jFactory.type(Bean.class).property("stringValues[0]", "a").create();
                Bean query = jFactory.type(Bean.class).property("stringValues[0]", "a").query();
                assertThat(query).isSameAs(bean);
                assertThat(query.getStringValues()).containsExactly("A");
            }
        }

//        TODO merge annotation with field alias
    }
}

//            TODO transformer in create, query
//            TODO transformer in single, sub object, sub element
//            TODO define in global type transformer, use in: type, spec
//            TODO define in global type transformer, override in global spec, use in: type, non global spec, global spec
//            TODO define in global type transformer no global spec;
//             in global type transformer with global spec;
//             in global type transformer override in global spec;
//             in global spec;

