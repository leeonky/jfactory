package com.github.leeonky.jfactory;

public class Specification<T> {
    private ObjectProducer<T> objectProducer;

    public PropertySpecification property(String name) {
        return new PropertySpecification(name);
    }

    public Specification<T> setObjectProducer(ObjectProducer<T> objectProducer) {
        this.objectProducer = objectProducer;
        return this;
    }

    public class PropertySpecification {
        private final String name;

        public PropertySpecification(String name) {
            this.name = name;
        }

        public Specification<T> value(Object value) {
            objectProducer.addChild(name, new UnFixedValueProducer<>(() -> value));
            return Specification.this;
        }
    }
}
