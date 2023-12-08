package com.github.leeonky.jfactory;

import lombok.Getter;
import lombok.Setter;
import org.assertj.core.api.AbstractAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Objects;

import static com.github.leeonky.jfactory.PropertyChain.propertyChain;

class ProducerTest {
    private final JFactory JFactory = new JFactory();
    private ObjectProducer<Bean> beanProducer;

    @BeforeEach
    void setupCollectionProducer() {
        JFactory.factory(Bean.class).spec(instance -> instance.spec()
                .property("array1[0]").byFactory()
                .property("array2[0]").byFactory()
                .property("dependency1").dependsOn("defaultString1", o -> o)
                .property("dependency2").dependsOn("defaultString2", o -> o)
                .property("unfixed1").value("")
                .property("unfixed2").value("")
                .link("link1", "link2")
        );
        beanProducer = (ObjectProducer<Bean>) JFactory.type(Bean.class)
                .property("inputString1", "a string")
                .property("inputString2", "a string")
                .property("subObj1.defaultString1", "1")
                .property("subObj2.defaultString1", "2")
                .createProducer();
        beanProducer.doDependenciesAndLinks();
    }

    @SuppressWarnings("unchecked")
    private void assertChange(String message, String from, String to, String result) {
        Producer producer = beanProducer.descendant(propertyChain(from));
        new ProducerAssert(producer.changeTo(beanProducer.descendant(propertyChain(to)))).
                isSameProducer(beanProducer.descendant(propertyChain(result)));
    }

    static class ProducerAssert extends AbstractAssert<ProducerAssert, Producer<?>> {
        public ProducerAssert(Producer producer) {
            super(producer, ProducerAssert.class);
        }

        public ProducerAssert isSameProducer(Producer<?> another) {
            isNotNull();
            if (another instanceof ReadOnlyProducer && actual instanceof ReadOnlyProducer) {
                try {
                    Field reader = ReadOnlyProducer.class.getDeclaredField("reader");
                    reader.setAccessible(true);
                    Field parent = ReadOnlyProducer.class.getDeclaredField("parent");
                    parent.setAccessible(true);
                    if (Objects.equals(reader.get(another), reader.get(actual))
                            && Objects.equals(parent.get(another), parent.get(actual)))
                        return this;
                    failWithMessage("\nExpect: %s\nActual: %s", another, actual);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
            if (!Objects.equals(actual, another))
                failWithMessage("\nExpect: %s\nActual: %s", another, actual);
            return this;
        }
    }

    @Getter
    @Setter
    public static class Bean {
        private String[] array1, array2;
        private String defaultString1, defaultString2;
        private String inputString1, inputString2;
        private Bean readonly1, readonly2;
        private String dependency1, dependency2;
        private String link1, link2;
        private Bean subObj1, subObj2;
        private String unfixed1, unfixed2;
    }

    public static class AnotherBean extends Spec<Bean> {

        @Override
        public void main() {
            property("defaultString1").value("str1");
        }
    }

    public static class ABean1 extends Spec<Bean> {

        @Override
        public void main() {
            property("readonly1").is(AnotherBean.class);
        }
    }

    public static class ABean2 extends Spec<Bean> {

        @Override
        public void main() {
            property("readonly1").is(AnotherBean.class);
        }
    }

    @Nested
    class Change {

        @Test
        void to_collection_should() {
            assertChange("override from collection", "array2", "array1", "array1");
            assertChange("override from default value", "defaultString2", "array1", "array1");
            assertChange("default override from dependency value", "dependency2", "array1", "array1");
            assertChange("not override from input", "inputString2", "array1", "inputString2");
            assertChange("default override from link value", "link2", "array1", "array1");
            assertChange("override from object", "subObj2", "array1", "array1");
            assertChange("default override from readonly", "readonly2", "array1", "array1");
            assertChange("override from unfixed", "unfixed2", "array1", "array1");
        }

        @Test
        void to_default_value_should() {
            assertChange("not override from collection", "array2", "defaultString1", "array2");
            assertChange("override from default value", "defaultString2", "defaultString1", "defaultString1");
            assertChange("not override from dependency value", "dependency2", "defaultString1", "dependency2");
            assertChange("not override from input", "inputString2", "defaultString1", "inputString2");
            assertChange("not override from link value", "link2", "defaultString1", "link2");
            assertChange("not override from object", "subObj2", "defaultString1", "subObj2");
            assertChange("not override from readonly", "readonly2", "defaultString1", "readonly2");
            assertChange("not override from unfixed", "unfixed2", "defaultString1", "unfixed2");
        }

        @Test
        void to_dependency_value_should() {
            assertChange("override from collection", "array2", "dependency1", "dependency1");
            assertChange("override from default value", "defaultString2", "dependency1", "dependency1");
            assertChange("override from dependency value", "dependency2", "dependency1", "dependency1");
            assertChange("not override from input", "inputString2", "dependency1", "inputString2");
            assertChange("default override from link value", "link2", "dependency1", "dependency1");
//            TODO incorrect preparing data for test case
//            assertChange("override from object", "subObj2", "dependency1", "dependency1");
            assertChange("default override from readonly", "readonly2", "dependency1", "dependency1");
            assertChange("override from unfixed", "unfixed2", "dependency1", "dependency1");
        }

        @Test
        void to_fixed_value_should() {
            assertChange("override from collection", "array2", "inputString1", "inputString1");
            assertChange("override from default value", "defaultString2", "inputString1", "inputString1");
            assertChange("override from dependency value", "dependency2", "inputString1", "inputString1");
            assertChange("override from input", "inputString2", "inputString1", "inputString1");
            assertChange("override from link value", "link2", "inputString1", "inputString1");
            assertChange("override from object", "subObj2", "inputString1", "inputString1");
            assertChange("default override from readonly", "readonly2", "inputString1", "inputString1");
            assertChange("override from unfixed", "unfixed2", "inputString1", "inputString1");
        }

        @Test
        void to_link_should() {
            assertChange("override from collection", "array2", "link1", "link1");
            assertChange("override from default value", "defaultString2", "link1", "link1");
            assertChange("override from dependency value", "dependency2", "link1", "link1");
            assertChange("not override from input", "inputString2", "link1", "inputString2");
            assertChange("override from link value", "link2", "link1", "link1");
//            TODO incorrect preparing data for test case
//            assertChange("override from object", "subObj2", "link1", "link1");
            assertChange("default override from readonly", "readonly2", "link1", "link1");
            assertChange("override from unfixed", "unfixed2", "link1", "link1");
        }

        @Test
        void to_object_should() {
            assertChange("default override from collection", "array2", "subObj1", "subObj1");
            assertChange("override from default value", "defaultString2", "subObj1", "subObj1");
            assertChange("default override from dependency value", "dependency2", "subObj1", "subObj1");
            assertChange("not override from input", "inputString2", "subObj1", "inputString2");
            assertChange("default override from link value", "link2", "subObj1", "subObj1");

            assertChange("default override from readonly", "readonly2", "subObj1", "subObj1");
            assertChange("override from unfixed", "unfixed2", "subObj1", "subObj1");
        }

        @Test
        void to_readonly_should() {
            assertChange("default override from collection", "array2", "readonly1", "readonly1");
            assertChange("default override from default value", "defaultString2", "readonly1", "readonly1");
            assertChange("default override from dependency value", "dependency2", "readonly1", "readonly1");
            assertChange("not override from input", "inputString2", "link1", "inputString2");
            assertChange("default override from link value", "link2", "readonly1", "readonly1");
            assertChange("default override from object", "subObj2", "readonly1", "readonly1");
            assertChange("default override from readonly", "readonly2", "readonly1", "readonly1");
            assertChange("default override from unfixed", "unfixed2", "readonly1", "readonly1");
        }

        @Test
        void to_unfixed_should() {
            assertChange("override from collection", "array2", "unfixed1", "unfixed1");
            assertChange("override from default value", "defaultString2", "unfixed1", "unfixed1");
            assertChange("default override from dependency value", "dependency2", "unfixed1", "unfixed1");
            assertChange("not override from input", "inputString2", "unfixed1", "inputString2");
            assertChange("default override from link value", "link2", "unfixed1", "unfixed1");
            assertChange("override from object", "subObj2", "unfixed1", "unfixed1");
            assertChange("default override from readonly", "readonly2", "unfixed1", "unfixed1");
            assertChange("override from unfixed", "unfixed2", "unfixed1", "unfixed1");
        }
    }
}