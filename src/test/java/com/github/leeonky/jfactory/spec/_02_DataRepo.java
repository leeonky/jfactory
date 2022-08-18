package com.github.leeonky.jfactory.spec;

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
    private JFactory jFactory = new JFactory();

    @Test
    void support_use_customer_data_repo() {
        List<Object> saved = new ArrayList<>();

        jFactory = new JFactory(new DataRepository() {
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

        Bean bean = jFactory.create(Bean.class);

        assertThat(saved).containsExactly(bean);
    }

    @Test
    void should_save_object_in_right_sequence() {
        List<Object> saved = new ArrayList<>();

        jFactory = new JFactory(new DataRepository() {
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

        BeansWrapper beansWrapper = jFactory.type(BeansWrapper.class).property("beans.bean.stringValue", "hello").create();

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
        private List<Bean> beans;
    }

    @Getter
    @Setter
    public static class BeansWrapper {
        private Beans beans;
    }
}
