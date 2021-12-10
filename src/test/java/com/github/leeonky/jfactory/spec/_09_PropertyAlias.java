package com.github.leeonky.jfactory.spec;

import com.github.leeonky.jfactory.JFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Test;

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
        jFactory.aliasOf(Bean.class).alias("anotherBeanValue", "anotherBean.value");

        expect((jFactory.type(Bean.class).property("anotherBeanValue", "hello").create())).match("{anotherBean.value: 'hello'}");
    }

//    @Test
//    void alias_chain() {
//        jFactory.propertyAlias(Bean.class, alias("aliasOfAnotherBean", "anotherBean"));
//                .propertyAlias(AnotherBean.class, alias("aliasOfValue", "value"));

//        expect((jFactory.type(Bean.class).property("aliasOfAnotherBean.aliasOfValue", "hello").create()))
//                .match("{anotherBean.value: 'hello'}");
//    }

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

// TODO alias is property chain
// TODO alias chain
// TODO define alias in spec class
}
