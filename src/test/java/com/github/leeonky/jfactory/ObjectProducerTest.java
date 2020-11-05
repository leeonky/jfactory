package com.github.leeonky.jfactory;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static java.util.function.Function.identity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObjectProducerTest {

    private FactorySet factorySet = new FactorySet();

    private <T> Producer buildProducer(Function<Builder<T>, Builder<T>> modifyBuilder, boolean intently, Class<T> type) {
        return modifyBuilder.apply(factorySet.type(type)).createProducer(intently);
    }

    private Producer sameProducer() {
        return producerOf(Bean.class);
    }

    private Producer producerOf(Class<?> type) {
        return buildProducer(builder -> builder.property("intValue", 1).trait("a bean"), false, type);
    }

    private Producer sameIntentlyProducer() {
        return buildProducer(builder -> builder.property("intValue", 1).trait("a bean"), true, Bean.class);
    }

    private Producer checkChangeSameProducer() {
        Producer another = sameProducer();
        another.checkChange();
        return another;
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

    @Getter
    @Setter
    public static class Beans {
        private Bean bean1, bean2;
    }

    @Nested
    class Hashcode {

        @BeforeEach
        void registerTrait() {
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

        @Test
        void should_get_equal_hashcode_for_same_object_producer() {
            assertThat(sameProducer().hashCode())
                    .isEqualTo(sameProducer().hashCode());
        }

        @Test
        void should_not_equal_when_has_outside_producer() {
            assertThat(sameProducer().hashCode())
                    .isNotEqualTo(checkChangeSameProducer().hashCode());

            assertThat(checkChangeSameProducer().hashCode())
                    .isNotEqualTo(sameProducer().hashCode());
        }

        @Test
        void should_not_equal_when_intently_create() {
            assertThat(sameProducer().hashCode())
                    .isNotEqualTo(sameIntentlyProducer().hashCode());

            assertThat(sameIntentlyProducer().hashCode())
                    .isNotEqualTo(sameProducer().hashCode());
        }

        @Test
        void should_not_equal_when_different_trait() {
            assertThat(sameProducer().hashCode())
                    .isNotEqualTo(buildProducer(builder1 -> builder1.property("intValue", 1).trait("another bean"), false, Bean.class).hashCode());
        }

        @Test
        void should_not_equal_when_different_property() {
            assertThat(sameProducer().hashCode())
                    .isNotEqualTo(buildProducer(builder1 -> builder1.property("intValue", 2000).trait("a bean"), false, Bean.class).hashCode());
        }

        @Test
        void should_not_equal_for_different_bean() {
            assertThat(producerOf(Bean.class).hashCode()).isNotEqualTo(producerOf(Bean2.class).hashCode());
        }
    }

    @Nested
    class Equal {

        @BeforeEach
        void registerTrait() {
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

        @Test
        void should_get_equal_for_same_object_producer() {
            assertThat(sameProducer())
                    .isEqualTo(sameProducer());
        }

        @Test
        void should_not_equal_when_has_outside_producer() {
            assertThat(sameProducer())
                    .isNotEqualTo(checkChangeSameProducer());

            assertThat(checkChangeSameProducer())
                    .isNotEqualTo(sameProducer());
        }

        @Test
        void should_not_equal_when_intently_create() {
            assertThat(sameIntentlyProducer())
                    .isNotEqualTo(sameProducer());

            assertThat(sameProducer())
                    .isNotEqualTo(sameIntentlyProducer());
        }

        @Test
        void should_not_equal_when_different_trait() {
            assertThat(sameProducer())
                    .isNotEqualTo(buildProducer(builder1 -> builder1.property("intValue", 1).trait("another bean"), false, Bean.class));
        }

        @Test
        void should_not_equal_when_different_property() {
            assertThat(sameProducer())
                    .isNotEqualTo(buildProducer(builder1 -> builder1.property("intValue", 2000).trait("a bean"), false, Bean.class));
        }

        @Test
        void should_not_equal_for_different_bean() {
            assertThat(producerOf(Bean.class)).isNotEqualTo(producerOf(Bean2.class));
        }
    }

    @Nested
    class HasOutsideSpec {

        @Nested
        class InDependency {

            @Test
            void should_not_has_outside_spec() {
                factorySet.factory(Beans.class).spec(instance -> instance.spec().property("bean1").asDefault());
                ObjectProducer<Beans> producer = (ObjectProducer<Beans>) factorySet.type(Beans.class).createProducer(false);

                producer.doDependencies();

                assertTrue(producer.child("bean1").get().isNotChange());
            }

            @Test
            void should_has_outside_spec() {
                factorySet.factory(Beans.class).spec(instance -> instance.spec()
                        .property("bean1").asDefault()
                        .property("bean2").asDefault()
                        .property("bean1.stringValue").dependsOn("bean2.stringValue", identity()));
                ObjectProducer<Beans> producer = (ObjectProducer<Beans>) factorySet.type(Beans.class).createProducer(false);

                producer.doDependenciesAndLinks();

                assertFalse(producer.child("bean1").get().isNotChange());
            }
        }

        //TODO for link
        //TODO for dependency and link
    }
}