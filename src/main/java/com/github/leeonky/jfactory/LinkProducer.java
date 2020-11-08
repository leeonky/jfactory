package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

public class LinkProducer<T> extends Producer<T> {
    private final LinkerReference<T> linkerReference;

    public LinkProducer(BeanClass<T> type, Linker<T> tLinker) {
        super(type);
        linkerReference = new LinkerReference<>(tLinker);
    }

    @Override
    protected T produce() {
        return linkerReference.getLinker().produce();
    }

    @Override
    public LinkerReference<T> getLinkerReference() {
        return linkerReference;
    }
}
