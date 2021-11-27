package com.github.leeonky.jfactory;

import java.util.Collections;

public class PropertyValue {
    private final String table;

    public PropertyValue(String table) {
        this.table = table;
    }

    public <T> Builder<T> assignTo(String property, Builder<T> builder) {
        String[] lines = table.split(System.lineSeparator());
        if (lines.length > 1) {
            int row = 1;
            return builder.property(property + "[" + (row - 1) + "]." + getCells(lines[0])[1].trim(), getCells(lines[row])[1].trim());
        }
        return builder.property(property, Collections.emptyList());
    }

    private String[] getCells(String line) {
        return line.split("\\|");
    }
}
