package com.github.leeonky.jfactory;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

class LinkSpec {
    private final Set<PropertyChain> linkedProperties = new LinkedHashSet<>();

    @SuppressWarnings("unchecked")
    public void process(Producer<?> root, PropertyChain base) {
        Link link = syncLink(root, base);
        absoluteProperties(base).forEach(linkProperty -> root.changeDescendant(linkProperty,
                (objectProducer, property) -> new LinkProducer(objectProducer.getPropertyWriterType(property), link,
                        root.descendant(linkProperty).getLinkOrigin(), linkProperty)));
    }

    private Stream<PropertyChain> absoluteProperties(PropertyChain base) {
        return linkedProperties.stream().map(base::concat);
    }

    @SuppressWarnings("unchecked")
    private <T> Link<T> syncLink(Producer<?> root, PropertyChain base) {
        Link<T> link = new Link<>(root);
        absoluteProperties(base).flatMap(property -> ((Producer<T>) root.descendant(property))
                .allLinkerReferences(root, property)).distinct().forEach(link::link);
        return link;
    }

    public boolean contains(List<PropertyChain> properties) {
        return properties.stream().anyMatch(linkedProperties::contains);
    }

    public void merge(List<PropertyChain> properties) {
        linkedProperties.addAll(properties);
    }
}
