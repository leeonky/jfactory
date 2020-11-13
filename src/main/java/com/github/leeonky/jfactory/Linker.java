package com.github.leeonky.jfactory;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

class Linker<T> {
    private static final List<Class<?>> TYPE_PRIORITY = asList(
            FixedValueProducer.class,
            ReadOnlyProducer.class,
            DependencyProducer.class,
            UnFixedValueProducer.class
    );
    private final Set<Producer<T>> linkedProducers = new LinkedHashSet<>();
    private Set<Reference<T>> references = new LinkedHashSet<>();

    public Linker<T> link(Producer<T> producer) {
        linkedProducers.add(producer);
        return this;
    }

    private Optional<Producer<T>> chooseProducer(Class<?> type) {
        //TODO should return only one producer
        List<Producer<T>> producers = linkedProducers.stream().filter(type::isInstance).limit(2).collect(toList());
        if (producers.size() > 1)
            throw new IllegalStateException("Ambiguous value in link");
        return producers.stream().findFirst();
    }

    public T produce() {
        return TYPE_PRIORITY.stream().map(this::chooseProducer)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElseGet(() -> linkedProducers.iterator().next()).getValue();
    }

    public void link(Reference<T> reference) {
        linkedProducers.addAll(reference.getLinker().linkedProducers);
        reference.setLinker(this);
    }

    public Stream<Reference<T>> allLinkedReferences() {
        return references.stream();
    }

    static class Reference<T> {
        private Linker<T> linker;

        public static <T> Reference<T> defaultLinkerReference(Producer<T> producer) {
            return new Reference<T>().setLinker(new Linker<T>().link(producer));
        }

        public Linker<T> getLinker() {
            return linker;
        }

        public Reference<T> setLinker(Linker<T> linker) {
            this.linker = linker;
            linker.references.add(this);
            return this;
        }
    }
}