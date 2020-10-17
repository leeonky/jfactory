package com.github.leeonky.jfactory;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.github.leeonky.jfactory.PropertyChain.createChain;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PropertyChainTest {

    @Test
    void is_chain_single() {
        assertTrue(createChain("a").isSingle());
        assertTrue(createChain("[0]").isSingle());
        assertFalse(createChain("a.b").isSingle());
    }

    @Test
    void top_level_collection() {
        assertTrue(createChain("a[0]").isTopLevelPropertyCollection());
        assertFalse(createChain("a[0].b").isTopLevelPropertyCollection());
        assertFalse(createChain("a").isTopLevelPropertyCollection());
        assertFalse(createChain("[0]").isTopLevelPropertyCollection());
        assertFalse(createChain("a[0][1]").isTopLevelPropertyCollection());
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
            assertThat(createChain(chain).toString()).isEqualTo(chain);
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

            assertThat(createChain("p1").hashCode()).isNotEqualTo(createChain("p2").hashCode());
        }

        @Test
        void equal_with_equal_content() {
            assertContentEqual("a");
            assertContentEqual("a.b");
            assertContentEqual("[0]");
            assertContentEqual("a.b[0].c");
            assertContentEqual("b[0].c");
            assertContentEqual("b[0]");

            assertThat(createChain("p1")).isNotEqualTo(createChain("p2"));
        }

        private void assertContentEqual(String value) {
            assertThat(createChain(value)).isEqualTo(createChain(value));
        }

        private void assertHashEqual(String value) {
            assertThat(createChain(value).hashCode()).isEqualTo(createChain(value).hashCode());
        }
    }
}