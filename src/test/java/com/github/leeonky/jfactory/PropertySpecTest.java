package com.github.leeonky.jfactory;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PropertySpecTest {
    private Spec<Object> spec = new Spec<>();
    private PropertySpec<Object> propertySpec = new PropertySpec<>(spec, PropertyChain.createChain("p1"));

    @Test
    void method_chain() {
        assertThat(propertySpec.value(() -> null)).isEqualTo(spec);
        assertThat(propertySpec.value(1)).isEqualTo(spec);
        assertThat(propertySpec.from(MySpec.class)).isEqualTo(spec);
        assertThat(propertySpec.from(MySpec.class, builder -> builder.trait("trait"))).isEqualTo(spec);
        assertThat(propertySpec.spec(MySpec.class, MySpec::someSpec)).isEqualTo(spec);
        assertThat(propertySpec.from("A", "Object")).isEqualTo(spec);
    }

    static class MySpec extends Spec<Object> {

        @Trait
        public MySpec someSpec() {
            return this;
        }
    }
}