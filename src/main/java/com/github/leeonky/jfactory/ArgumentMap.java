package com.github.leeonky.jfactory;

import java.util.LinkedHashMap;

public class ArgumentMap extends LinkedHashMap<String, Object> {
    public ArgumentMap arg(String key, Object value) {
        ArgumentMap argumentMap = new ArgumentMap();
        argumentMap.putAll(this);
        argumentMap.put(key, value);
        return argumentMap;
    }
}
