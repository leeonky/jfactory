package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;

class DefaultValueBuilders {
    private final Map<Class<?>, DefaultValueBuilder<?>> defaultValueBuilders = new HashMap<>();

    public DefaultValueBuilders() {
        defaultValueBuilders.put(String.class, new DefaultStringBuilder());
        defaultValueBuilders.put(Integer.class, new DefaultIntegerBuilder());
        defaultValueBuilders.put(int.class, defaultValueBuilders.get(Integer.class));
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<DefaultValueBuilder<T>> query(Class<T> type) {
        return ofNullable((DefaultValueBuilder<T>) defaultValueBuilders.get(type));
    }

    public static class DefaultStringBuilder implements DefaultValueBuilder<String> {

        @Override
        public <T> String create(BeanClass<T> beanType, SubInstance instance) {
            return instance.propertyInfo();
        }
    }

    public static class DefaultIntegerBuilder implements DefaultValueBuilder<Integer> {

        @Override
        public <T> Integer create(BeanClass<T> beanType, SubInstance instance) {
            return instance.getSequence();
        }
    }

    public static class DefaultTypeBuilder<V> implements DefaultValueBuilder<V> {
        private final BeanClass<V> type;

        public DefaultTypeBuilder(BeanClass<V> type) {
            this.type = type;
        }

        @Override
        public <T> V create(BeanClass<T> beanType, SubInstance instance) {
            return type.createDefault();
        }

        @Override
        public Class<V> getType() {
            return type.getType();
        }
    }
}
