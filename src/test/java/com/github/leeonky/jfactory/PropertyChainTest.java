package com.github.leeonky.jfactory;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.github.leeonky.jfactory.PropertyChain.propertyChain;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PropertyChainTest {

    @Test
    void is_chain_single() {
        assertTrue(propertyChain("a").isSingle());
        assertTrue(propertyChain("[0]").isSingle());
        assertFalse(propertyChain("a.b").isSingle());
    }

    @Test
    void top_level_collection() {
        assertTrue(propertyChain("a[0]").isTopLevelPropertyCollection());
        assertFalse(propertyChain("a[0].b").isTopLevelPropertyCollection());
        assertFalse(propertyChain("a").isTopLevelPropertyCollection());
        assertFalse(propertyChain("[0]").isTopLevelPropertyCollection());
        assertFalse(propertyChain("a[0][1]").isTopLevelPropertyCollection());
    }

    @Nested
    class ToString {

        @Test
        void single_chain() {
            assertToString("value");
        }

        @Test
        void root_collection() {
            assertToString("[1]");
        }

        @Test
        void full_chain() {
            assertToString("b[9]");
            assertToString("a.b[9].c");
            assertToString("b[9].c");
            assertToString("b.c[9]");
        }

        private void assertToString(String chain) {
            assertThat(propertyChain(chain).toString()).isEqualTo(chain);
        }
    }

    @Nested
    class HashAndEqual {

        @Test
        void equal_hash_with_equal_content() {
            assertHashEqual("a");
            assertHashEqual("a.b");
            assertHashEqual("[0]");
            assertHashEqual("a.b[0].c");
            assertHashEqual("b[0].c");
            assertHashEqual("b[0]");

            assertThat(propertyChain("p1").hashCode()).isNotEqualTo(propertyChain("p2").hashCode());
        }

        @Test
        void equal_with_equal_content() {
            assertContentEqual("a");
            assertContentEqual("a.b");
            assertContentEqual("[0]");
            assertContentEqual("a.b[0].c");
            assertContentEqual("b[0].c");
            assertContentEqual("b[0]");

            assertThat(propertyChain("p1")).isNotEqualTo(propertyChain("p2"));
        }

        private void assertContentEqual(String value) {
            assertThat(propertyChain(value)).isEqualTo(propertyChain(value));
        }

        private void assertHashEqual(String value) {
            assertThat(propertyChain(value).hashCode()).isEqualTo(propertyChain(value).hashCode());
        }
    }

    @Nested
    class SubShould {

        @Test
        void return_empty_when_not_matches() {
            assertThat(propertyChain("a").sub(propertyChain("b"))).isEmpty();
            assertThat(propertyChain("a.x").sub(propertyChain("a.x.y"))).isEmpty();
        }

        @Test
        void return_the_left_chain() {
            assertThat(propertyChain("a.b").sub(propertyChain("a"))).isEqualTo(of(propertyChain("b")));
            assertThat(propertyChain("a[1].b").sub(propertyChain("a[1]"))).isEqualTo(of(propertyChain("b")));
        }
    }
}