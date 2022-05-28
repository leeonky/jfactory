package com.github.leeonky.jfactory;

public interface Transformer {
    default boolean matches(String input) {
        return true;
    }

    Object transform(String input);

    default Object checkAndTransform(Object value) {
        if (value instanceof String && matches((String) value))
            return transform((String) value);
        return value;
    }
}
