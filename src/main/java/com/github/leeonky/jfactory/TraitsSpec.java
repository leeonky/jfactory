package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;

import static java.util.Arrays.asList;

class TraitsSpec {
    private String[] traits;
    private String spec;

    public TraitsSpec(String[] traits, String spec) {
        this.traits = traits;
        this.spec = spec;
    }

    private void mergeTraits(TraitsSpec another, String property) {
        if (isDifferentTraits(another))
            throw new IllegalArgumentException(String.format("Cannot merge different trait %s and %s for %s",
                    Arrays.toString(traits), Arrays.toString(another.traits), property));
        if (traits.length == 0)
            traits = another.traits;
    }

    private boolean isDifferentTraits(TraitsSpec another) {
        return traits.length != 0 && another.traits.length != 0
                && !new LinkedHashSet<>(asList(traits)).equals(new LinkedHashSet<>(asList(another.traits)));
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
                .traits(traits);
    }

    public void merge(TraitsSpec another, String property) {
        mergeTraits(another, property);
        mergeSpec(another, property);
    }
}
