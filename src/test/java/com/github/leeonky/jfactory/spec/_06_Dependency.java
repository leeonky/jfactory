package com.github.leeonky.jfactory.spec;

import com.github.leeonky.jfactory.FactorySet;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class _06_Dependency {
    private FactorySet factorySet = new FactorySet();

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
        private Bean bean;
        private int intValue;
    }

    @Getter
    @Setter
    public static class BeansWrapper {
        private Bean bean;
        private Beans beans;
    }

    @Nested
    class FlattenDependency {

        @Test
        void depends_on_one_property() {
            factorySet.factory(Bean.class).spec(instance -> instance.spec()
                    .property("content").dependsOn("intValue", Object::toString));

            Bean bean = factorySet.create(Bean.class);

            assertThat(bean.content).isEqualTo(String.valueOf(bean.intValue));
        }

        @Test
        void depends_on_property_list() {
            factorySet.factory(Bean.class).spec(instance -> instance.spec()
                    .property("content").dependsOn(asList("intValue", "stringValue"), args -> args[0].toString() + args[1]));

            Bean bean = factorySet.create(Bean.class);

            assertThat(bean.content).isEqualTo(bean.intValue + bean.getStringValue());
        }

        @Test
        void dependency_chain_in_one_object() {
            factorySet.factory(Beans.class).spec(instance -> instance.spec()
                    .property("bean1").dependsOn("bean2", obj -> obj)
                    .property("bean2").dependsOn("bean3", obj -> obj));

            Bean bean = new Bean();

            assertThat(factorySet.type(Beans.class).property("bean3", bean).create())
                    .hasFieldOrPropertyWithValue("bean1", bean)
                    .hasFieldOrPropertyWithValue("bean2", bean)
                    .hasFieldOrPropertyWithValue("bean3", bean)
            ;
        }

        @Nested
        class Override {

            @Test
            void ignore_original_spec_when_dependency_override_spec() {
                factorySet.factory(Beans.class).spec(instance -> instance.spec()
                        .property("bean1").value(null)
                        .property("bean1").dependsOn("bean2", obj -> obj));
                Bean bean2 = new Bean();

                assertThat(factorySet.type(Beans.class).property("bean2", bean2).create())
                        .hasFieldOrPropertyWithValue("bean1", bean2);
            }

            @Test
            void ignore_dependency_when_input_property_override_dependency() {
                factorySet.factory(Beans.class).spec(instance -> instance.spec()
                        .property("bean1").dependsOn("bean2", obj -> obj));

                Bean bean1 = new Bean();
                Bean bean2 = new Bean();
                Beans beans = factorySet.type(Beans.class)
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
            factorySet.factory(BeanArray.class).spec(instance -> instance.spec()
                    .property("beans[1]").dependsOn("beans[0]", obj -> obj));

            Bean bean = new Bean();

            assertThat(factorySet.type(BeanArray.class).property("beans[0]", bean).create().getBeans())
                    .containsOnly(bean, bean);
        }

        @Test
        void dependency_chain_in_array() {
            factorySet.factory(BeanArray.class).spec(instance -> instance.spec()
                    .property("beans[2]").dependsOn("beans[1]", obj -> obj)
                    .property("beans[1]").dependsOn("beans[0]", obj -> obj));

            Bean bean = new Bean();

            assertThat(factorySet.type(BeanArray.class).property("beans[0]", bean).create().getBeans())
                    .containsOnly(bean, bean, bean);
        }

        @Test
        void dependency_chain_with_array_and_property() {
            factorySet.factory(BeanArray.class).spec(instance -> instance.spec()
                    .property("beans[1]").dependsOn("beans[0]", obj -> obj)
                    .property("beans[0]").dependsOn("bean", obj -> obj));

            Bean bean = new Bean();

            BeanArray beanArray = factorySet.type(BeanArray.class).property("bean", bean).create();
            assertThat(beanArray.getBeans()).containsOnly(bean, bean);
            assertThat(beanArray.getBean()).isEqualTo(bean);
        }

        @Nested
        class Override {

            @Test
            void ignore_original_spec_when_dependency_override_spec() {
                factorySet.factory(BeanArray.class).spec(instance -> instance.spec()
                        .property("beans[1]").asDefault()
                        .property("beans[1]").dependsOn("beans[0]", obj -> obj));

                Bean bean = new Bean();

                assertThat(factorySet.type(BeanArray.class).property("beans[0]", bean).create().getBeans())
                        .containsOnly(bean, bean);
            }

            @Test
            void ignore_dependency_when_input_property_override_dependency() {
                factorySet.factory(BeanArray.class).spec(instance ->
                        instance.spec().property("beans[1]").dependsOn("beans[0]", obj -> obj));

                Bean bean = new Bean();

                assertThat(factorySet.type(BeanArray.class)
                        .property("beans[0]", bean)
                        .property("beans[1]", null)
                        .create().getBeans())
                        .containsOnly(bean, null);
            }
        }
    }

    @Nested
    class SubFieldDependency {

        @Test
        void dependency_in_different_level() {
            factorySet.factory(BeansWrapper.class).spec(instance -> instance.spec()
                    .property("beans").asDefault()
                    .property("beans.bean1").dependsOn("bean", obj -> obj));
            Bean bean = new Bean();

            assertThat(factorySet.type(BeansWrapper.class).property("bean", bean).create().getBeans())
                    .hasFieldOrPropertyWithValue("bean1", bean);

            factorySet.factory(BeansWrapper.class).spec(instance -> instance.spec()
                    .property("beans").asDefault(builder -> builder.property("bean1", bean))
                    .property("bean").dependsOn("beans.bean1", obj -> obj));

            BeansWrapper actual = factorySet.type(BeansWrapper.class).create();
            assertThat(actual)
                    .hasFieldOrPropertyWithValue("bean", bean);
        }

        @Nested
        class Override {

            @Test
            void ignore_dependency_when_input_property_override_dependency() {
                factorySet.factory(BeansWrapper.class).spec(instance -> instance.spec()
                        .property("beans").asDefault()
                        .property("beans.bean1").dependsOn("bean", obj -> obj));
                Bean bean1 = new Bean();
                Bean bean = new Bean();

                assertThat(factorySet.type(BeansWrapper.class)
                        .property("beans.bean1", bean1)
                        .property("bean", bean)
                        .create().getBeans())
                        .hasFieldOrPropertyWithValue("bean1", bean1);
            }

            @Test
            void ignore_dependency_when_input_property_override_host_object() {
                factorySet.factory(BeansWrapper.class).spec(instance -> instance.spec()
                        .property("beans").asDefault()
                        .property("beans.bean1").dependsOn("bean", obj -> obj));

                Bean bean = new Bean();
                Beans beans = new Beans();
                BeansWrapper beansWrapper = factorySet.type(BeansWrapper.class)
                        .property("beans", beans)
                        .property("bean", bean).create();

                assertThat(beansWrapper.getBeans().getBean1()).isNotEqualTo(bean);
                assertThat(beansWrapper)
                        .hasFieldOrPropertyWithValue("beans", beans)
                        .hasFieldOrPropertyWithValue("bean", bean);
            }

            @Test
            void parent_property_dependency_can_override_sub_property_spec() {
                factorySet.factory(Beans.class).spec(instance -> instance.spec()
                        .property("bean1").dependsOn("bean3", obj -> obj)
                        .property("bean1.stringValue").dependsOn("bean2", obj -> ((Bean) obj).getIntValue() + ""));

                Bean bean2 = new Bean().setIntValue(1000);
                Bean bean3 = new Bean().setStringValue("bean3");
                Beans beans = factorySet.type(Beans.class)
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
}
