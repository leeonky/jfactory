package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

class PropertyValueBuilders {

    public static class StringPropertyValueBuilder implements PropertyValueBuilder<String> {

        @Override
        public <T> String create(BeanClass<T> type, Instance<T> instance) {
            return String.format("%s#%d", instance.getProperty(), instance.getSequence());
        }

        //TODO can be guessed from generic type params
        @Override
        public Class<String> getType() {
            return String.class;
        }
    }
}
