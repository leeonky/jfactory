package com.github.leeonky.jfactory;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

import static java.lang.String.format;

public class PropertyValue {
    private final String table;

    public PropertyValue(String table) {
        this.table = table;
    }

    private static <T> BinaryOperator<T> notAllowParallelReduce() {
        return (o1, o2) -> {
            throw new IllegalStateException("Not allow parallel here!");
        };
    }

    private static <T, R> R reduceWithIndex(Stream<T> stream, R input, TriFunction<Integer, R, T, R> triFunction) {
        AtomicInteger index = new AtomicInteger(0);
        return stream.reduce(input, (reducer, line) -> triFunction.apply(index.getAndIncrement(), reducer, line),
                notAllowParallelReduce());
    }

    public <T> Builder<T> assignTo(String property, Builder<T> builder) {
        String[] lines = table.split(System.lineSeparator());
        if (lines.length > 1) {
            String[] headers = getCells(lines[0]);
            return reduceWithIndex(Stream.of(lines).skip(1), builder, (rowIndex, rowReduceBuilder, row) -> {
                String[] cells = getCells(row);
                if (cells.length != headers.length)
                    throw new IllegalArgumentException("Invalid table at row: " + rowIndex + ", different size of cells and headers.");
                return reduceWithIndex(Stream.of(headers), rowReduceBuilder, (columnIndex, columnReduceBuilder, cell) ->
                        columnReduceBuilder.property(format("%s[%d].%s", property, rowIndex, headers[columnIndex]), cells[columnIndex]));
            });
        }
        return builder.property(property, Collections.emptyList());
    }

    private String[] getCells(String line) {
        return Stream.of(line.split("\\|")).skip(1).map(String::trim).toArray(String[]::new);
    }

    @FunctionalInterface
    interface TriFunction<A, B, C, R> {
        R apply(A a, B b, C c);
    }
}
