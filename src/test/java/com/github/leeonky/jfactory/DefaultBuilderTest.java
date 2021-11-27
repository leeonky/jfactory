package com.github.leeonky.jfactory;

import com.github.leeonky.dal.extension.assertj.DALAssert;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.leeonky.dal.extension.assertj.DALAssert.expect;
import static com.github.leeonky.jfactory.Builder.table;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    @Accessors(chain = true)
    public static class Item {
        private String value, value2;
    }

    @Getter
    @Setter
    public static class Bean {
        private String stringValue;
        private List<Item> list;
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

    @Nested
    class Table {
        private final Builder<Bean> builder = jFactory.type(Bean.class);

        @Test
        void table_with_only_header_equals_empty_property() {
            expectTable("| value |").match("[]");
        }

        @Test
        void table_1_x_1() {
            expectTable("| value |\n" +
                    "| hello |")
                    .should("value: ['hello']");
        }

        @Test
        void table_2_x_1() {
            expectTable("| value |\n" +
                    "| hello |\n" +
                    "| world |")
                    .should("value: ['hello' 'world']");
        }

        @Test
        void table_2_x_2() {
            expectTable("| value | value2 |\n" +
                    "| hello | Tom |\n" +
                    "| world | Jerry |")
                    .should("value: ['hello' 'world']")
                    .should("value2: ['Tom' 'Jerry']");
        }

        @Test
        void invalid_table_too_many_cells() {
            assertThat(assertThrows(IllegalArgumentException.class, () ->
                    builder.propertyValue("list", table("| value |\n" +
                            "| hello | world |")))).hasMessage("Invalid table at row: 0, different size of cells and headers.");
        }

        private DALAssert expectTable(String table) {
            return expect(builder.propertyValue("list", table(table)).create().getList());
        }
    }
}