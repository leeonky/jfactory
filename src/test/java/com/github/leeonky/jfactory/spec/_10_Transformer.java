package com.github.leeonky.jfactory.spec;

import com.github.leeonky.jfactory.Global;
import com.github.leeonky.jfactory.JFactory;
import com.github.leeonky.jfactory.Spec;
import com.github.leeonky.jfactory.Transformer;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.github.leeonky.dal.Assertions.expect;
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

    @Getter
    @Setter
    public static class BeanWrapper {
        private Bean bean;
    }

    @Getter
    @Setter
    public static class BeanList {
        private Bean[] beans;
    }

    public static class ExtendBean extends Bean {
    }

    public static class ABean extends Spec<Bean> {
    }

    public static class AnotherBean extends Spec<Bean> {
    }

    @Global
    public static class NoOverrideABean extends Spec<Bean> {
    }

    @Global
    public static class OverrideABean extends Spec<Bean> {
    }

    public static class AExtendBean extends Spec<ExtendBean> {
    }

    public static class ABeanWithMore extends ABean {
    }

    @Global
    public static class GlobalABean extends Spec<Bean> {
    }

    @Nested
    class Create {

        @Nested
        class Single {

            //            @Nested
            class DefineInType {

                //                @Test
                void matches_in_type() {
                    jFactory.factory(Bean.class).transformer("content", String::toUpperCase);

                    assertThat(jFactory.type(Bean.class).property("content", "abc").create().getContent()).isEqualTo("ABC");
                }

                //                @Test
                void matches_in_sub_type() {
                    jFactory.factory(Bean.class).transformer("content", String::toUpperCase);

                    assertThat(jFactory.type(ExtendBean.class).property("content", "abc").create().getContent()).isEqualTo("ABC");
                }

                //                @Test
                void override_super_transformer() {
                    jFactory.factory(Bean.class).transformer("content", String::toUpperCase);
                    jFactory.factory(ExtendBean.class).transformer("content", s -> "(" + s + ")");

                    assertThat(jFactory.type(ExtendBean.class).property("content", "abc").create().getContent()).isEqualTo("(abc)");
                }

                //                @Test
                void matches_in_spec() {
                    jFactory.factory(Bean.class).transformer("content", String::toUpperCase);

                    assertThat(jFactory.spec(ABean.class).property("content", "abc").create().getContent()).isEqualTo("ABC");
                }

                //                @Test
                void matches_spec_in_sub_type() {
                    jFactory.factory(Bean.class).transformer("content", String::toUpperCase);

                    assertThat(jFactory.spec(AExtendBean.class).property("content", "abc").create().getContent()).isEqualTo("ABC");
                }

                //                @Test
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

                //                @Test
                void property_is_list() {
                    jFactory.factory(Bean.class).transformer("stringValues", input -> input.split(","));

                    assertThat(jFactory.type(Bean.class).property("stringValues", "a,b,c").create().getStringValues())
                            .containsExactly("a", "b", "c");
                }

                //                @Nested
                class NoOverrideSpec {

                    //                    @Test
                    void matches_in_type() {
                        jFactory.factory(Bean.class).transformer("content", String::toUpperCase);
                        jFactory.register(NoOverrideABean.class);

                        assertThat(jFactory.type(Bean.class).property("content", "abc").create().getContent()).isEqualTo("ABC");
                    }

                    //                    @Test
                    void matches_in_sup_type() {
                        jFactory.factory(Bean.class).transformer("content", String::toUpperCase);
                        jFactory.register(NoOverrideABean.class);

                        assertThat(jFactory.type(ExtendBean.class).property("content", "abc").create().getContent()).isEqualTo("ABC");
                    }

                    //                    @Test
                    void matches_in_another_spec() {
                        jFactory.factory(Bean.class).transformer("content", String::toUpperCase);
                        jFactory.register(NoOverrideABean.class);

                        assertThat(jFactory.spec(ABean.class).property("content", "abc").create().getContent()).isEqualTo("ABC");
                    }

                    //                    @Test
                    void matches_in_global_spec() {
                        jFactory.factory(Bean.class).transformer("content", String::toUpperCase);
                        jFactory.register(NoOverrideABean.class);

                        assertThat(jFactory.spec(NoOverrideABean.class).property("content", "abc").create().getContent()).isEqualTo("ABC");
                    }
                }

                //                @Nested
                class OverrideSpec {

                    //                    @Test
                    void matches_in_type() {
                        jFactory.factory(Bean.class).transformer("content", String::toUpperCase);
                        jFactory.specFactory(OverrideABean.class).transformer("content", str -> "(" + str + ")");

                        assertThat(jFactory.type(Bean.class).property("content", "abc").create().getContent()).isEqualTo("(abc)");
                    }

                    //                    @Test
                    void matches_in_sub_type() {
                        jFactory.factory(Bean.class).transformer("content", String::toUpperCase);
                        jFactory.specFactory(OverrideABean.class).transformer("content", str -> "(" + str + ")");

                        assertThat(jFactory.type(ExtendBean.class).property("content", "abc").create().getContent()).isEqualTo("(abc)");
                    }

                    //                    @Test
                    void matches_in_another_spec() {
                        jFactory.factory(Bean.class).transformer("content", String::toUpperCase);
                        jFactory.specFactory(OverrideABean.class).transformer("content", str -> "(" + str + ")");

                        assertThat(jFactory.spec(ABean.class).property("content", "abc").create().getContent()).isEqualTo("(abc)");
                    }

                    //                    @Test
                    void matches_in_global_spec() {
                        jFactory.factory(Bean.class).transformer("content", String::toUpperCase);
                        jFactory.specFactory(OverrideABean.class).transformer("content", str -> "(" + str + ")");

                        assertThat(jFactory.spec(OverrideABean.class).property("content", "abc").create().getContent()).isEqualTo("(abc)");
                    }
                }
            }

            @Nested
            class DefineInSpec {

                //                @Test
                void not_match_in_type() {
                    jFactory.specFactory(ABean.class).transformer("content", String::toUpperCase);

                    assertThat(jFactory.type(Bean.class).property("content", "abc").create().getContent()).isEqualTo("abc");
                }

                //                @Test
                void matches_in_same_spec() {
                    jFactory.specFactory(ABean.class).transformer("content", String::toUpperCase);

                    assertThat(jFactory.spec(ABean.class).property("content", "abc").create().getContent()).isEqualTo("ABC");
                }

                //                @Test
                void matches_in_sub_spec() {
                    jFactory.specFactory(ABean.class).transformer("content", String::toUpperCase);

                    assertThat(jFactory.spec(ABeanWithMore.class).property("content", "abc").create().getContent()).isEqualTo("ABC");
                }

                //                @Test
                void matches_from_base_spec() {
                    jFactory.specFactory(GlobalABean.class).transformer("content", String::toUpperCase);

                    assertThat(jFactory.spec(ABean.class).property("content", "abc").create().getContent()).isEqualTo("ABC");
                }

                //                @Test
                void not_match_in_other_spec() {
                    jFactory.specFactory(ABean.class).transformer("content", String::toUpperCase);

                    assertThat(jFactory.spec(AnotherBean.class).property("content", "abc").create().getContent()).isEqualTo("abc");
                }

                //                @Test
                void not_match_in_other_global_spec() {
                    jFactory.specFactory(ABean.class).transformer("content", String::toUpperCase);
                    jFactory.register(NoOverrideABean.class);

                    assertThat(jFactory.spec(NoOverrideABean.class).property("content", "abc").create().getContent()).isEqualTo("abc");
                }
            }
        }

        @Nested
        class SubObject {

            @Test
            void matches_in_type() {
                jFactory.factory(Bean.class).transformer("content", String::toUpperCase);

                expect(jFactory.type(BeanWrapper.class).property("bean.content", "abc").create())
                        .should("bean.content: ABC");
            }

            @Test
            void matches_in_spec() {
                jFactory.specFactory(ABean.class).transformer("content", String::toUpperCase);

                expect(jFactory.type(BeanWrapper.class).property("bean(ABean).content", "abc").create())
                        .should("bean.content: ABC");
            }
        }

        @Nested
        class SubElement {

            @Test
            void matches_in_type() {
                jFactory.factory(Bean.class).transformer("content", String::toUpperCase);

                expect(jFactory.type(BeanList.class).property("beans[0].content", "abc").create())
                        .should("beans[0].content: ABC");
            }

            @Test
            void matches_in_spec() {
                jFactory.specFactory(ABean.class).transformer("content", String::toUpperCase);

                expect(jFactory.type(BeanList.class).property("beans[0](ABean).content", "abc").create())
                        .should("beans[0].content: ABC");
            }
        }
    }

    @Nested
    class Query {

        @Nested
        class Single {

            @Nested
            class DefineInType {

                @Test
                void matches_in_type() {
                    Bean bean = jFactory.type(Bean.class).property("content", "ABC").create();
                    jFactory.factory(Bean.class).transformer("content", String::toUpperCase);

                    assertThat(jFactory.type(Bean.class).property("content", "abc").query()).isSameAs(bean);
                }

                @Test
                void matches_in_sub_type() {
                    Bean bean = jFactory.type(ExtendBean.class).property("content", "ABC").create();
                    jFactory.factory(Bean.class).transformer("content", String::toUpperCase);

                    assertThat(jFactory.type(ExtendBean.class).property("content", "abc").query()).isSameAs(bean);
                }

                @Test
                void matches_spec() {
                    Bean bean = jFactory.type(Bean.class).property("content", "ABC").create();
                    jFactory.factory(Bean.class).transformer("content", String::toUpperCase);

                    assertThat(jFactory.spec(ABean.class).property("content", "abc").query()).isSameAs(bean);
                }

                @Test
                void matches_spec_in_sub_type() {
                    Bean bean = jFactory.type(ExtendBean.class).property("content", "ABC").create();
                    jFactory.factory(Bean.class).transformer("content", String::toUpperCase);

                    assertThat(jFactory.spec(AExtendBean.class).property("content", "abc").query()).isSameAs(bean);
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

                @Test
                void property_is_list() {
                    Bean bean = jFactory.type(Bean.class)
                            .property("stringValues[0]", "a")
                            .property("stringValues[1]", "b")
                            .property("stringValues[2]", "c").create();

                    jFactory.factory(Bean.class).transformer("stringValues", input -> input.split(","));

                    assertThat(jFactory.type(Bean.class).property("stringValues", "a,b,c").query()).isSameAs(bean);
                }

                @Nested
                class NoOverrideSpec {
                    @Test
                    void matches_in_type() {
                        Bean bean = jFactory.type(Bean.class).property("content", "ABC").create();
                        jFactory.factory(Bean.class).transformer("content", String::toUpperCase);
                        jFactory.register(NoOverrideABean.class);

                        assertThat(jFactory.type(Bean.class).property("content", "abc").query()).isSameAs(bean);
                    }

                    @Test
                    void matches_in_sub_type() {
                        Bean bean = jFactory.type(ExtendBean.class).property("content", "ABC").create();
                        jFactory.factory(Bean.class).transformer("content", String::toUpperCase);
                        jFactory.register(NoOverrideABean.class);

                        assertThat(jFactory.type(ExtendBean.class).property("content", "abc").query()).isSameAs(bean);
                    }

                    @Test
                    void matches_in_another_spec() {
                        Bean bean = jFactory.type(Bean.class).property("content", "ABC").create();
                        jFactory.factory(Bean.class).transformer("content", String::toUpperCase);
                        jFactory.register(NoOverrideABean.class);

                        assertThat(jFactory.spec(ABean.class).property("content", "abc").query()).isSameAs(bean);
                    }

                    @Test
                    void matches_in_global_spec() {
                        Bean bean = jFactory.type(Bean.class).property("content", "ABC").create();
                        jFactory.factory(Bean.class).transformer("content", String::toUpperCase);
                        jFactory.register(NoOverrideABean.class);

                        assertThat(jFactory.spec(NoOverrideABean.class).property("content", "abc").query()).isSameAs(bean);
                    }
                }

                @Nested
                class OverrideSpec {

                    @Test
                    void matches_in_type() {
                        Bean bean = jFactory.type(Bean.class).property("content", "(abc)").create();
                        jFactory.factory(Bean.class).transformer("content", String::toUpperCase);
                        jFactory.specFactory(OverrideABean.class).transformer("content", str -> "(" + str + ")");

                        assertThat(jFactory.type(Bean.class).property("content", "abc").query()).isSameAs(bean);
                    }

                    @Test
                    void matches_in_sub_type() {
                        Bean bean = jFactory.type(ExtendBean.class).property("content", "(abc)").create();
                        jFactory.factory(Bean.class).transformer("content", String::toUpperCase);
                        jFactory.specFactory(OverrideABean.class).transformer("content", str -> "(" + str + ")");

                        assertThat(jFactory.type(ExtendBean.class).property("content", "abc").query()).isSameAs(bean);
                    }

                    @Test
                    void matches_in_another_spec() {
                        Bean bean = jFactory.type(Bean.class).property("content", "(abc)").create();
                        jFactory.factory(Bean.class).transformer("content", String::toUpperCase);
                        jFactory.specFactory(OverrideABean.class).transformer("content", str -> "(" + str + ")");

                        assertThat(jFactory.spec(ABean.class).property("content", "abc").query()).isSameAs(bean);
                    }

                    @Test
                    void matches_in_global_spec() {
                        Bean bean = jFactory.type(Bean.class).property("content", "(abc)").create();
                        jFactory.factory(Bean.class).transformer("content", String::toUpperCase);
                        jFactory.specFactory(OverrideABean.class).transformer("content", str -> "(" + str + ")");

                        assertThat(jFactory.spec(AnotherBean.class).property("content", "abc").query()).isSameAs(bean);
                    }
                }
            }

            @Nested
            class DefineInSpec {

                @Test
                void not_match_in_type() {
                    jFactory.type(Bean.class).property("content", "ABC").create();
                    jFactory.specFactory(ABean.class).transformer("content", String::toUpperCase);

                    assertThat(jFactory.type(Bean.class).property("content", "abc").queryAll()).isEmpty();
                }

                @Test
                void matches_in_same_spec() {
                    Bean bean = jFactory.type(Bean.class).property("content", "ABC").create();
                    jFactory.specFactory(ABean.class).transformer("content", String::toUpperCase);

                    assertThat(jFactory.spec(ABean.class).property("content", "abc").query()).isSameAs(bean);
                }

                @Test
                void matches_in_sub_spec() {
                    Bean bean = jFactory.type(Bean.class).property("content", "ABC").create();
                    jFactory.specFactory(ABean.class).transformer("content", String::toUpperCase);

                    assertThat(jFactory.spec(ABeanWithMore.class).property("content", "abc").query()).isSameAs(bean);
                }

                @Test
                void matches_from_base_spec() {
                    Bean bean = jFactory.type(Bean.class).property("content", "ABC").create();
                    jFactory.specFactory(GlobalABean.class).transformer("content", String::toUpperCase);

                    assertThat(jFactory.spec(ABean.class).property("content", "abc").query()).isSameAs(bean);
                }

                @Test
                void not_match_in_other_spec() {
                    jFactory.type(Bean.class).property("content", "ABC").create();
                    jFactory.specFactory(ABean.class).transformer("content", String::toUpperCase);

                    assertThat(jFactory.spec(AnotherBean.class).property("content", "abc").queryAll()).isEmpty();
                }

                @Test
                void not_match_in_other_global_spec() {
                    jFactory.type(Bean.class).property("content", "ABC").create();
                    jFactory.specFactory(ABean.class).transformer("content", String::toUpperCase);
                    jFactory.register(NoOverrideABean.class);

                    assertThat(jFactory.spec(NoOverrideABean.class).property("content", "abc").queryAll()).isEmpty();
                }
            }
        }

        @Nested
        class SubObject {


            @Test
            void matches_in_type() {
                Bean bean = jFactory.type(Bean.class).property("content", "ABC").create();

                jFactory.factory(Bean.class).transformer("content", String::toUpperCase);

                assertThat(jFactory.type(BeanWrapper.class).property("bean.content", "abc").create().getBean())
                        .isSameAs(bean);
            }
        }

        @Nested
        class SubElement {

            @Test
            void matches_in_type() {
                Bean bean = jFactory.type(Bean.class).property("content", "ABC").create();

                jFactory.factory(Bean.class).transformer("content", String::toUpperCase);

                assertThat(jFactory.type(BeanList.class).property("beans[0].content", "abc").create().getBeans()[0])
                        .isSameAs(bean);
            }
        }
    }

    @Nested
    class ListValue {

        @Test
        void transform_list_property() {
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
            Bean bean = jFactory.type(Bean.class).property("stringValues[0]", "A").create();
            jFactory.factory(Bean.class).transformer("stringValues[]", String::toUpperCase);
            Bean query = jFactory.type(Bean.class).property("stringValues[0]", "a").query();
            assertThat(query).isSameAs(bean);
            assertThat(query.getStringValues()).containsExactly("A");
        }
    }

//        TODO merge annotation with field alias
}

//            TODO transformer in create, query
//            transformer in single, sub object, sub element
//            define in global type transformer, use in: type, spec, sub type, extend spec
//            define in global type transformer, and no override global spec, use in: type, non global spec, global spec, sub type, extend spec
//            define in global type transformer, override in global spec, use in: type, non global spec, global spec, sub type, extend spec
//            define in spec, use in: type, same spec, another spec, another global spec, sub type, extend spec
//            define in global spec, use in: type, non global spec, global spec, sub type, extend spec
