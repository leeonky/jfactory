package com.github.leeonky.jfactory;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PropertyExpressionTest {

    @Nested
    class Merge {

        @Test
        void single_merge_single_should_return_the_new_one() {
            SingleValuePropertyExpression<Object> expression1 = new SingleValuePropertyExpression<>(null, null, new Property<>(null, null));
            SingleValuePropertyExpression<Object> expression2 = new SingleValuePropertyExpression<>(null, null, new Property<>(null, null));

            assertThat(expression1.merge(expression2)).isEqualTo(expression2);
        }
    }
}