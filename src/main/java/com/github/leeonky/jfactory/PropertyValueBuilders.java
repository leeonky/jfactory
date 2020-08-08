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

        //TODO can be guessed from generic type params
        @Override
        public Class<String> getType() {
            return String.class;
        }
    }
}
