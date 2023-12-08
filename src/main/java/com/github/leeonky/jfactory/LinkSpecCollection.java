package com.github.leeonky.jfactory;

import java.util.ArrayList;
import java.util.List;

class LinkSpecCollection {

    private final List<LinkSpec> linkSpecs = new ArrayList<>();

    public void processLinks(Producer<?> root, PropertyChain base) {
        linkSpecs.forEach(linkSpec -> linkSpec.process(root, base));
        linkSpecs.clear();
    }

    public void link(List<PropertyChain> properties) {
        if (properties.size() > 1)
            linkSpecs.stream().filter(linkSpec -> linkSpec.contains(properties)).findFirst().orElseGet(this::newLink).merge(properties);
    }

    private LinkSpec newLink() {
        LinkSpec linkSpec = new LinkSpec();
        linkSpecs.add(linkSpec);
        return linkSpec;
    }
}