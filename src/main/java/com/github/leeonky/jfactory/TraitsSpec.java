package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import static java.util.Arrays.asList;

class TraitsSpec {
    private String spec;
    private Set<String> traits;

    public TraitsSpec(String[] traits, String spec) {
        this.spec = spec;
        this.traits = new LinkedHashSet<>(asList(traits));
    }

    private void mergeTraits(TraitsSpec another) {
        traits.addAll(another.traits);
    }

    private void mergeSpec(TraitsSpec another, String property) {
        if (isDifferentSpec(another))
            throw new IllegalArgumentException(String.format("Cannot merge different spec `%s` and `%s` for %s",
                    spec, another.spec, property));
        if (spec == null)
            spec = another.spec;
    }

    private boolean isDifferentSpec(TraitsSpec another) {
        return spec != null && another.spec != null && !Objects.equals(spec, another.spec);
    }

    public Builder<?> toBuilder(JFactory jFactory, BeanClass<?> propertyType) {
        return (spec != null ? jFactory.spec(spec) : jFactory.type(propertyType.getType()))
                .traits(traits.toArray(new String[0]));
    }

    public void merge(TraitsSpec another, String property) {
        mergeTraits(another);
        mergeSpec(another, property);
    }
}
