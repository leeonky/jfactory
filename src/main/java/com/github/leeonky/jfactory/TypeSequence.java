package com.github.leeonky.jfactory;

import java.util.HashMap;
import java.util.Map;

class TypeSequence {
    private final Map<Class<?>, Integer> sequences = new HashMap<>();

    public synchronized <T> int generate(Class<T> type) {
        int sequence = sequences.getOrDefault(type, 0) + 1;
        sequences.put(type, sequence);
        return sequence;
    }
}
