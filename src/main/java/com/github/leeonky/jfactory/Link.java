package com.github.leeonky.jfactory;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

class Link {
    private final Set<PropertyChain> properties = new LinkedHashSet<>();

    @SuppressWarnings("unchecked")
    public void process(Producer<?> root, PropertyChain current) {
        Linker linker = syncLink(root, current);
        absoluteProperties(current).forEach(linkProperty ->
                root.changeDescendant(linkProperty, (nextToLast, property) ->
                        new LinkProducer(nextToLast.getPropertyWriterType(property), linker,
                                root.descendant(linkProperty).getLinkOrigin(), linkProperty)));
    }

    private Stream<PropertyChain> absoluteProperties(PropertyChain current) {
        return properties.stream().map(current::concat);
    }

    @SuppressWarnings("unchecked")
    private <T> Linker<T> syncLink(Producer<?> root, PropertyChain current) {
        Linker<T> linker = new Linker<>(root);
        absoluteProperties(current)
                .flatMap(property -> ((Producer<T>) root.descendant(property))
                        .allLinkerReferences(root, property))
                .distinct().forEach(linker::link);
        return linker;
    }

    public boolean contains(List<PropertyChain> properties) {
        return properties.stream().anyMatch(this.properties::contains);
    }

    public void merge(List<PropertyChain> properties) {
        this.properties.addAll(properties);
    }
}
