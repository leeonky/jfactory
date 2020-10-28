package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


//TODO move expression classes to new package
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
    private final Object value;

    private ExpressionParser(BeanClass<T> beanClass, String expression, Object value) {
        matcher = Pattern.compile(PATTERN_PROPERTY + PATTERN_COLLECTION_INDEX +
                PATTERN_MIX_IN_SPEC + PATTERN_INTENTLY + PATTERN_CONDITION).matcher(expression);
        if (!matcher.matches())
            throw new IllegalArgumentException(String.format("Invalid property `%s` for %s creation.",
                    expression, beanClass.getName()));

        this.beanClass = beanClass;
        this.value = value;
    }

    public static <T> PropertyExpression<T> parse(BeanClass<T> beanClass, String expression, Object value) {
        return new ExpressionParser<>(beanClass, expression, value).create();
    }

    private PropertyExpression<T> create() {
        String property = matcher.group(GROUP_PROPERTY);
        String index = matcher.group(GROUP_COLLECTION_INDEX);
        boolean intently = matcher.group(GROUP_INTENTLY) != null;

        MixInsSpec mixInsSpec = getMixInsSpec();
        SupKeyValue supKeyValue = getSupKeyValue();
        if (index != null)
            return new CollectionPropertyExpression<>(Integer.valueOf(index),
                    supKeyValue.createSubExpression(beanClass.getPropertyWriter(property).getType().getProperty(index),
                            mixInsSpec, value).setIntently(intently), beanClass.getProperty(property));

        return supKeyValue.createSubExpression(beanClass.getProperty(property), mixInsSpec, value).setIntently(intently);
    }

    private SupKeyValue getSupKeyValue() {
        return new SupKeyValue(matcher.group(GROUP_CONDITION), value);
    }

    private MixInsSpec getMixInsSpec() {
        return new MixInsSpec(matcher.group(GROUP_MIX_IN) != null ? matcher.group(GROUP_MIX_IN).split(", |,| ") : new String[0],
                matcher.group(GROUP_SPEC));
    }
}
