package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
        public <T> String create(BeanClass<T> beanType, Instance<T> instance) {
            return String.format("%s#%d%s", instance.getProperty(), instance.getSequence(),
                    instance.getIndexes().stream().map(i -> String.format("[%d]", i)).collect(Collectors.joining()));
        }
    }

    public static class DefaultIntegerBuilder implements DefaultValueBuilder<Integer> {

        @Override
        public <T> Integer create(BeanClass<T> beanType, Instance<T> instance) {
            return instance.getSequence();
        }
    }

    public static class DefaultTypeBuilder<T> implements DefaultValueBuilder<T> {
        private final BeanClass<T> type;

        public DefaultTypeBuilder(BeanClass<T> type) {
            this.type = type;
        }

        @Override
        public T create(BeanClass beanType, Instance instance) {
            return type.createDefault();
        }

        @Override
        public Class<T> getType() {
            return type.getType();
        }
    }
}
