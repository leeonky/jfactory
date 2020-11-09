package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

public class LinkProducer<T> extends Producer<T> {
    private final LinkerReference<T> linkerReference;

    public LinkProducer(BeanClass<T> type, Linker<T> linker) {
        super(type);
        linkerReference = new LinkerReference<>(linker);
    }

    @Override
    protected T produce() {
        return linkerReference.getLinker().produce();
    }

    @Override
    protected LinkerReference<T> getLinkerReference() {
        return linkerReference;
    }
}
