package com.github.leeonky.jfactory;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.IntStream.range;

class PropertyChain {
    public final List<Object> property;

    private PropertyChain(String property) {
        this.property = Arrays.stream(property.split("[\\[\\].]"))
                .filter(s -> !s.isEmpty())
                .map(this::tryToNumber).collect(Collectors.toList());
    }

    private PropertyChain(List<Object> propertyChain) {
        property = new ArrayList<>(propertyChain);
    }

    public static PropertyChain createChain(String property) {
        return new PropertyChain(property);
    }

    private Object tryToNumber(String s) {
        try {
            return Integer.valueOf(s);
        } catch (Exception ignore) {
            return s;
        }
    }

    public boolean isTopLevelPropertyCollection() {
        return property.size() == 2 && (property.get(1) instanceof Integer);
    }

    public boolean isSingle() {
        return property.size() == 1;
    }

    @Override
    public int hashCode() {
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(PropertyChain.class);
        objects.addAll(property);
        return Objects.hash(objects.toArray());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PropertyChain && Objects.equals(property, ((PropertyChain) obj).property);
    }

    @Override
    public String toString() {
        return property.stream().map(c -> {
            if (c instanceof Integer)
                return String.format("[%d]", c);
            return c.toString();
        }).collect(Collectors.joining(".")).replace(".[", "[");
    }

    public <T> T access(Producer<?> producer, BiFunction<Producer<?>, String, Producer<?>> accessor,
                        Function<Producer<?>, T> returnWrapper) {
        if (property.isEmpty())
            return returnWrapper.apply(producer);
        String head = property.get(0).toString();
        return new PropertyChain(property.subList(1, property.size()))
                .access(accessor.apply(producer, head), accessor, returnWrapper);
    }

    public String tail() {
        return property.get(property.size() - 1).toString();
    }

    public PropertyChain removeTail() {
        return new PropertyChain(property.subList(0, property.size() - 1));
    }

    public PropertyChain concat(PropertyChain subChain) {
        List<Object> chain = new ArrayList<>(property);
        chain.addAll(subChain.property);
        return new PropertyChain(chain);
    }

    public PropertyChain concat(String node) {
        return concat(createChain(node));
    }

    public Optional<PropertyChain> sub(PropertyChain propertyChain) {
        if (contains(propertyChain))
            return of(new PropertyChain(property.subList(propertyChain.property.size(), property.size())));
        return empty();
    }

    private boolean contains(PropertyChain propertyChain) {
        return propertyChain.property.size() <= property.size()
                && range(0, propertyChain.property.size())
                .allMatch(i -> Objects.equals(propertyChain.property.get(i), property.get(i)));
    }
}
