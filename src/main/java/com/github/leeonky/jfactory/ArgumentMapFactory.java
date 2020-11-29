package com.github.leeonky.jfactory;

public class ArgumentMapFactory {
    public static ArgumentMap arg(String key, Object value) {
        ArgumentMap argumentMap = new ArgumentMap();
        argumentMap.put(key, value);
        return argumentMap;
    }
}
