package com.github.leeonky.jfactory;

public class LinkerReference<T> {
    private Linker<T> linker;

    public LinkerReference(Linker<T> linker) {
        this.linker = linker;
        linker.linkToReference(this);
    }

    public Linker<T> getLinker() {
        return linker;
    }

    public void setLinker(Linker<T> linker) {
        this.linker = linker;
    }
}