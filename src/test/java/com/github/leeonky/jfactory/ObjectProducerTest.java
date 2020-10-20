package com.github.leeonky.jfactory;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class ObjectProducerTest {
    private FactorySet factorySet = new FactorySet();

    @BeforeEach
    void registerMixin() {
        factorySet.factory(Bean.class)
                .spec("a bean", instance -> {
                })
                .spec("another bean", instance -> {
                });

        factorySet.factory(Bean2.class)
                .spec("a bean", instance -> {
                })
                .spec("another bean", instance -> {
                });
    }

    private ObjectProducer buildProducer(Function<Builder<Bean>, Builder<Bean>> modifyBuilder) {
        ObjectFactory<Bean> objectFactory = factorySet.getObjectFactorySet().queryObjectFactory(Bean.class);
        return new ObjectProducer<>(factorySet, objectFactory,
                (DefaultBuilder<Bean>) modifyBuilder.apply(new DefaultBuilder<>(objectFactory, factorySet)));
    }

    private ObjectProducer sameProducer() {
        return buildProducer(builder -> builder.property("intValue", 1).mixIn("a bean"));
    }

    @Getter
    @Setter
    public static class Bean {
        private String content, stringValue;
        private int intValue;
        private long longValue;
    }

    @Getter
    @Setter
    public static class Bean2 {
        private String content, stringValue;
        private int intValue;
        private long longValue;
    }

    @Nested
    class Hashcode {

        @Test
        void should_get_equal_hashcode_for_same_object_producer() {
            assertThat(sameProducer().hashCode())
                    .isEqualTo(sameProducer().hashCode());
        }

        @Test
        void should_not_equal_when_has_dependency() {
            ObjectProducer producer2 = sameProducer();
            producer2.addDependency(null, null, null);

            ObjectProducer producer1 = sameProducer();
            assertThat(producer2.hashCode()).isNotEqualTo(producer1.hashCode());
        }

        @Test
        void should_not_equal_when_has_link() {
            ObjectProducer producer2 = sameProducer();
            producer2.link(asList(PropertyChain.createChain("a1"), PropertyChain.createChain("a2")));

            ObjectProducer producer1 = sameProducer();
            assertThat(producer2.hashCode()).isNotEqualTo(producer1.hashCode());
        }

        @Test
        void should_not_equal_when_different_mixin() {
            assertThat(sameProducer().hashCode())
                    .isNotEqualTo(buildProducer(builder1 -> builder1.property("intValue", 1).mixIn("another bean")).hashCode());
        }

        @Test
        void should_not_equal_when_different_property() {
            assertThat(sameProducer().hashCode())
                    .isNotEqualTo(buildProducer(builder1 -> builder1.property("intValue", 2000).mixIn("a bean")).hashCode());
        }

        @Test
        void should_not_equal_for_different_bean() {
            ObjectFactory<Bean2> objectFactory = factorySet.getObjectFactorySet().queryObjectFactory(Bean2.class);
            assertThat(sameProducer().hashCode())
                    .isNotEqualTo(new ObjectProducer<>(factorySet, objectFactory,
                            (DefaultBuilder<Bean2>) new DefaultBuilder<>(objectFactory, factorySet)
                                    .property("intValue", 1).mixIn("a bean")).hashCode());
        }
    }

    @Nested
    class Equal {

        //        @Test
        //TODO skip
        void should_get_equal_for_same_object_producer() {
            assertThat(sameProducer())
                    .isEqualTo(sameProducer());
        }

        //        @Test
        //TODO skip
        void should_not_equal_for_default_build() {
            assertThat(buildProducer(Function.identity()))
                    .isNotEqualTo(buildProducer(Function.identity()));
        }

//        @Test
//        void should_not_equal_when_has_dependency() {
//            ObjectProducer producer2 = sameProducer();
//            producer2.addDependency(null, null, null);
//
//            ObjectProducer producer1 = sameProducer();
//            assertThat(producer2).isNotEqualTo(producer1);
//        }
//
//        @Test
//        void should_not_equal_when_has_link() {
//            ObjectProducer producer2 = sameProducer();
//            producer2.link(asList(PropertyChain.createChain("a1"), PropertyChain.createChain("a2")));
//
//            ObjectProducer producer1 = sameProducer();
//            assertThat(producer2).isNotEqualTo(producer1);
//        }
//
//        @Test
//        void should_not_equal_when_has_child_which_is_not_property_value_producer() {
//            ObjectProducer<?> producer2 = sameProducer();
//            producer2.addChild("any", new UnFixedValueProducer<>(() -> null, BeanClass.create(Object.class)));
//
//            ObjectProducer producer1 = sameProducer();
//            assertThat(producer2).isNotEqualTo(producer1);
//        }

        @Test
        void should_not_equal_when_different_mixin() {
            assertThat(sameProducer())
                    .isNotEqualTo(buildProducer(builder1 -> builder1.property("intValue", 1).mixIn("another bean")));
        }

        @Test
        void should_not_equal_when_different_property() {
            assertThat(sameProducer())
                    .isNotEqualTo(buildProducer(builder1 -> builder1.property("intValue", 2000).mixIn("a bean")));
        }

        @Test
        void should_not_equal_for_different_bean() {
            ObjectFactory<Bean2> objectFactory = factorySet.getObjectFactorySet().queryObjectFactory(Bean2.class);
            assertThat(sameProducer())
                    .isNotEqualTo(new ObjectProducer<>(factorySet, objectFactory,
                            (DefaultBuilder<Bean2>) new DefaultBuilder<>(objectFactory, factorySet)
                                    .property("intValue", 1).mixIn("a bean")));
        }
    }
}