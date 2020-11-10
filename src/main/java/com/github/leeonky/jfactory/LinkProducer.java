package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.stream.Stream;

class LinkProducer<T> extends Producer<T> {
    private final Linker.Reference<T> linkerReference;

    public LinkProducer(BeanClass<T> type, Linker<T> linker) {
        super(type);
        linkerReference = new Linker.Reference<T>().setLinker(linker);
    }

    @Override
    protected T produce() {
        return linkerReference.getLinker().produce();
    }

    @Override
    public Stream<Linker.Reference<T>> allLinkerReferences() {
        return linkerReference.getLinker().allLinkedReferences();
    }
}
