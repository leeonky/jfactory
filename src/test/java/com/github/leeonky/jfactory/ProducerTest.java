package com.github.leeonky.jfactory;

import lombok.Getter;
import lombok.Setter;
import org.assertj.core.api.AbstractAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Objects;

import static com.github.leeonky.jfactory.PropertyChain.createChain;

class ProducerTest {
    private final FactorySet factorySet = new FactorySet();
    private ObjectProducer<Bean> beanProducer;

    @BeforeEach
    void setupCollectionProducer() {
        factorySet.factory(Bean.class).spec(instance -> instance.spec()
                .property("array1[0]").asDefault()
                .property("array2[0]").asDefault()
                .property("dependency1").dependsOn("defaultString1", o -> o)
                .property("dependency2").dependsOn("defaultString2", o -> o)
                .property("unfixed1").value("")
                .property("unfixed2").value("")
                .link("link1", "link2")
        );
        beanProducer = (ObjectProducer<Bean>) factorySet.type(Bean.class)
                .property("inputString1", "a string")
                .property("subObj1.defaultString1", "1")
                .property("subObj2.defaultString1", "2")
                .createProducer(true);
        beanProducer.doDependenciesAndLinks();
    }

    @SuppressWarnings("unchecked")
    private void assertChange(String message, String from, String to, String result) {
        Producer producer = beanProducer.child(createChain(from));
        new ProducerAssert(producer.changeTo(beanProducer.child(createChain(to)))).
                isSameProducer(beanProducer.child(createChain(result)));
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
        private String inputString1;
        private Bean readonly1;
        private String dependency1, dependency2;
        private String link1, link2;
        private Bean subObj1, subObj2;
        private String unfixed1, unfixed2;
    }

    @Nested
    class Change {

        @Test
        void to_collection_should() {
            assertChange("override from collection", "array2", "array1", "array1");
            assertChange("override from default value", "defaultString1", "array1", "array1");
            assertChange("default override from default value", "dependency1", "array1", "array1");
            assertChange("not override from input", "inputString1", "array1", "inputString1");
            assertChange("default override from link value", "link1", "array1", "array1");
            assertChange("override from object", "subObj1", "array1", "array1");
            assertChange("default override from readonly", "readonly1", "array1", "array1");
            assertChange("override from unfixed", "unfixed1", "array1", "array1");
        }

        @Test
        void to_default_value_should() {
            assertChange("not override from collection", "array1", "defaultString1", "array1");
            assertChange("override from default value", "defaultString2", "defaultString1", "defaultString1");
            assertChange("not override from default value", "dependency1", "defaultString1", "dependency1");
            assertChange("not override from input", "inputString1", "defaultString1", "inputString1");
            assertChange("not override from link value", "link1", "defaultString1", "link1");
            assertChange("not override from object", "subObj1", "defaultString1", "subObj1");
            assertChange("not override from readonly", "readonly1", "defaultString1", "readonly1");
            assertChange("not override from unfixed", "unfixed1", "defaultString1", "unfixed1");
        }

        //        @Nested
        class ToDependency {

            @Nested
            class FromCollection {
            }

            @Nested
            class FromDefaultValue {

                @Test
                void should_not_override() {
                    assertChange("", "dependency1", "defaultString1", "dependency1");
                }
            }

            @Nested
            class FromDependency {

                @Test
                void should_override() {
                    assertChange("", "dependency1", "dependency2", "dependency2");
                }
            }

            @Nested
            class FromFixedValue {

                @Test
                void should_override() {
                    assertChange("", "dependency1", "inputString1", "inputString1");
                }
            }

            @Nested
            class FromLink {

                @Test
                void should_override() {
                    assertChange("", "dependency1", "link1", "link1");
                }
            }

            @Nested
            class FromObject {
            }

            @Nested
            class FromReadOnly {
            }

            @Nested
            class FromUnFixedValue {
            }
        }

        @Nested
        class ToFixedValue {

            @Nested
            class FromCollection {
            }

            @Nested
            class FromDefaultValue {
            }

            @Nested
            class FromDependency {
            }

            @Nested
            class FromFixedValue {
            }

            @Nested
            class FromLink {
            }

            @Nested
            class FromObject {
            }

            @Nested
            class FromReadOnly {
            }

            @Nested
            class FromUnFixedValue {
            }
        }

        @Nested
        class ToLink {

            @Nested
            class FromCollection {
            }

            @Nested
            class FromDefaultValue {
            }

            @Nested
            class FromDependency {
            }

            @Nested
            class FromFixedValue {
            }

            @Nested
            class FromLink {
            }

            @Nested
            class FromObject {
            }

            @Nested
            class FromReadOnly {
            }

            @Nested
            class FromUnFixedValue {
            }
        }

        @Nested
        class ToObject {
            @Nested
            class FromCollection {
            }

            @Nested
            class FromDefaultValue {
            }

            @Nested
            class FromDependency {
            }

            @Nested
            class FromFixedValue {
            }

            @Nested
            class FromLink {
            }

            @Nested
            class FromObject {
            }

            @Nested
            class FromReadOnly {
            }

            @Nested
            class FromUnFixedValue {
            }
        }

        @Nested
        class ToReadOnly {
            @Nested
            class FromCollection {
            }

            @Nested
            class FromDefaultValue {
            }

            @Nested
            class FromDependency {
            }

            @Nested
            class FromFixedValue {
            }

            @Nested
            class FromLink {
            }

            @Nested
            class FromObject {
            }

            @Nested
            class FromReadOnly {
            }

            @Nested
            class FromUnFixedValue {
            }
        }

        @Nested
        class ToUnFixedValue {
            @Nested
            class FromCollection {
            }

            @Nested
            class FromDefaultValue {
            }

            @Nested
            class FromDependency {
            }

            @Nested
            class FromFixedValue {
            }

            @Nested
            class FromLink {
            }

            @Nested
            class FromObject {
            }

            @Nested
            class FromReadOnly {
            }

            @Nested
            class FromUnFixedValue {
            }
        }
    }
}