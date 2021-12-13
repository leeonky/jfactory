package com.github.leeonky.jfactory;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Repeatable(PropertyAliases.class)
@Target(TYPE)
@Retention(RUNTIME)
public @interface PropertyAlias {
    String alias();

    String property();
}
