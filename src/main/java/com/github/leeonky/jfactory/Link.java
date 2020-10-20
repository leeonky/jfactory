package com.github.leeonky.jfactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class Link {
    private final List<PropertyChain> properties = new ArrayList<>();

    public Link(List<PropertyChain> properties) {
        this.properties.addAll(properties);
    }

    @SuppressWarnings("unchecked")
    public void process(Producer<?> producer) {
        List<Producer<?>> linkedProducers = properties.stream().map(producer::getChild).collect(Collectors.toList());
        properties.forEach(linkProperty -> producer.changeChild(linkProperty,
                (origin, property) -> new LinkProducer(linkedProducers, origin.getType())));
    }
}
