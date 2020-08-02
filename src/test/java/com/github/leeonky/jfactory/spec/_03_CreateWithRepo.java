package com.github.leeonky.jfactory.spec;

import com.github.leeonky.jfactory.Builder;
import com.github.leeonky.jfactory.FactorySet;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class _03_CreateWithRepo {
    private FactorySet factorySet = new FactorySet();

    @Test
    void save_in_repo_after_create() {
        Builder<Bean> builder = factorySet.type(Bean.class).property("stringValue", "hello").property("intValue", "100");

        Bean created = builder.create();

        assertThat(builder.query()).isEqualTo(created);

        factorySet.clearRepo();

        assertThat(builder.query()).isNull();
    }

    @Getter
    @Setter
    public static class Bean {
        private String stringValue;
        private int intValue;
    }
}
