package com.github.leeonky.jfactory.spec;

import com.github.leeonky.jfactory.JFactory;
import com.github.leeonky.jfactory.TypeReference;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

public class _08_TypeReference {
    private final JFactory jFactory = new JFactory();

    @Nested
    class JavaBean {
        @Test
        void support_create_java_class_by_type_reference() {
            String string = jFactory.type(new TypeReference<String>() {
            }).create();

            assertThat(string).isEmpty();
        }
    }

    @Nested
    class Collection {

        @Test
        void support_create_empty_array_list_from_given_collection_type() {
            ArrayList<String> strings = jFactory.type(new TypeReference<ArrayList<String>>() {
            }).create();

            assertThat(strings).isEmpty();
        }

        @Test
        void support_create_empty_hash_set_from_given_collection_type() {
            HashSet<String> strings = jFactory.type(new TypeReference<HashSet<String>>() {
            }).create();

            assertThat(strings).isEmpty();
        }

        //        @Test
        void support_create_empty_array() {
            String[] strings = jFactory.type(String[].class).create();

            assertThat(strings).isEmpty();
        }
    }
}
