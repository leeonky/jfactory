package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.Optional;
import java.util.stream.Stream;

class LinkProducer<T> extends Producer<T> {
    private final Linker.Reference<T> linkerReference;
    private final Producer<T> origin;

    public LinkProducer(BeanClass<T> type, Linker<T> linker, Producer<T> origin) {
        super(type);
        linkerReference = new Linker.Reference<T>().setLinker(linker);
        this.origin = origin;
    }

    @Override
    protected T produce() {
        return linkerReference.getLinker().chooseProducer().getValue();
    }

    @Override
    public Stream<Linker.Reference<T>> allLinkerReferences(Producer<?> root, PropertyChain absoluteCurrent) {
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
}
