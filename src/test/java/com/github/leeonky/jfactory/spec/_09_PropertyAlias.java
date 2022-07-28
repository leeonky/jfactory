package com.github.leeonky.jfactory.spec;

import com.github.leeonky.jfactory.*;
import com.github.leeonky.util.NoSuchPropertyException;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.github.leeonky.dal.Assertions.expect;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class _09_PropertyAlias {
    private final JFactory jFactory = new JFactory();

    @Test
    void support_define_and_use_property_alias_in_top_level() {
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

        expect(beans).should("value[]: ['hello']");
    }

    @Test
    void alias_of_collection() {
        jFactory.aliasOf(BeanContainer.class).alias("aliasOfBeans", "beans");

        BeanContainer beanContainer = jFactory.type(BeanContainer.class).property("aliasOfBeans[0].value", "hello").create();

        expect(beanContainer).should("beans.value[]: ['hello']");
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

        expect(beanContainer).should("beans.value[]: ['hello']");
    }

    @Test
    void intently_creation_with_alias() {
        jFactory.aliasOf(Bean.class).alias("aliasOfAnotherBean", "anotherBean");

        jFactory.type(Bean.class).property("aliasOfAnotherBean!.value", "hello").create();
        jFactory.type(Bean.class).property("aliasOfAnotherBean!.value", "hello").create();

        assertThat(jFactory.type(AnotherBean.class).queryAll()).hasSize(2);
    }

    @Test
    void uses_collection_alias_with_collection_args() {
        jFactory.aliasOf(BeanContainer.class).alias("beansValue", "beans[$].value");

        BeanContainer beanContainer = jFactory.type(BeanContainer.class).property("beansValue", asList("hello", "world"))
                .create();

        expect(beanContainer).should("beans.value[]: ['hello' 'world']");
    }

    @Test
    void empty_list_property_with_collection_alias() {
        jFactory.aliasOf(BeanContainer.class).alias("aliasOfBeans", "beans[$]");
        BeanContainer beanContainer = jFactory.type(BeanContainer.class).property("aliasOfBeans", emptyList()).create();

        expect(beanContainer).should("beans: []");
    }

    @Test
    void empty_list_property_with_collection_alias_and_sub_properties() {
        jFactory.aliasOf(BeanContainer.class).alias("aliasOfBeansValues", "beans[$].value");
        BeanContainer beanContainer = jFactory.type(BeanContainer.class).property("aliasOfBeansValues", emptyList()).create();

        expect(beanContainer).should("beans: []");
    }

    @Test
    void global_spec_should_not_has_super_spec() {
        assertThatThrownBy(() -> jFactory.register(InvalidGlobalSpec.class)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Global Spec com.github.leeonky.jfactory.spec._09_PropertyAlias$InvalidGlobalSpec should not have super Spec com.github.leeonky.jfactory.spec._09_PropertyAlias$AliasBeanSpec.");
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
        private String value, value2;
        private AnotherBean anotherBean;
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class AnotherBean {
        private String value;
    }

    @PropertyAliases(
            @PropertyAlias(alias = "aliasOfValue", property = "value")
    )
    public static class AliasBeanSpec extends Spec<Bean> {
    }

    public static class NoAliasBeanSpec extends Spec<Bean> {
    }

    public static class AliasInSuperSpec extends AliasBeanSpec {
    }

    @Global
    @PropertyAliases(
            @PropertyAlias(alias = "aliasOfValue", property = "value")
    )
    public static class GlobalSpecAlias extends Spec<Bean> {
    }

    @PropertyAliases(
            @PropertyAlias(alias = "aliasOfValue", property = "value2")
    )
    public static class OverrideSuperSpec extends AliasBeanSpec {
    }

    @Global
    public static class InvalidGlobalSpec extends AliasBeanSpec {
    }

    @Nested
    class SpecAlias {

        @Test
        void support_define_alias_on_spec() {
            jFactory.register(AliasBeanSpec.class);

            expect(jFactory.spec(AliasBeanSpec.class).property("aliasOfValue", "hello").create()).should("value: 'hello'");
        }

        @Test
        void alias_on_spec_is_not_global() {
            jFactory.register(AliasBeanSpec.class);

            assertThatThrownBy(() -> jFactory.type(Bean.class).property("aliasOfValue", "hello").create())
                    .isInstanceOf(NoSuchPropertyException.class);
        }

        @Test
        void should_fall_back_to_type_alias_when_no_spec_alias() {
            jFactory.aliasOf(Bean.class).alias("typeAliasOfValue", "value");

            jFactory.register(NoAliasBeanSpec.class);

            expect(jFactory.spec(NoAliasBeanSpec.class).property("typeAliasOfValue", "hello").create()).should("value: 'hello'");
        }

        @Test
        void should_fall_back_to_type_alias_when_no_matches_spec_on_alias() {
            jFactory.aliasOf(Bean.class).alias("typeAliasOfValue", "value");

            jFactory.register(AliasBeanSpec.class);

            expect(jFactory.spec(AliasBeanSpec.class).property("typeAliasOfValue", "hello").create()).should("value: 'hello'");
        }

        @Test
        void alias_spec_in_super_alias() {
            jFactory.register(AliasInSuperSpec.class);

            expect(jFactory.spec(AliasInSuperSpec.class).property("aliasOfValue", "hello").create()).should("value: 'hello'");
        }

        @Test
        void alias_on_global_spec_should_type_global_alias() {
            jFactory.register(GlobalSpecAlias.class);

            expect(jFactory.type(Bean.class).property("aliasOfValue", "hello").create()).should("value: 'hello'");
        }

        @Test
        void fallback_to_alias_on_global_spec() {
            jFactory.register(GlobalSpecAlias.class);

            jFactory.register(NoAliasBeanSpec.class);

            expect(jFactory.spec(NoAliasBeanSpec.class).property("aliasOfValue", "hello").create()).should("value: 'hello'");
        }

        @Test
        void sub_class_alias_should_override_super_class_alias() {
            jFactory.register(AliasBeanSpec.class);
            jFactory.register(OverrideSuperSpec.class);

            expect(jFactory.spec(OverrideSuperSpec.class).property("aliasOfValue", "hello").create()).should("value2: 'hello'");
        }
    }
}
