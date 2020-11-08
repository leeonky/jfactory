package com.github.leeonky.jfactory;

import java.util.*;
import java.util.stream.Stream;

public class Linker<T> {
    private final Set<Producer<T>> linkedProducers;
    private List<LinkerReference<T>> references = new ArrayList<>();

    public Linker(List<Producer<T>> linkedProducers) {
        this.linkedProducers = new LinkedHashSet<>(linkedProducers);
    }

    private Optional<Producer<T>> chooseProducer(Class<?> type) {
        //TODO should return only one producer
        return linkedProducers.stream().filter(type::isInstance).findFirst();
    }

    public T produce() {
        return chooseProducer(FixedValueProducer.class).orElseGet(() -> linkedProducers.iterator().next()).getValue();
    }

    public void mergeTo(LinkerReference<T> reference) {
        linkedProducers.addAll(reference.getLinker().linkedProducers);
        reference.setLinker(this);
        references.add(reference);
    }

    public Stream<LinkerReference<T>> allLinked() {
        return references.stream();
    }

    public void linkToReference(LinkerReference<T> reference) {
        references.add(reference);
    }
}