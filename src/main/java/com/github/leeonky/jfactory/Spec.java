package com.github.leeonky.jfactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Spec<T> {
    private List<Consumer<ObjectProducer<T>>> operations = new ArrayList<>();

    public void main() {
    }

    public PropertySpecification property(String name) {
        return new PropertySpecification(name);
    }

    void apply(ObjectProducer<T> producer) {
        operations.forEach(o -> o.accept(producer));
    }

    public class PropertySpecification {
        private final String name;

        public PropertySpecification(String name) {
            this.name = name;
        }

        public Spec<T> value(Object value) {
            operations.add(objectProducer -> objectProducer.addChild(name, new UnFixedValueProducer<>(() -> value)));
            return Spec.this;
        }
    }
}
