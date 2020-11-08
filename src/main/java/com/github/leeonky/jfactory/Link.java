package com.github.leeonky.jfactory;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;

class Link {
    private final Set<PropertyChain> properties = new LinkedHashSet<>();

    @SuppressWarnings("unchecked")
    public void process(Producer<?> producer) {
        Linker linker = syncLink(producer);
        properties.forEach(linkProperty -> producer.changeChild(linkProperty, (nextToLast, property) ->
                new LinkProducer(nextToLast.getPropertyWriterType(property), linker)));
    }

    @SuppressWarnings("unchecked")
    private <T> Linker<T> syncLink(Producer<?> producer) {
        Linker<T> linker = new Linker<>(emptyList());
        properties.stream().map(property -> (Producer<T>) producer.child(property))
                .map(Producer::getLinkerReference)
                .map(LinkerReference::getLinker)
                .flatMap(Linker::allLinked)
                .forEach(linker::mergeTo);
        return linker;
    }

    public boolean contains(List<PropertyChain> properties) {
        return properties.stream().anyMatch(this.properties::contains);
    }

    public void merge(List<PropertyChain> properties) {
        this.properties.addAll(properties);
    }
}
