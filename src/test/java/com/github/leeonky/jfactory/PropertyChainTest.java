package com.github.leeonky.jfactory;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PropertyChainTest {

    @Test
    void is_chain_single() {
        assertTrue(new PropertyChain("a").isSingle());
        assertTrue(new PropertyChain("[0]").isSingle());
        assertFalse(new PropertyChain("a.b").isSingle());
    }

    @Test
    void top_level_collection() {
        assertTrue(new PropertyChain("a[0]").isTopLevelPropertyCollection());
        assertFalse(new PropertyChain("a[0].b").isTopLevelPropertyCollection());
        assertFalse(new PropertyChain("a").isTopLevelPropertyCollection());
        assertFalse(new PropertyChain("[0]").isTopLevelPropertyCollection());
        assertFalse(new PropertyChain("a[0][1]").isTopLevelPropertyCollection());
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
            assertThat(new PropertyChain(chain).toString()).isEqualTo(chain);
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

            assertThat(new PropertyChain("p1").hashCode()).isNotEqualTo(new PropertyChain("p2").hashCode());
        }

        @Test
        void equal_with_equal_content() {
            assertContentEqual("a");
            assertContentEqual("a.b");
            assertContentEqual("[0]");
            assertContentEqual("a.b[0].c");
            assertContentEqual("b[0].c");
            assertContentEqual("b[0]");

            assertThat(new PropertyChain("p1")).isNotEqualTo(new PropertyChain("p2"));
        }

        private void assertContentEqual(String value) {
            assertThat(new PropertyChain(value)).isEqualTo(new PropertyChain(value));
        }

        private void assertHashEqual(String value) {
            assertThat(new PropertyChain(value).hashCode()).isEqualTo(new PropertyChain(value).hashCode());
        }
    }
}