package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//TODO move expression classes to new package
class KeyValue {
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

    private final String key;
    private final Object value;

    public KeyValue(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    // TODO large method
    public <T> Expression<T> createExpression(BeanClass<T> beanClass) {
        Matcher matcher = Pattern.compile(PATTERN_PROPERTY + PATTERN_COLLECTION_INDEX +
                PATTERN_MIX_IN_SPEC + PATTERN_INTENTLY + PATTERN_CONDITION).matcher(key);
        if (!matcher.matches())
            throw new IllegalArgumentException(String.format("Invalid property `%s` for %s creation.",
                    key, beanClass.getName()));

        String property = matcher.group(GROUP_PROPERTY);
        String index = matcher.group(GROUP_COLLECTION_INDEX);
        boolean intently = matcher.group(GROUP_INTENTLY) != null;

        MixInsSpec mixInsSpec = new MixInsSpec(matcher.group(GROUP_MIX_IN) != null ?
                matcher.group(GROUP_MIX_IN).split(", |,| ") : new String[0], matcher.group(GROUP_SPEC));
        KeyValueCollection keyValueCollection = new KeyValueCollection().add(matcher.group(GROUP_CONDITION), value);
        if (index != null)
            return new CollectionExpression<>(beanClass.getProperty(property), Integer.valueOf(index),
                    keyValueCollection.createSubExpression(beanClass.getPropertyWriter(property).getType().getProperty(index),
                            mixInsSpec, value).setIntently(intently));

        return keyValueCollection.createSubExpression(beanClass.getProperty(property), mixInsSpec, value).setIntently(intently);
    }

    public <T> Builder<T> apply(Builder<T> builder) {
        return builder.property(key, value);
    }

    public boolean nullKey() {
        return key == null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(KeyValue.class, key, value);
    }

    @Override
    public boolean equals(Object obj) {
        return BeanClass.cast(obj, KeyValue.class)
                .map(keyValue -> Objects.equals(key, keyValue.key) && Objects.equals(value, keyValue.value))
                .orElseGet(() -> super.equals(obj));
    }
}

