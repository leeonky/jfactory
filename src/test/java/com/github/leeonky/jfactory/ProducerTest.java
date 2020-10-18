package com.github.leeonky.jfactory;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProducerTest {
    private FactorySet factorySet = new FactorySet();
    private int intValue = 1;

    @Test
    void should_cache_produce_value() {
        factorySet.factory(Bean.class).spec(instance -> instance.spec()
                .property("stringValue").value(() -> String.valueOf(intValue++))
        );
        factorySet.factory(Beans.class)
                .spec(instance -> instance.spec()
                        .property("bean1").asDefault()
                        .property("bean2").asDefault()
                        .property("bean2.stringValue").dependsOn("bean1.stringValue", v -> v)
                        .property("bean3").asDefault()
                        .property("bean3.stringValue").dependsOn("bean1.stringValue", v -> v)
                );

        Beans beans = factorySet.create(Beans.class);

        assertThat(beans.bean2.stringValue)
                .isEqualTo(beans.bean3.stringValue)
                .isEqualTo(beans.bean1.stringValue)
                .isEqualTo("1");
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Beans {
        private Bean bean1, bean2, bean3;
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Bean {
        private String stringValue;
    }
}