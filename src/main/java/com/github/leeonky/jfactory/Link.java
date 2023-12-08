package com.github.leeonky.jfactory;

import java.util.*;
import java.util.stream.Stream;

import static com.github.leeonky.util.BeanClass.cast;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

class Link<T> {
    private static final List<Class<?>> TYPE_PRIORITY = asList(
            FixedValueProducer.class,
            ReadOnlyProducer.class,
            DependencyProducer.class,
            UnFixedValueProducer.class
    );
    private final Set<PropertyChain> linkedAbsoluteProperties = new LinkedHashSet<>();
    private final Producer<?> root;
    private final Set<Reference<T>> references = new LinkedHashSet<>();

    public Link(Producer<?> root) {
        this.root = root;
    }

    public Link<T> link(PropertyChain absoluteCurrent) {
        linkedAbsoluteProperties.add(absoluteCurrent);
        return this;
    }

    private Optional<Producer<T>> chooseProducer(Class<?> type, Collection<Producer<T>> linkedProducers) {
        Optional<Producer<T>> fixedProducer = linkedProducers.stream().filter(Producer::isFixed).findFirst();
        return fixedProducer.isPresent() ? fixedProducer : linkedProducers.stream().filter(type::isInstance).findFirst();
    }

    @SuppressWarnings("unchecked")
    public Producer<T> chooseProducer() {
        List<Producer<T>> linkedProducers = linkedAbsoluteProperties.stream()
                .map(p -> (Producer<T>) root.descendant(p).getLinkOrigin())
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
        private Link<T> link;

        public Reference(PropertyChain absoluteCurrent) {
            this.absoluteCurrent = absoluteCurrent;
        }

        public static <T> Reference<T> defaultLinkerReference(Producer<?> root, PropertyChain absoluteCurrent) {
            return new Reference<T>(absoluteCurrent).setLinker(new Link<T>(root).link(absoluteCurrent));
        }

        public Link<T> getLinker() {
            return link;
        }

        public Reference<T> setLinker(Link<T> link) {
            this.link = link;
            link.references.add(this);
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