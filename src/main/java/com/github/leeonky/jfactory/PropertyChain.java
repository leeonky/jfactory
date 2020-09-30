package com.github.leeonky.jfactory;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.of;

class PropertyChain {
    public final List<Object> property;

    public PropertyChain(String property) {
        this.property = Arrays.stream(property.split("[\\[\\].]"))
                .filter(s -> !s.isEmpty())
                .map(s -> {
                    try {
                        return Integer.valueOf(s);
                    } catch (Exception ignore) {
                        return s;
                    }
                }).collect(Collectors.toList());
    }

    public PropertyChain(List<Object> propertyChain) {
        property = new ArrayList<>(propertyChain);
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

    public Optional<Producer<?>> getProducer(Producer<?> producer) {
        //TODO producer maybe null
        if (property.isEmpty())
            return of(producer);
        return removeHead().getProducer(producer.getChild(property.get(0).toString()));
    }

    private PropertyChain removeHead() {
        return new PropertyChain(property.subList(1, property.size()));
    }

    public String getTail() {
        return property.get(property.size() - 1).toString();
    }

    public PropertyChain removeTail() {
        return new PropertyChain(property.subList(0, property.size() - 1));
    }
}
