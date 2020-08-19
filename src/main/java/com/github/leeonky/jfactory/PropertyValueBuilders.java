package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.stream.Collectors;

class PropertyValueBuilders {

    public static class StringPropertyValueBuilder implements PropertyValueBuilder<String> {

        @Override
        public <T> String create(BeanClass<T> type, Instance<T> instance) {
            return String.format("%s#%d%s", instance.getProperty(), instance.getSequence(),
                    instance.getIndexes().stream().map(i -> String.format("[%d]", i)).collect(Collectors.joining()));
        }
    }

    public static class DefaultValueBuilder<T> implements PropertyValueBuilder<T> {
        private final BeanClass<T> type;

        public DefaultValueBuilder(BeanClass<T> type) {
            this.type = type;
        }

        @Override
        public T create(BeanClass type, Instance instance) {
            return this.type.createDefault();
        }

        @Override
        public Class<T> getType() {
            return type.getType();
        }
    }
}
