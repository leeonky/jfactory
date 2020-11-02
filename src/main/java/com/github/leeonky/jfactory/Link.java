package com.github.leeonky.jfactory;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class Link {
    private final Set<PropertyChain> properties = new LinkedHashSet<>();

    @SuppressWarnings("unchecked")
    public void process(Producer<?> producer) {
        List<Producer<?>> linkedProducers = properties.stream().map(producer::child).collect(Collectors.toList());
        properties.forEach(linkProperty -> producer.changeChild(linkProperty,
                (nextToLast, property) -> new LinkProducer(linkedProducers, nextToLast.getType().getPropertyWriter(property).getType())));
    }

    public boolean contains(List<PropertyChain> properties) {
        return properties.stream().anyMatch(this.properties::contains);
    }

    public void merge(List<PropertyChain> properties) {
        this.properties.addAll(properties);
    }
}
