package com.github.leeonky.jfactory;

import java.util.ArrayList;
import java.util.List;

class LinkCollection {

    private final List<Link> links = new ArrayList<>();

    public void processLinks(Producer<?> producer) {
        links.forEach(link -> link.process(producer));
        links.clear();
    }

    public void link(List<PropertyChain> properties) {
        if (properties.size() > 1)
            links.stream().filter(link -> link.contains(properties)).findFirst().orElseGet(this::newLink).merge(properties);
    }

    private Link newLink() {
        Link link = new Link();
        links.add(link);
        return link;
    }
}