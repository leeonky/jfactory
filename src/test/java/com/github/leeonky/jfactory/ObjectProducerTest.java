package com.github.leeonky.jfactory;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObjectProducerTest {

    private FactorySet factorySet = new FactorySet();

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

    @Getter
    @Setter
    public static class Beans {
        private Bean bean1, bean2;
    }

    @Nested
    class Hashcode {

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

        @Test
        void should_get_equal_hashcode_for_same_object_producer() {
            assertThat(sameProducer().hashCode())
                    .isEqualTo(sameProducer().hashCode());
        }

        @Test
        void should_not_equal_when_has_outside_producer() {
            ObjectProducer another = sameProducer();
            another.checkChange();
            ObjectProducer producer = sameProducer();
            producer.checkChange();

            assertThat(producer.hashCode())
                    .isNotEqualTo(another.hashCode());
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

        @Test
        void should_get_equal_for_same_object_producer() {
            assertThat(sameProducer())
                    .isEqualTo(sameProducer());
        }

        @Test
        void should_not_equal_when_has_outside_producer() {
            ObjectProducer another = sameProducer();
            another.checkChange();
            ObjectProducer objectProducer = sameProducer();
            objectProducer.checkChange();

            assertThat(objectProducer)
                    .isNotEqualTo(another);
        }

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

    @Nested
    class HasOutsideSpec {

        @Nested
        class InDependency {

            @Test
            void should_not_has_outside_spec() {
                factorySet.factory(Beans.class).spec(instance -> instance.spec().property("bean1").asDefault());
                ObjectProducer<Beans> producer = (ObjectProducer<Beans>) factorySet.type(Beans.class).createProducer(null);

                producer.processDependencies();

                assertTrue(producer.getChild("bean1").get().isNotChange());
            }

            @Test
            void should_has_outside_spec() {
                factorySet.factory(Beans.class).spec(instance -> instance.spec()
                        .property("bean1").asDefault()
                        .property("bean2").asDefault()
                        .property("bean1.stringValue").dependsOn("bean2.stringValue", Function.identity())
                );
                ObjectProducer<Beans> producer = (ObjectProducer<Beans>) factorySet.type(Beans.class).createProducer(null);

                producer.processDependencies();

                assertFalse(producer.getChild("bean1").get().isNotChange());
            }
        }
    }
}