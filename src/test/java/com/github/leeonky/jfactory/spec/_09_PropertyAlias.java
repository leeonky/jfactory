package com.github.leeonky.jfactory.spec;

import com.github.leeonky.jfactory.JFactory;
import com.github.leeonky.jfactory.TypeReference;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.github.leeonky.dal.extension.assertj.DALAssert.expect;

public class _09_PropertyAlias {
    private final JFactory jFactory = new JFactory();

    @Test
    void support_define_and_use_property_alias_in_top_level() {
        jFactory.aliasOf(Bean.class).alias("aliasOfValue", "value");

        jFactory.aliasOf(Bean.class).alias("aliasOfValue", "value");

        expect((jFactory.type(Bean.class).property("aliasOfValue", "hello").create())).match("{value: 'hello'}");
    }

    @Test
    void should_use_as_property_when_no_alias() {
        jFactory.aliasOf(Bean.class).alias("aliasOfValue", "noMatchProperty");

        expect((jFactory.type(Bean.class).property("value", "hello").create())).match("{value: 'hello'}");
    }

    @Test
    void alias_of_property_chain() {
        jFactory.aliasOf(BeanContainer.class).alias("beanAnotherBeanValue", "bean.anotherBean.value");

        expect((jFactory.type(BeanContainer.class).property("beanAnotherBeanValue", "hello").create())).match("{bean.anotherBean.value: 'hello'}");
    }

    @Test
    void alias_chain() {
        jFactory.aliasOf(Bean.class).alias("aliasOfAnotherBean", "anotherBean");
        jFactory.aliasOf(AnotherBean.class).alias("aliasOfValue", "value");

        Bean hello = jFactory.type(Bean.class).property("aliasOfAnotherBean.aliasOfValue", "hello").create();
        expect(hello)
                .match("{anotherBean.value: 'hello'}");
    }

    @Test
    void alias_in_collection() {
        jFactory.aliasOf(Bean.class).alias("aliasOfValue", "value");

        List<Bean> beans = jFactory.type(new TypeReference<ArrayList<Bean>>() {
        }).property("[0].aliasOfValue", "hello").create();

        expect(beans).should("value: ['hello']");
    }

    @Test
    void alias_of_collection() {
        jFactory.aliasOf(BeanContainer.class).alias("aliasOfBeans", "beans");

        BeanContainer beanContainer = jFactory.type(BeanContainer.class).property("aliasOfBeans[0].value", "hello").create();

        expect(beanContainer).should("beans.value: ['hello']");
    }

    @Test
    void recursive_alias() {
        jFactory.aliasOf(Bean.class).alias("aliasOfAnotherBeanValue", "anotherBeanValue");
        jFactory.aliasOf(Bean.class).alias("anotherBeanValue", "anotherBean.value");

        expect((jFactory.type(Bean.class).property("aliasOfAnotherBeanValue", "hello").create())).match("{anotherBean.value: 'hello'}");
    }

    @Test
    void index_arg_in_alias() {
        jFactory.aliasOf(BeanContainer.class).alias("beansValue", "beans[$].value");

        BeanContainer beanContainer = jFactory.type(BeanContainer.class).property("beansValue[0]", "hello").create();

        expect(beanContainer).should("beans.value: ['hello']");
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class BeanContainer {
        private Bean bean;
        private List<Bean> beans;
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Bean {
        private String value;
        private AnotherBean anotherBean;
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class AnotherBean {
        private String value;
    }

// TODO alias with index parameter
// TODO alias with !
// TODO define alias in spec class
}
