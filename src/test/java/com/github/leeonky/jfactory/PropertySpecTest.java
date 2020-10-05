package com.github.leeonky.jfactory;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PropertySpecTest {
    private Spec<Object> spec = new Spec<>();
    private PropertySpec<Object> propertySpec = new PropertySpec<>(spec, new PropertyChain("p1"));

    @Test
    void method_chain() {
        assertThat(propertySpec.value(() -> null)).isEqualTo(spec);
        assertThat(propertySpec.value(1)).isEqualTo(spec);
        assertThat(propertySpec.spec(MySpec.class)).isEqualTo(spec);
        assertThat(propertySpec.spec(MySpec.class, builder -> builder.mixIn("mix-in"))).isEqualTo(spec);
        assertThat(propertySpec.spec(spec)).isEqualTo(spec);
        assertThat(propertySpec.spec("A", "Object")).isEqualTo(spec);
    }

    static class MySpec extends Spec<Object> {
    }
}