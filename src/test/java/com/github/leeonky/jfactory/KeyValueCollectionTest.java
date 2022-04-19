package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class KeyValueCollectionTest {

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Bean {
        public String key1, key2;
    }

    @Nested
    class Merge {

        @Test
        void merge_two_key_value_collection() {
            KeyValueCollection collection1 = new KeyValueCollection() {{
                append("key1", "value1");
            }};
            KeyValueCollection collection2 = new KeyValueCollection() {{
                append("key2", "value2");
            }};

            collection1.mergeFrom(collection2);

            ArrayList<Expression<Bean>> expressions = new ArrayList<>(collection1.expressions(BeanClass.create(Bean.class)));

            assertThat(expressions.get(0).isMatch(new Bean().setKey1("value1"))).isTrue();
            assertThat(expressions.get(1).isMatch(new Bean().setKey2("value2"))).isTrue();
        }
    }
}