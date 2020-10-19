package com.github.leeonky.jfactory;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

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
            assertThat(buildProducer(builder -> builder.property("intValue", 1).mixIn("a bean")).hashCode())
                    .isEqualTo(buildProducer(builder1 -> builder1.property("intValue", 1).mixIn("a bean")).hashCode());

            assertThat(buildProducer(builder -> builder.property("intValue", 1).mixIn("a bean")).hashCode())
                    .isNotEqualTo(buildProducer(builder1 -> builder1.property("intValue", 1).mixIn("another bean")).hashCode());

            assertThat(buildProducer(builder -> builder.property("intValue", 1).mixIn("a bean")).hashCode())
                    .isNotEqualTo(buildProducer(builder1 -> builder1.property("intValue", 2000).mixIn("a bean")).hashCode());

            ObjectFactory<Bean2> objectFactory = factorySet.getObjectFactorySet().queryObjectFactory(Bean2.class);
            assertThat(buildProducer(builder -> builder.property("intValue", 1).mixIn("a bean")).hashCode())
                    .isNotEqualTo(new ObjectProducer<>(factorySet, objectFactory,
                            (DefaultBuilder<Bean2>) new DefaultBuilder<>(objectFactory, factorySet)
                                    .property("intValue", 1).mixIn("a bean")).hashCode());
        }
    }

    @Nested
    class Equal {

        @Test
        void should_get_equal_for_same_object_producer() {
            assertThat(buildProducer(builder -> builder.property("intValue", 1).mixIn("a bean")))
                    .isEqualTo(buildProducer(builder1 -> builder1.property("intValue", 1).mixIn("a bean")));

            assertThat(buildProducer(builder -> builder.property("intValue", 1).mixIn("a bean")))
                    .isNotEqualTo(buildProducer(builder1 -> builder1.property("intValue", 1).mixIn("another bean")));

            assertThat(buildProducer(builder -> builder.property("intValue", 1).mixIn("a bean")))
                    .isNotEqualTo(buildProducer(builder1 -> builder1.property("intValue", 2000).mixIn("a bean")));

            ObjectFactory<Bean2> objectFactory = factorySet.getObjectFactorySet().queryObjectFactory(Bean2.class);
            assertThat(buildProducer(builder -> builder.property("intValue", 1).mixIn("a bean")))
                    .isNotEqualTo(new ObjectProducer<>(factorySet, objectFactory,
                            (DefaultBuilder<Bean2>) new DefaultBuilder<>(objectFactory, factorySet)
                                    .property("intValue", 1).mixIn("a bean")));
        }
    }
}