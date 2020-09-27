package com.github.leeonky.jfactory.spec;

import com.github.leeonky.jfactory.FactorySet;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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

    @Nested
    class FlattenDependency {

        @Test
        void depends_on_one_property() {
            factorySet.factory(Bean.class).spec(instance ->
                    instance.spec().property("content").dependsOn("intValue", Object::toString));

            Bean bean = factorySet.create(Bean.class);

            assertThat(bean.content).isEqualTo(String.valueOf(bean.intValue));
        }

//        @Test
//        void depends_on_property_list() {
//            factorySet.factory(Bean.class).define((argument, spec) -> {
//                spec.property("content").dependsOn(new String[]{"intValue", "stringValue"}, args -> args[0].toString() + args[1]);
//            });
//
//            Bean bean = factorySet.create(Bean.class);
//
//            assertThat(bean.content).isEqualTo(bean.intValue + bean.getStringValue());
//        }
//
//        @Test
//        void dependency_chain_in_one_object() {
//            factorySet.factory(Beans.class).define((argument, spec) -> {
//                spec.property("bean1").dependsOn("bean2", obj -> obj);
//                spec.property("bean2").dependsOn("bean3", obj -> obj);
//            });
//
//            Bean bean = new Bean();
//
//            assertThat(factorySet.type(Beans.class).property("bean3", bean).create())
//                    .hasFieldOrPropertyWithValue("bean1", bean)
//                    .hasFieldOrPropertyWithValue("bean2", bean)
//                    .hasFieldOrPropertyWithValue("bean3", bean)
//            ;
//        }
//
//        @Nested
//        class Override {
//
//            @Test
//            void ignore_original_spec_when_dependency_override_spec() {
//                factorySet.factory(Beans.class).define((argument, spec) -> {
//                    spec.property("bean1").value(null);
//                    spec.property("bean1").dependsOn("bean2", obj -> obj);
//                });
//                Bean bean2 = new Bean();
//
//                assertThat(factorySet.type(Beans.class).property("bean2", bean2).create())
//                        .hasFieldOrPropertyWithValue("bean1", bean2);
//            }
//
//            @Test
//            void ignore_dependency_when_input_property_override_dependency() {
//                factorySet.factory(Beans.class).define((argument, spec) -> {
//                    spec.property("bean1").dependsOn("bean2", obj -> obj);
//                });
//
//                Bean bean1 = new Bean();
//                Bean bean2 = new Bean();
//                Beans beans = factorySet.type(Beans.class)
//                        .property("bean1", bean1)
//                        .property("bean2", bean2).create();
//
//                assertThat(beans)
//                        .hasFieldOrPropertyWithValue("bean1", bean1)
//                        .hasFieldOrPropertyWithValue("bean2", bean2)
//                ;
//            }
//        }
    }
}
