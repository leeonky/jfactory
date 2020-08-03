package com.github.leeonky.jfactory.spec;

import com.github.leeonky.jfactory.FactorySet;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class _04_CustomizedFactory {
    private FactorySet factorySet = new FactorySet();

    @Test
    void support_define_build() {
        factorySet.factory(Bean.class).spec(instance -> {
            instance.spec().property("stringValue").value("hello");
        });

        assertThat(factorySet.type(Bean.class).create())
                .hasFieldOrPropertyWithValue("stringValue", "hello");
    }

    @Test
    void support_define_mix_in() {
        factorySet.factory(Bean.class).spec("100", instance -> {
            instance.spec().property("intValue").value(100);
        });

        assertThat(factorySet.type(Bean.class).mixIn("100").create())
                .hasFieldOrPropertyWithValue("intValue", 100);
    }

    @Test
    void raise_error_when_mixin_not_exist() {
        assertThrows(IllegalArgumentException.class, () -> factorySet.type(Bean.class).mixIn("not exist").create());
    }

    @Getter
    @Setter
    public static class Bean {
        private String content;
        private String stringValue;
        private int intValue;
    }
}
