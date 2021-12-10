package com.github.leeonky.jfactory;

import java.util.HashMap;
import java.util.Map;

public class AliasSet {
    private final Map<String, String> aliases = new HashMap<>();

    public AliasSet(String alias, String target) {
        aliases.put(alias, target);
    }

    public String evaluate(String alias) {
        return aliases.getOrDefault(alias, alias);
    }
}
