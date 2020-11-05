package com.github.leeonky.jfactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

class TypeSequence {
    private final Map<Class<?>, AtomicInteger> sequences = new HashMap<>();

    public synchronized <T> int generate(Class<T> type) {
        return sequences.computeIfAbsent(type, k -> new AtomicInteger(0)).incrementAndGet();
    }
}
