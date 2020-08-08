package com.github.leeonky.jfactory;

import com.github.leeonky.util.GenericType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Spec<T> {
    private List<Consumer<ObjectProducer<T>>> operations = new ArrayList<>();

    public Spec() {
        main();
    }

    public void main() {
    }

    public PropertySpecification property(String name) {
        return new PropertySpecification(name);
    }

    void apply(ObjectProducer<T> producer) {
        operations.forEach(o -> o.accept(producer));
    }

    @SuppressWarnings("unchecked")
    Class<T> getType() {
        return (Class<T>) GenericType.createGenericType(getClass().getGenericSuperclass()).getGenericTypeParameter(0)
                .orElseThrow(() -> new IllegalStateException(String.format("Invalid Spec '%s', should specify generic type or override getType() method", getClass().getName())))
                .getRawType();
    }

    String getName() {
        return getClass().getSimpleName();
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

        public Spec<T> value(Supplier<?> value) {
            operations.add(objectProducer -> objectProducer.addChild(name, new UnFixedValueProducer<>(value)));
            return Spec.this;
        }
    }
}
