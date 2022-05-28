package com.github.leeonky.jfactory;

public interface Transformer {
    default boolean matches(String input) {
        return true;
    }

    Object transform(String input);
}
