package com.github.leeonky.jfactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

class RootInstance<T> implements Instance<T> {
    private final ValueCache<T> valueCache = new ValueCache<>();
    private final int sequence;
    private final Spec<T> spec;

    public RootInstance(int sequence, Spec<T> spec) {
        this.sequence = sequence;
        this.spec = spec;
    }

    @Override
    public int getSequence() {
        return sequence;
    }

    public Sub sub(String property) {
        return new Sub(property);
    }

    @Override
    public Spec<T> spec() {
        return spec;
    }

    @Override
    public Supplier<T> reference() {
        return valueCache::getValue;
    }

    public T cache(Supplier<T> supplier, Consumer<T> operation) {
        return valueCache.cache(supplier, operation);
    }

    class Sub extends RootInstance<T> {
        private final String property;

        public Sub(String property) {
            super(sequence, spec);
            this.property = property;
        }

        public String propertyInfo() {
            return String.format("%s#%d", property, getSequence());
        }

        public Collection inCollection() {
            return new Collection(emptyList());
        }

        class Collection extends Sub {
            private final List<Integer> indexes;

            public Collection(List<Integer> indexes) {
                super(property);
                this.indexes = new ArrayList<>(indexes);
            }

            @Override
            public String propertyInfo() {
                return String.format("%s%s", super.propertyInfo(),
                        indexes.stream().map(i -> String.format("[%d]", i)).collect(Collectors.joining()));
            }

            public Collection element(int index) {
                Collection collection = new Collection(indexes);
                collection.indexes.add(index);
                return collection;
            }
        }
    }
}
