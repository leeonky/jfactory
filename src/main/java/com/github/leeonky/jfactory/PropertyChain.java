package com.github.leeonky.jfactory;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

    //TODO move back to producer
    //TODO try to remove optional
    public Optional<Producer<?>> getProducerForCreate(Producer<?> producer) {
        return childOf(producer, Optional::ofNullable, Producer::getChildOrDefault);
    }

    public <T> T childOf(Producer<?> producer, Function<Producer<?>, T> last, BiFunction<Producer<?>, String, Producer<?>> accessor) {
        return childOf(new LinkedList<>(property), producer, last, accessor);
    }

    private <T> T childOf(LinkedList<Object> properties, Producer<?> producer, Function<Producer<?>, T> last, BiFunction<Producer<?>, String, Producer<?>> accessor) {
        Predicate<List<Object>> predicate = List::isEmpty;
        return childOf(properties, producer, predicate, last, accessor);
    }

    private <T> T childOf(LinkedList<Object> properties, Producer<?> producer, Predicate<List<Object>> condition,
                          Function<Producer<?>, T> last, BiFunction<Producer<?>, String, Producer<?>> accessor) {
        if (condition.test(properties))
            return last.apply(producer);
        Object first = properties.removeFirst();
        return childOf(properties, accessor.apply(producer, first.toString()), last, accessor);
    }

    public void forTail(Producer<?> producer, BiConsumer<String, Producer<?>> operation) {
        String tail = property.get(property.size() - 1).toString();
        new PropertyChain(property.subList(0, property.size() - 1)).getProducerForCreate(producer)
                .ifPresent(producer1 -> operation.accept(tail, producer1));
    }
}
