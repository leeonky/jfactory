package com.github.leeonky.jfactory;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultBuilderTest {
    private JFactory jFactory = new JFactory();

    @BeforeEach
    void registerTrait() {
        jFactory.factory(Bean.class).spec("trait1", instance -> {
        });

        jFactory.factory(Bean2.class).spec("trait1", instance -> {
        }).spec("trait2", instance -> {
        });
    }

    private Builder<?> builder(Class<?> type, String value, String trait) {
        return jFactory.type(type).property("defaultString1", value).traits(trait);
    }

    @Getter
    @Setter
    public static class Bean {
        private String stringValue;
    }

    @Getter
    @Setter
    public static class Bean2 {
        private String stringValue;
    }

    @Nested
    class Hashcode {

        @Test
        void should_get_equal_hashcode_for_same_object_producer() {
            assertThat(builder(Bean.class, "string", "trait1").hashCode())
                    .isEqualTo(builder(Bean.class, "string", "trait1").hashCode());
        }

        @Test
        void should_not_equal_for_different_type() {
            assertThat(builder(Bean.class, "string", "trait1").hashCode())
                    .isNotEqualTo(builder(Bean2.class, "string", "trait1").hashCode());
        }

        @Test
        void should_not_equal_for_different_property() {
            assertThat(builder(Bean.class, "string", "trait1").hashCode())
                    .isNotEqualTo(builder(Bean.class, "string2", "trait1").hashCode());
        }

        @Test
        void should_not_equal_for_different_trait() {
            assertThat(builder(Bean.class, "string", "trait1").hashCode())
                    .isNotEqualTo(builder(Bean.class, "string", "trait2").hashCode());
        }
    }

    @Nested
    class Equal {

        @Test
        void should_get_equal_hashcode_for_same_object_producer() {
            assertThat(builder(Bean.class, "string", "trait1"))
                    .isEqualTo(builder(Bean.class, "string", "trait1"));
        }

        @Test
        void should_not_equal_for_different_type() {
            assertThat(builder(Bean.class, "string", "trait1"))
                    .isNotEqualTo(builder(Bean2.class, "string", "trait1"));
        }

        @Test
        void should_not_equal_for_different_property() {
            assertThat(builder(Bean.class, "string", "trait1"))
                    .isNotEqualTo(builder(Bean.class, "string2", "trait1"));
        }

        @Test
        void should_not_equal_for_different_trait() {
            assertThat(builder(Bean.class, "string", "trait1"))
                    .isNotEqualTo(builder(Bean.class, "string", "trait2"));
        }
    }
}