package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.HashMap;
import java.util.Map;

public class AliasSetStore {
    final Map<BeanClass<?>, AliasSet> aliasSetMap = new HashMap<>();

    public String evaluate(BeanClass<?> type, String alias) {
        AliasSet aliasSet = aliasSetMap.get(type);
        if (aliasSet != null) {
            return aliasSet.evaluate(alias);
        } else
            return alias;
    }

    public AliasSet createSet(BeanClass<?> type) {
        return aliasSetMap.computeIfAbsent(type, k -> new AliasSet());
    }

    public static class AliasSet {
        private final Map<String, String> aliases = new HashMap<>();

        public String evaluate(String alias) {
            return aliases.getOrDefault(alias, alias);
        }

        public AliasSet alias(String alias, String target) {
            aliases.put(alias, target);
            return this;
        }
    }
}