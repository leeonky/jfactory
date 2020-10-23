package com.github.leeonky.jfactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

class ProducerList {
    private List<Producer<?>> data = new ArrayList<>();

    public Optional<Producer<?>> query(String index) {
        return Optional.ofNullable(data.get(Integer.valueOf(index)));
    }

    private void fillCollectionWithDefaultValue(int index, Function<Integer, Producer<?>> placeholderFactory) {
        for (int i = data.size(); i <= index; i++)
            data.add(placeholderFactory.apply(i));
    }

    public void set(int intIndex, Producer<?> producer, Function<Integer, Producer<?>> placeholderFactory) {
        fillCollectionWithDefaultValue(intIndex, placeholderFactory);
        data.set(intIndex, producer);
    }

    public Producer<?> get(int index, Function<Integer, Producer<?>> placeholderFactory) {
        fillCollectionWithDefaultValue(index, placeholderFactory);
        return data.get(index);
    }

    public Stream<Producer<?>> stream() {
        return data.stream();
    }
}
