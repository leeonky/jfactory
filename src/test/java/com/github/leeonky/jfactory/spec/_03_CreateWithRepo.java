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
    void should_save_in_repo_after_creation() {
        Builder<Bean> builder = factorySet.type(Bean.class).property("stringValue", "hello").property("intValue", "100");

        Bean created = builder.create();

        assertThat(builder.query()).isEqualTo(created);

        factorySet.clearRepo();

        assertThat(builder.query()).isNull();
    }

    @Test
    void should_save_repo_after_nested_creation() {
        Bean bean = factorySet.type(Beans.class).property("bean.stringValue", "hello").create().getBean();

        assertThat(factorySet.type(Bean.class).property("stringValue", "hello").query()).isEqualTo(bean);
    }

    @Test
    void should_use_queried_object_of_given_criteria_as_property_value_in_nested_specified_property() {
        Bean helloBean = factorySet.type(Bean.class).property("stringValue", "hello").create();

        assertThat(factorySet.type(Beans.class).property("bean.stringValue", "hello").create())
                .hasFieldOrPropertyWithValue("bean", helloBean);
    }

    @Test
    void should_create_nested_with_property_and_value_when_query_return_empty() {
        Beans notMatched = factorySet.type(Beans.class).create();

        Beans beans = factorySet.type(BeansWrapper.class).property("beans.bean.stringValue", "hello").create().getBeans();
        assertThat(beans).isNotEqualTo(notMatched);
        assertThat(beans.getBean())
                .hasFieldOrPropertyWithValue("stringValue", "hello");
    }

    @Getter
    @Setter
    public static class Bean {
        private String stringValue;
        private int intValue;
    }

    @Getter
    @Setter
    public static class Beans {
        private Bean bean;
    }

    @Getter
    @Setter
    public static class BeansWrapper {
        private Beans beans;
    }
}
