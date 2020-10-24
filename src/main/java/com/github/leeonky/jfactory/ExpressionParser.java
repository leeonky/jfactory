package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ExpressionParser<T> {
    private static final String PATTERN_PROPERTY = "([^.(!\\[]+)";
    private static final String PATTERN_COLLECTION_INDEX = "(\\[(\\d+)])?";
    private static final String PATTERN_MIX_IN = "(([^, ]+[, ])([^, ]+[, ])*)?";
    private static final String PATTERN_SPEC = "(.+)";
    private static final String PATTERN_CONDITION = "(\\." + PATTERN_SPEC + ")?";
    private static final String PATTERN_MIX_IN_DEFINITION = "(\\(" + PATTERN_MIX_IN + PATTERN_SPEC + "\\))?";
    private static final String PATTERN_INTENTLY = "(!)?";
    private static final int GROUP_PROPERTY = 1;
    private static final int GROUP_COLLECTION_INDEX = 3;
    private static final int GROUP_MIX_IN = 5;
    private static final int GROUP_SPEC = 8;
    private static final int GROUP_INTENTLY = 9;
    private static final int GROUP_CONDITION = 11;
    private final BeanClass<T> beanClass;
    private final Matcher matcher;

    private ExpressionParser(BeanClass<T> beanClass, String expression) {
        this.beanClass = beanClass;
        matcher = Pattern.compile(PATTERN_PROPERTY +
                PATTERN_COLLECTION_INDEX +
                PATTERN_MIX_IN_DEFINITION +
                PATTERN_INTENTLY +
                PATTERN_CONDITION).matcher(expression);
        if (!matcher.matches())
            throw new IllegalArgumentException(String.format("Invalid property `%s` for %s creation.",
                    expression, beanClass.getName()));
    }

    public static <T> PropertyExpression<T, T> parse(BeanClass<T> beanClass, String expression, Object value) {
        return new ExpressionParser<>(beanClass, expression).create(value);
    }

    private PropertyExpression<T, T> create(Object value) {
        String property = matcher.group(GROUP_PROPERTY);
        String index = matcher.group(GROUP_COLLECTION_INDEX);
        boolean intently = matcher.group(GROUP_INTENTLY) != null;
        String[] mixIn = matcher.group(GROUP_MIX_IN) != null ? matcher.group(GROUP_MIX_IN).split(", |,| ") : new String[0];
        String group = matcher.group(GROUP_SPEC);
        String condition = matcher.group(GROUP_CONDITION);

        if (index != null) {
            return new CollectionPropertyExpression<>(beanClass, beanClass, property, Integer.valueOf(index),
                    create(value, mixIn, group, condition, index, beanClass.getPropertyWriter(property).getType(), beanClass)
                            .setIntently(intently)
            );
        }
        return create(value, mixIn, group, condition, property, beanClass, beanClass).setIntently(intently);
    }

    private <H, B> PropertyExpression<H, B> create(Object value, String[] mixIn, String definition, String condition,
                                                   String property, BeanClass<H> hostClass, BeanClass<B> beanClass) {
        return condition != null ?
                new SubObjectPropertyExpression<>(condition, value, mixIn, definition, property, hostClass, beanClass)
                : new SingleValuePropertyExpression<>(value, mixIn, definition, property, hostClass, beanClass);
    }
}
