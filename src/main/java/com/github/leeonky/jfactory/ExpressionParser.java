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
    private static final String PATTERN_MIX_IN_SPEC = "(\\(" + PATTERN_MIX_IN + PATTERN_SPEC + "\\))?";
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
        matcher = Pattern.compile(PATTERN_PROPERTY + PATTERN_COLLECTION_INDEX +
                PATTERN_MIX_IN_SPEC + PATTERN_INTENTLY + PATTERN_CONDITION).matcher(expression);
        if (!matcher.matches())
            throw new IllegalArgumentException(String.format("Invalid property `%s` for %s creation.",
                    expression, beanClass.getName()));
    }

    public static <T> PropertyExpression<T> parse(BeanClass<T> beanClass, String expression, Object value) {
        return new ExpressionParser<>(beanClass, expression).create(value);
    }

    private PropertyExpression<T> create(Object value) {
        String property = matcher.group(GROUP_PROPERTY);
        String index = matcher.group(GROUP_COLLECTION_INDEX);
        boolean intently = matcher.group(GROUP_INTENTLY) != null;
        String condition = matcher.group(GROUP_CONDITION);
        MixInsSpec mixInsSpec = new MixInsSpec(
                matcher.group(GROUP_MIX_IN) != null ? matcher.group(GROUP_MIX_IN).split(", |,| ") : new String[0],
                matcher.group(GROUP_SPEC));

        if (index != null)
            return new CollectionPropertyExpression<>(Integer.valueOf(index),
                    create(value, condition, mixInsSpec, new Property<>(beanClass.getPropertyWriter(property).getType(), index))
                            .setIntently(intently), new Property<>(beanClass, property));
        return create(value, condition, mixInsSpec, new Property<>(beanClass, property)).setIntently(intently);
    }

    private <H> PropertyExpression<H> create(Object value, String condition, MixInsSpec mixInsSpec, Property<H> property) {
        return condition != null ? new SubObjectPropertyExpression<>(condition, value, mixInsSpec, property)
                : new SingleValuePropertyExpression<>(value, mixInsSpec, property);
    }
}
