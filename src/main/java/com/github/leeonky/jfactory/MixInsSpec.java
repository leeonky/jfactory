package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

import static java.util.Arrays.asList;

public class MixInsSpec {
    public String[] mixIns;
    public String spec;

    public MixInsSpec(String[] mixIns, String spec) {
        this.mixIns = mixIns;
        this.spec = spec;
    }

    private void mergeMixIn(MixInsSpec another, BeanClass<?> hostClass, String property) {
        if (mixIns.length != 0 && another.mixIns.length != 0
                && !new HashSet<>(asList(mixIns)).equals(new HashSet<>(asList(another.mixIns))))
            throw new IllegalArgumentException(String.format("Cannot merge different mix-in %s and %s for %s.%s",
                    Arrays.toString(mixIns), Arrays.toString(another.mixIns), hostClass.getName(), property));
        if (mixIns.length == 0)
            mixIns = another.mixIns;
    }

    private void mergeSpec(MixInsSpec another, BeanClass<?> hostClass, String property) {
        if (spec != null && another.spec != null
                && !Objects.equals(spec, another.spec))
            throw new IllegalArgumentException(String.format("Cannot merge different spec `%s` and `%s` for %s.%s",
                    spec, another.spec, hostClass.getName(), property));
        if (spec == null)
            spec = another.spec;
    }

    public Builder<?> toBuilder(FactorySet factorySet, BeanClass<?> propertyType) {
        return (spec != null ? factorySet.spec(spec) : factorySet.type(propertyType.getType()))
                .mixIn(mixIns);
    }

    public void mergeSubObject(MixInsSpec another, BeanClass<?> hostClass, String property) {
        mergeMixIn(another, hostClass, property);
        mergeSpec(another, hostClass, property);
    }
}
