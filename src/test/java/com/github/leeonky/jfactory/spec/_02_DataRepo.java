package com.github.leeonky.jfactory.spec;

import com.github.leeonky.jfactory.Builder;
import com.github.leeonky.jfactory.DataRepository;
import com.github.leeonky.jfactory.JFactory;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class _02_DataRepo {
    private JFactory JFactory = new JFactory();

    @Test
    void should_save_in_repo_after_creation_for_simple_condition() {
        Builder<Bean> builder = JFactory.type(Bean.class).property("stringValue", "hello");

        Bean created = builder.create();

        assertThat(builder.query()).isEqualTo(created);
    }

    @Test
    void should_return_null_if_query_condition_not_matched_for_simple_condition() {
        JFactory.type(Bean.class).property("stringValue", "hello").create();

        assertThat(JFactory.type(Bean.class).property("stringValue", "not hello").query()).isNull();
    }

    @Test
    void should_return_null_when_no_data_in_repo_for_simple_condition() {
        Builder<Bean> builder = JFactory.type(Bean.class).property("stringValue", "hello");

        builder.create();
        JFactory.getDataRepository().clear();

        assertThat(builder.query()).isNull();
    }

    @Test
    void should_save_in_repo_after_creation_for_complex_condition() {
        Builder<Beans> builder = JFactory.type(Beans.class).property("bean.stringValue", "hello");

        Beans created = builder.create();

        assertThat(builder.query()).isEqualTo(created);
    }

    @Test
    void should_return_null_if_query_condition_not_matched_for_complex_condition() {
        JFactory.type(Beans.class).property("bean.stringValue", "hello").create();

        assertThat(JFactory.type(Beans.class).property("bean.stringValue", "not hello").query()).isNull();
    }

    @Test
    void should_return_null_when_no_data_in_repo_for_complex_condition() {
        Builder<Beans> builder = JFactory.type(Beans.class).property("bean.stringValue", "hello");

        builder.create();
        JFactory.getDataRepository().clear();

        assertThat(builder.query()).isNull();
    }

    @Test
    void should_save_repo_after_nested_creation() {
        Bean bean = JFactory.type(Beans.class).property("bean.stringValue", "hello").create().getBean();

        assertThat(JFactory.type(Bean.class).property("stringValue", "hello").query()).isEqualTo(bean);
    }

    @Test
    void should_use_queried_object_of_given_criteria_as_property_value_in_nested_specified_property() {
        Bean helloBean = JFactory.type(Bean.class).property("stringValue", "hello").create();

        assertThat(JFactory.type(Beans.class).property("bean.stringValue", "hello").create())
                .hasFieldOrPropertyWithValue("bean", helloBean);
    }

    @Test
    void should_create_nested_with_property_and_value_when_query_return_empty() {
        Beans beans = JFactory.type(BeansWrapper.class).property("beans.bean.stringValue", "hello").create().getBeans();

        assertThat(JFactory.type(Beans.class).property("bean.stringValue", "hello").query()).isEqualTo(beans);
        assertThat(beans.getBean())
                .hasFieldOrPropertyWithValue("stringValue", "hello");
    }

    @Test
    void support_use_customer_data_repo() {
        List<Object> saved = new ArrayList<>();

        JFactory = new JFactory(new DataRepository() {
            @Override
            public void save(Object object) {
                saved.add(object);
            }

            @Override
            public <T> Collection<T> queryAll(Class<T> type) {
                return null;
            }

            @Override
            public void clear() {
            }
        });

        Bean bean = JFactory.create(Bean.class);

        assertThat(saved).containsExactly(bean);
    }

    @Test
    void should_save_object_in_right_sequence() {
        List<Object> saved = new ArrayList<>();

        JFactory = new JFactory(new DataRepository() {
            @Override
            public void save(Object object) {
                saved.add(object);
            }

            @Override
            public <T> Collection<T> queryAll(Class<T> type) {
                return Collections.emptyList();
            }

            @Override
            public void clear() {
            }
        });

        BeansWrapper beansWrapper = JFactory.type(BeansWrapper.class).property("beans.bean.stringValue", "hello").create();

        assertThat(saved).containsExactly(beansWrapper.beans.bean, beansWrapper.beans, beansWrapper);
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
