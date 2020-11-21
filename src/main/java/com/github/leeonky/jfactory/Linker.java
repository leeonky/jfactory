package com.github.leeonky.jfactory;

import java.util.*;
import java.util.stream.Stream;

import static com.github.leeonky.util.BeanClass.cast;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

class Linker<T> {
    private static final List<Class<?>> TYPE_PRIORITY = asList(
            FixedValueProducer.class,
            ReadOnlyProducer.class,
            DependencyProducer.class,
            UnFixedValueProducer.class
    );
    private final Set<PropertyChain> linkedAbsoluteProperties = new LinkedHashSet<>();
    private final Producer<?> root;
    private Set<Reference<T>> references = new LinkedHashSet<>();

    public Linker(Producer<?> root) {
        this.root = root;
    }

    public Linker<T> link(PropertyChain absoluteCurrent) {
        linkedAbsoluteProperties.add(absoluteCurrent);
        return this;
    }

    private Optional<Producer<T>> chooseProducer(Class<?> type, Collection<Producer<T>> linkedProducers) {
        List<Producer<T>> producers = linkedProducers.stream().filter(type::isInstance).limit(2).collect(toList());
        if (producers.size() > 1)
            throw new IllegalStateException("Ambiguous value in link");
        return producers.stream().findFirst();
    }

    @SuppressWarnings("unchecked")
    public Producer<T> chooseProducer() {
        List<Producer<T>> linkedProducers = linkedAbsoluteProperties.stream()
                .map(p -> (Producer<T>) root.child(p).getLinkOrigin())
                .collect(toList());
        return TYPE_PRIORITY.stream().map(type -> chooseProducer(type, linkedProducers))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElseGet(() -> linkedProducers.iterator().next());
    }

    public void link(Reference<T> reference) {
        linkedAbsoluteProperties.addAll(reference.getLinker().linkedAbsoluteProperties);
        reference.setLinker(this);
    }

    public Stream<Reference<T>> allLinkedReferences() {
        return references.stream();
    }

    static class Reference<T> {
        private final PropertyChain absoluteCurrent;
        private Linker<T> linker;

        public Reference(PropertyChain absoluteCurrent) {
            this.absoluteCurrent = absoluteCurrent;
        }

        public static <T> Reference<T> defaultLinkerReference(Producer<?> root, PropertyChain absoluteCurrent) {
            return new Reference<T>(absoluteCurrent).setLinker(new Linker<T>(root).link(absoluteCurrent));
        }

        public Linker<T> getLinker() {
            return linker;
        }

        public Reference<T> setLinker(Linker<T> linker) {
            this.linker = linker;
            linker.references.add(this);
            return this;
        }

        @Override
        public int hashCode() {
            return Objects.hash(Reference.class, absoluteCurrent);
        }

        @Override
        public boolean equals(Object obj) {
            return cast(obj, Reference.class)
                    .map(another -> Objects.equals(absoluteCurrent, another.absoluteCurrent))
                    .orElseGet(() -> super.equals(obj));
        }
    }
}