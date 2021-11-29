package com.github.leeonky.jfactory.spec;

import com.github.leeonky.jfactory.JFactory;
import com.github.leeonky.jfactory.TypeReference;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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

//    @Nested
//    class CreateCollectionThroughElementType {
//
//        @Test
//        void support_create_empty_collection_from_given_collection_type() {
//            ArrayList<String> strings = jFactory.type(new TypeReference<ArrayList<String>>() {
//            }).create();
//
//            assertThat(strings).isEmpty();
//        }
//    }
}
