package com.github.leeonky.jfactory.spec;

import com.github.leeonky.jfactory.JFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Test;

import static com.github.leeonky.dal.extension.assertj.DALAssert.expect;

public class _09_PropertyAlias {
    private JFactory jFactory = new JFactory();

    @Test
    void support_define_and_use_property_alias_in_top_level() {
        jFactory.propertyAlias(Bean.class, JFactory.alias("aliasOfValue", "value"));

        expect((jFactory.type(Bean.class).property("aliasOfValue", "hello").create())).match("{value: 'hello'}");
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Bean {
        private String value;
    }
}
