package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;
import com.github.leeonky.util.Property;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

//TODO use a parser to parse this
class KeyValue {
    private static final String PATTERN_PROPERTY = "([^.(!\\[]+)";
    private static final String PATTERN_COLLECTION_INDEX = "(\\[(-?\\d+)])?";
    private static final String PATTERN_SPEC_TRAIT_WORD = "[^, )]";
    private static final String PATTERN_TRAIT = "((" + PATTERN_SPEC_TRAIT_WORD + "+[, ])(" + PATTERN_SPEC_TRAIT_WORD + "+[, ])*)?";
    private static final String PATTERN_SPEC = "(" + PATTERN_SPEC_TRAIT_WORD + "+)";
    private static final String PATTERN_TRAIT_SPEC = "(\\(" + PATTERN_TRAIT + PATTERN_SPEC + "\\))?";
    private static final String PATTERN_CLAUSE = "(\\." + "(.+)" + ")?";
    private static final String PATTERN_INTENTLY = "(!)?";
    private static final int GROUP_PROPERTY = 1;
    private static final int GROUP_COLLECTION_INDEX = 3;
    private static final int GROUP_TRAIT = 5;
    private static final int GROUP_SPEC = 8;
    private static final int GROUP_INTENTLY = 9;
    private static final int GROUP_CLAUSE = 11;
    private final String key;
    private final Object value;
    private final FactorySet factorySet;

    public KeyValue(String key, Object value, FactorySet factorySet) {
        this.key = key;
        this.value = value;
        this.factorySet = factorySet;
    }

    public <T> Expression<T> createExpression(BeanClass<T> beanClass, ObjectFactory<T> objectFactory) {
        Matcher matcher = parse(beanClass);
        Property<T> property = beanClass.getProperty(matcher.group(GROUP_PROPERTY));
        return hasIndex(matcher).map(index -> createCollectionExpression(matcher, property, index, objectFactory))
                .orElseGet(() -> createSubExpression(matcher, property, null, objectFactory));
    }

    private <T> Expression<T> createCollectionExpression(Matcher matcher, Property<T> property, String index, ObjectFactory<T> objectFactory) {
        return new CollectionExpression<>(property, parseInt(index),
                createSubExpression(matcher, property.getWriter().getType().getProperty(index), property, objectFactory));
    }

    private Optional<String> hasIndex(Matcher matcher) {
        return Optional.ofNullable(matcher.group(GROUP_COLLECTION_INDEX));
    }

    private <T> Expression<T> createSubExpression(Matcher matcher, Property<T> property, Property<?> parentProperty, ObjectFactory<?> objectFactory) {
        KeyValueCollection properties = new KeyValueCollection(factorySet).append(matcher.group(GROUP_CLAUSE), value);
        TraitsSpec traitsSpec = new TraitsSpec(matcher.group(GROUP_TRAIT) != null ?
                matcher.group(GROUP_TRAIT).split(", |,| ") : new String[0], matcher.group(GROUP_SPEC));
        return properties.createExpression(property, traitsSpec, parentProperty, objectFactory).setIntently(matcher.group(GROUP_INTENTLY) != null);
    }

    private <T> Matcher parse(BeanClass<T> beanClass) {
        Matcher matcher = Pattern.compile(PATTERN_PROPERTY + PATTERN_COLLECTION_INDEX +
                PATTERN_TRAIT_SPEC + PATTERN_INTENTLY + PATTERN_CLAUSE).matcher(key);
        if (!matcher.matches())
            throw new IllegalArgumentException(String.format("Invalid property `%s` for %s creation.",
                    key, beanClass.getName()));
        return matcher;
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
    public boolean equals(Object another) {
        return BeanClass.cast(another, KeyValue.class)
                .map(keyValue -> Objects.equals(key, keyValue.key) && Objects.equals(value, keyValue.value))
                .orElseGet(() -> super.equals(another));
    }

    public Object getValue() {
        return value;
    }
}

