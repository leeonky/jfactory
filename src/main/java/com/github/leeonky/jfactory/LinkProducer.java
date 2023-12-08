package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.Optional;
import java.util.stream.Stream;

class LinkProducer<T> extends Producer<T> {
    private final Link.Reference<T> linkerReference;
    private final Producer<T> origin;

    public LinkProducer(BeanClass<T> type, Link<T> link, Producer<T> origin, PropertyChain linkProperty) {
        super(type);
        linkerReference = new Link.Reference<T>(linkProperty).setLinker(link);
        this.origin = origin;
    }

    @Override
    protected T produce() {
        return linkerReference.getLinker().chooseProducer().getValue();
    }

    @Override
    public Stream<Link.Reference<T>> allLinkerReferences(Producer<?> root, PropertyChain absoluteCurrent) {
        return linkerReference.getLinker().allLinkedReferences();
    }

    @Override
    public Producer<T> getLinkOrigin() {
        return origin;
    }

    @Override
    public Optional<Producer<?>> child(String property) {
        return linkerReference.getLinker().chooseProducer().child(property);
    }

    @Override
    protected Producer<T> changeFrom(ObjectProducer<T> producer) {
        return producer.isFixed() ? producer : this;
    }
}
