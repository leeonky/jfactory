package com.github.leeonky.jfactory;

public class LinkerReference<T> {
    private Linker<T> linker;

    public LinkerReference(Linker<T> linker) {
        this.linker = linker;
        linker.linkToReference(this);
    }

    private LinkerReference(Producer<T> producer) {
        linker = new Linker<>(producer);
        linker.linkToReference(this);
    }

    public static <T> LinkerReference<T> defaultLinkerReference(Producer<T> producer) {
        return new LinkerReference<>(producer);
    }

    public Linker<T> getLinker() {
        return linker;
    }

    public void setLinker(Linker<T> linker) {
        this.linker = linker;
    }
}