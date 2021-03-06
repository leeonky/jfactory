package com.github.leeonky.jfactory.spec;

import com.github.leeonky.jfactory.JFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static java.util.function.Function.identity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class _06_Dependency {
    private JFactory JFactory = new JFactory();
    private int intValue = 1;

    @Test
    void should_cache_produce_value() {
        JFactory.factory(Bean.class).spec(instance -> instance.spec()
                .property("stringValue").value(() -> String.valueOf(intValue++))
        );
        JFactory.factory(Beans.class)
                .spec(instance -> instance.spec()
                        .property("bean1").asDefault()
                        .property("bean2").asDefault()
                        .property("bean2.stringValue").dependsOn("bean1.stringValue", v -> v)
                        .property("bean3").asDefault()
                        .property("bean3.stringValue").dependsOn("bean1.stringValue", v -> v)
                );

        Beans beans = JFactory.create(Beans.class);

        assertThat(beans.bean2.stringValue)
                .isEqualTo(beans.bean3.stringValue)
                .isEqualTo(beans.bean1.stringValue)
                .isEqualTo("1");
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Bean {
        private String content;
        private String stringValue;
        private int intValue;
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Beans {
        private Bean bean1, bean2, bean3;
    }

    @Getter
    @Setter
    public static class BeanArray {
        private Bean[] beans;
        private Bean[] anotherBeans;
        private Bean bean;
        private int intValue;
    }

    @Getter
    @Setter
    public static class BeansWrapper {
        private Bean bean;
        private Beans beans;
    }

    @Getter
    @Setter
    public static class BeansArray {
        private Beans[] beansArray;
    }

    @Nested
    class FlattenDependency {

        @Test
        void depends_on_one_property() {
            JFactory.factory(Bean.class).spec(instance -> instance.spec()
                    .property("content").dependsOn("intValue", Object::toString));

            Bean bean = JFactory.create(Bean.class);

            assertThat(bean.content).isEqualTo(String.valueOf(bean.intValue));
        }

        @Test
        void depends_on_property_list() {
            JFactory.factory(Bean.class).spec(instance -> instance.spec()
                    .property("content").dependsOn(asList("intValue", "stringValue"), args -> args[0].toString() + args[1]));

            Bean bean = JFactory.create(Bean.class);

            assertThat(bean.content).isEqualTo(bean.intValue + bean.getStringValue());
        }

        @Test
        void dependency_chain_in_one_object() {
            JFactory.factory(Beans.class).spec(instance -> instance.spec()
                    .property("bean1").dependsOn("bean2", obj -> obj)
                    .property("bean2").dependsOn("bean3", obj -> obj));

            Bean bean = new Bean();

            assertThat(JFactory.type(Beans.class).property("bean3", bean).create())
                    .hasFieldOrPropertyWithValue("bean1", bean)
                    .hasFieldOrPropertyWithValue("bean2", bean)
                    .hasFieldOrPropertyWithValue("bean3", bean)
            ;
        }

        @Nested
        class Override {

            @Test
            void ignore_original_spec_when_dependency_override_spec() {
                JFactory.factory(Beans.class).spec(instance -> instance.spec()
                        .property("bean1").value(null)
                        .property("bean1").dependsOn("bean2", obj -> obj));
                Bean bean2 = new Bean();

                assertThat(JFactory.type(Beans.class).property("bean2", bean2).create())
                        .hasFieldOrPropertyWithValue("bean1", bean2);
            }

            @Test
            void ignore_dependency_when_input_property_override_dependency() {
                JFactory.factory(Beans.class).spec(instance -> instance.spec()
                        .property("bean1").dependsOn("bean2", obj -> obj));

                Bean bean1 = new Bean();
                Bean bean2 = new Bean();
                Beans beans = JFactory.type(Beans.class)
                        .property("bean1", bean1)
                        .property("bean2", bean2).create();

                assertThat(beans)
                        .hasFieldOrPropertyWithValue("bean1", bean1)
                        .hasFieldOrPropertyWithValue("bean2", bean2)
                ;
            }
        }
    }

    @Nested
    class CollectionElementDependency {

        @Test
        void dependency_in_collection() {
            JFactory.factory(BeanArray.class).spec(instance -> instance.spec()
                    .property("beans[1]").dependsOn("beans[0]", obj -> obj));

            Bean bean = new Bean();

            assertThat(JFactory.type(BeanArray.class).property("beans[0]", bean).create().getBeans())
                    .containsExactly(bean, bean);
        }

        @Test
        void dependency_chain_in_array() {
            JFactory.factory(BeanArray.class).spec(instance -> instance.spec()
                    .property("beans[2]").dependsOn("beans[1]", obj -> obj)
                    .property("beans[1]").dependsOn("beans[0]", obj -> obj));

            Bean bean = new Bean();

            assertThat(JFactory.type(BeanArray.class).property("beans[0]", bean).create().getBeans())
                    .containsExactly(bean, bean, bean);
        }

        @Test
        void dependency_chain_with_array_and_property() {
            JFactory.factory(BeanArray.class).spec(instance -> instance.spec()
                    .property("beans[1]").dependsOn("beans[0]", obj -> obj)
                    .property("beans[0]").dependsOn("bean", obj -> obj));

            Bean bean = new Bean();

            BeanArray beanArray = JFactory.type(BeanArray.class).property("bean", bean).create();
            assertThat(beanArray.getBeans()).containsExactly(bean, bean);
            assertThat(beanArray.getBean()).isEqualTo(bean);
        }

        @Nested
        class Override {

            @Test
            void ignore_original_spec_when_dependency_override_spec() {
                JFactory.factory(BeanArray.class).spec(instance -> instance.spec()
                        .property("beans[1]").asDefault()
                        .property("beans[1]").dependsOn("beans[0]", obj -> obj));

                Bean bean = new Bean();

                assertThat(JFactory.type(BeanArray.class).property("beans[0]", bean).create().getBeans())
                        .containsExactly(bean, bean);
            }

            @Test
            void ignore_dependency_when_input_property_override_dependency() {
                JFactory.factory(BeanArray.class).spec(instance ->
                        instance.spec().property("beans[1]").dependsOn("beans[0]", obj -> obj));

                Bean bean = new Bean();

                assertThat(JFactory.type(BeanArray.class)
                        .property("beans[0]", bean)
                        .property("beans[1]", null)
                        .create().getBeans())
                        .containsExactly(bean, null);
            }
        }
    }

    @Nested
    class SubFieldDependency {

        @Test
        void dependency_in_different_level() {
            JFactory.factory(BeansWrapper.class).spec(instance -> instance.spec()
                    .property("beans").asDefault()
                    .property("beans.bean1").dependsOn("bean", obj -> obj));
            Bean bean = new Bean();

            assertThat(JFactory.type(BeansWrapper.class).property("bean", bean).create().getBeans())
                    .hasFieldOrPropertyWithValue("bean1", bean);

            JFactory.factory(BeansWrapper.class).spec(instance -> instance.spec()
                    .property("beans").asDefault(builder -> builder.property("bean1", bean))
                    .property("bean").dependsOn("beans.bean1", obj -> obj));

            BeansWrapper actual = JFactory.type(BeansWrapper.class).create();
            assertThat(actual)
                    .hasFieldOrPropertyWithValue("bean", bean);
        }

        @Nested
        class Override {

            @Test
            void ignore_dependency_when_input_property_override_dependency() {
                JFactory.factory(BeansWrapper.class).spec(instance -> instance.spec()
                        .property("beans").asDefault()
                        .property("beans.bean1").dependsOn("bean", obj -> obj));
                Bean bean1 = new Bean();
                Bean bean = new Bean();

                assertThat(JFactory.type(BeansWrapper.class)
                        .property("beans.bean1", bean1)
                        .property("bean", bean)
                        .create().getBeans())
                        .hasFieldOrPropertyWithValue("bean1", bean1);
            }

            @Test
            void ignore_dependency_when_input_property_override_host_object() {
                JFactory.factory(BeansWrapper.class).spec(instance -> instance.spec()
                        .property("beans").asDefault()
                        .property("beans.bean1").dependsOn("bean", obj -> obj));

                Bean bean = new Bean();
                Beans beans = new Beans();
                BeansWrapper beansWrapper = JFactory.type(BeansWrapper.class)
                        .property("beans", beans)
                        .property("bean", bean).create();

                assertThat(beansWrapper.getBeans().getBean1()).isNotEqualTo(bean);
                assertThat(beansWrapper)
                        .hasFieldOrPropertyWithValue("beans", beans)
                        .hasFieldOrPropertyWithValue("bean", bean);
            }

            @Test
            void parent_property_dependency_can_override_sub_property_spec() {
                JFactory.factory(Beans.class).spec(instance -> instance.spec()
                        .property("bean1").dependsOn("bean3", obj -> obj)
                        .property("bean1.stringValue").dependsOn("bean2", obj -> ((Bean) obj).getIntValue() + ""));

                Bean bean2 = new Bean().setIntValue(1000);
                Bean bean3 = new Bean().setStringValue("bean3");
                Beans beans = JFactory.type(Beans.class)
                        .property("bean2", bean2)
                        .property("bean3", bean3)
                        .create();

                assertThat(beans)
                        .hasFieldOrPropertyWithValue("bean1", bean3)
                        .hasFieldOrPropertyWithValue("bean2", bean2);

                assertThat(bean3).hasFieldOrPropertyWithValue("stringValue", "bean3");
            }
        }
    }

    @Nested
    class SubCollectionElementDependency {

        @Test
        void dependency_of_collection_element_property_with_default_value() {
            JFactory.factory(BeanArray.class).spec(instance -> instance.spec()
                    .property("beans[0]").asDefault()
                    .property("beans[1]").asDefault()
                    .property("beans[0].stringValue").dependsOn("beans[1].stringValue", obj -> obj));

            BeanArray beanArray = JFactory.create(BeanArray.class);
            assertThat(beanArray.getBeans()[0].getStringValue())
                    .isEqualTo(beanArray.getBeans()[1].getStringValue());
        }

        @Test
        void dependency_of_collection_element_property_with_specified_value() {
            JFactory.factory(BeanArray.class).spec(instance -> instance.spec()
                    .property("beans[0]").asDefault()
                    .property("beans[1]").asDefault()
                    .property("beans[0].stringValue").dependsOn("beans[1].stringValue", obj -> obj));

            BeanArray beanArray = JFactory.type(BeanArray.class).property("beans[1].stringValue", "hello").create();

            assertThat(beanArray.getBeans()[0].getStringValue())
                    .isEqualTo(beanArray.getBeans()[1].getStringValue())
                    .isEqualTo("hello");
        }

        @Nested
        class Override {

            @Test
            void ignore_dependency_when_input_property_override_dependency() {
                JFactory.factory(BeansArray.class).spec(instance -> instance.spec()
                        .property("beansArray[0]").asDefault()
                        .property("beansArray[1]").asDefault()
                        .property("beansArray[0].bean1").dependsOn("beansArray[1].bean2", obj -> obj));

                Bean bean = new Bean();
                BeansArray beansArray = JFactory.type(BeansArray.class).property("beansArray[0].bean1", bean).create();

                assertThat(beansArray.getBeansArray()).hasSize(2);
                assertThat(beansArray.getBeansArray()[0])
                        .hasFieldOrPropertyWithValue("bean1", bean)
                        .hasFieldOrPropertyWithValue("bean2", null);
                assertThat(beansArray.getBeansArray()[1])
                        .hasFieldOrPropertyWithValue("bean1", null)
                        .hasFieldOrPropertyWithValue("bean2", null);
            }

            @Test
            void ignore_dependency_when_input_property_override_host_object() {
                JFactory.factory(BeansArray.class).spec(instance -> instance.spec()
                        .property("beansArray[0]").asDefault()
                        .property("beansArray[1]").asDefault()
                        .property("beansArray[0].bean1").dependsOn("beansArray[1].bean2", obj -> obj));

                BeansArray beansArray = JFactory.type(BeansArray.class).property("beansArray[0]", null).create();

                assertThat(beansArray.getBeansArray()).hasSize(2);
                assertThat(beansArray.getBeansArray()[0]).isNull();
                assertThat(beansArray.getBeansArray()[1])
                        .hasFieldOrPropertyWithValue("bean1", null)
                        .hasFieldOrPropertyWithValue("bean2", null);
            }

            @Test
            void parent_property_dependency_can_override_sub_property_spec() {
                JFactory.factory(Beans.class).spec(instance -> instance.spec()
                        .property("bean1").asDefault()
                        .property("bean2").asDefault());
                JFactory.factory(BeansArray.class).spec(instance -> instance.spec()
                        .property("beansArray[0]").asDefault()
                        .property("beansArray[1]").asDefault()
                        .property("beansArray[0].bean1.intValue").dependsOn("beansArray[1].bean2.intValue", obj -> {
                            fail("should not be called");
                            return 0;
                        }));

                Beans beans = new Beans();
                BeansArray beansArray = JFactory.type(BeansArray.class).property("beansArray[0]", beans).create();

                assertThat(beansArray.getBeansArray()).hasSize(2);
                assertThat(beansArray.getBeansArray()[0]).isEqualTo(beans);
                assertThat(beansArray.getBeansArray()[1]).isNotEqualTo(beans);
            }
        }
    }

    @Nested
    class NestedDependency {

        @Test
        void dependency_in_two_object_spec_definitions() {
            JFactory.factory(BeansWrapper.class).spec(instance -> instance.spec()
                    .property("beans").asDefault()
                    .property("bean").dependsOn("beans.bean1", obj -> obj));
            JFactory.factory(Beans.class).spec(instance -> instance.spec()
                    .property("bean1").dependsOn("bean2", obj -> obj));

            Bean bean = new Bean();
            BeansWrapper beansWrapper = JFactory.type(BeansWrapper.class)
                    .property("beans.bean2", bean).create();

            assertThat(beansWrapper)
                    .hasFieldOrPropertyWithValue("bean", bean);
            assertThat(beansWrapper.getBeans())
                    .hasFieldOrPropertyWithValue("bean1", bean)
                    .hasFieldOrPropertyWithValue("bean2", bean);
        }

        @Test
        void dependency_in_two_object_spec_definitions_in_collection() {
            JFactory.factory(Bean.class).spec(instance -> instance.spec()
                    .property("content").dependsOn("stringValue", identity()));

            JFactory.factory(BeanArray.class).spec(instance -> instance.spec()
                    .property("beans[0]").asDefault());

            BeanArray beanArray = JFactory.type(BeanArray.class)
                    .property("beans[0].stringValue", "hello").create();

            assertThat(beanArray.beans[0])
                    .hasFieldOrPropertyWithValue("content", "hello");
        }
    }

    @Nested
    class TargetPropertyObjectIsNotObjectProducer {

        @Test
        void should_ignore_dependency_when_parent_object_not_set_factory() {
            JFactory.factory(Beans.class).spec(instance -> instance.spec()
                    .property("bean1.stringValue").dependsOn("bean2", obj -> ((Bean) obj).getIntValue() + ""));

            Bean bean = new Bean();
            Beans beans = JFactory.type(Beans.class)
                    .property("bean2", bean)
                    .create();

            assertThat(beans)
                    .hasFieldOrPropertyWithValue("bean1", null)
                    .hasFieldOrPropertyWithValue("bean2", bean);
        }

        @Test
        void should_ignore_dependency_in_collection() {
            JFactory.factory(BeanArray.class).spec(instance -> instance.spec()
                    .property("beans[1].stringValue").dependsOn("beans[0]", obj -> ((Bean) obj).getIntValue() + ""));

            Bean bean = new Bean();
            BeanArray beanArray = JFactory.type(BeanArray.class)
                    .property("beans[0]", bean)
                    .create();

            assertThat(beanArray.getBeans()).containsExactly(bean, null);
        }

        @Test
        void should_ignore_dependency_when_parent_object_specified_during_creation() {
            JFactory.factory(Beans.class).spec(instance -> instance.spec()
                    .property("bean1").asDefault());

            JFactory.factory(BeansWrapper.class).spec(instance -> instance.spec()
                    .property("beans").asDefault()
                    .property("beans.bean1.stringValue").dependsOn("bean", obj -> ((Bean) obj).getStringValue()));

            Bean bean = new Bean();
            BeansWrapper beansWrapper = JFactory.type(BeansWrapper.class)
                    .property("bean", new Bean().setStringValue("hello"))
                    .property("beans.bean1", bean)
                    .create();

            assertThat(beansWrapper.getBeans().getBean1().getStringValue()).isNotEqualTo("hello");
        }
    }

    @Nested
    class DependencyIsNotProducer {

        @Test
        void read_property_value_from_object() {
            Bean bean = new Bean();
            JFactory.factory(Beans.class).spec(instance -> instance.spec()
                    .property("bean1").dependsOn("bean2", obj -> obj));

            assertThat(JFactory.type(Beans.class).property("bean2", bean).create())
                    .hasFieldOrPropertyWithValue("bean1", bean)
                    .hasFieldOrPropertyWithValue("bean2", bean)
            ;
        }

        @Test
        void read_property_value_from_specified_sub_object() {
            Bean bean = new Bean().setIntValue(100);
            JFactory.factory(Beans.class).spec(instance -> instance.spec()
                    .property("bean1").asDefault()
                    .property("bean1.intValue").dependsOn("bean2.intValue", obj -> obj));

            assertThat(JFactory.type(Beans.class).property("bean2", bean).create().getBean1())
                    .hasFieldOrPropertyWithValue("intValue", 100)
                    .isNotEqualTo(bean);
        }

        @Test
        void read_property_value_from_created_sub_object() {
            JFactory.factory(Beans.class)
                    .constructor(instance -> new Beans().setBean2(new Bean().setIntValue(100)))
                    .spec(instance -> instance.spec()
                            .property("bean1").asDefault()
                            .property("bean1.intValue").dependsOn("bean2.intValue", obj -> obj));

            Beans beans = JFactory.create(Beans.class);
            assertThat(beans.getBean1())
                    .hasFieldOrPropertyWithValue("intValue", 100);
        }

        @Test
        void should_use_type_default_value_when_has_null_in_property_chain() {
            JFactory.factory(Beans.class).spec(instance -> instance.spec()
                    .property("bean1").asDefault()
                    .property("bean1.intValue").dependsOn("bean2.intValue", obj -> obj));

            assertThat(JFactory.create(Beans.class).getBean1())
                    .hasFieldOrPropertyWithValue("intValue", 0)
            ;
        }

        @Test
        void read_property_value_from_collection() {
            JFactory.factory(BeanArray.class).constructor(argument -> {
                BeanArray beanArray = new BeanArray();
                beanArray.anotherBeans = new Bean[]{new Bean().setIntValue(100)};
                return beanArray;
            }).spec(instance -> instance.spec()
                    .property("beans[0]").asDefault()
                    .property("beans[0].intValue").dependsOn("anotherBeans[0].intValue", obj -> obj));

            BeanArray beanArray = JFactory.create(BeanArray.class);

            assertThat(beanArray.getBeans()[0])
                    .hasFieldOrPropertyWithValue("intValue", 100);
        }
    }
}
