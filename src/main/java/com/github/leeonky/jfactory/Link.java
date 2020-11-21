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
                root.changeChild(linkProperty, (nextToLast, property) -> new LinkProducer(
                        nextToLast.getPropertyWriterType(property), linker, root.child(linkProperty).getLinkOrigin())));
    }

    private Stream<PropertyChain> absoluteProperties(PropertyChain current) {
        return properties.stream().map(current::concat);
    }

    @SuppressWarnings("unchecked")
    private <T> Linker<T> syncLink(Producer<?> root, PropertyChain current) {
        Linker<T> linker = new Linker<>(root);
        absoluteProperties(current)
                .flatMap(property -> {
                    Producer<T> producer = (Producer<T>) root.child(property);
                    return producer.allLinkerReferences(root, property);
                })
                .distinct()
                .forEach(linker::link);
        return linker;
    }

    public boolean contains(List<PropertyChain> properties) {
        return properties.stream().anyMatch(this.properties::contains);
    }

    public void merge(List<PropertyChain> properties) {
        this.properties.addAll(properties);
    }
}
