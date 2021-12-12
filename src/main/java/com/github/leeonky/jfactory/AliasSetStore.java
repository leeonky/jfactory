package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonList;

public class AliasSetStore {
    final Map<BeanClass<?>, AliasSet> aliasSetMap = new HashMap<>();

    public String evaluate(BeanClass<?> type, String propertyChain) {
        return evaluate(type, PropertyChain.createChain(propertyChain)).toString();
    }

    private PropertyChain evaluate(BeanClass<?> type, PropertyChain chain) {
        AliasSet aliasSet = aliasSetMap.get(type);
        return aliasSet != null ? aliasSet.evaluateHead(chain) : chain;
    }

    public AliasSet createSet(BeanClass<?> type) {
        return aliasSetMap.computeIfAbsent(type, key -> new AliasSet());
    }

    public static class AliasSet {
        private final Map<String, String> aliases = new HashMap<>();

        public PropertyChain evaluateHead(PropertyChain chain) {
            Object head = chain.head();
            PropertyChain left = chain.removeHead();
            if (aliases.containsKey(head)) {
                String property = aliases.get(head);
                if (property.contains("$")) {
                    property = property.replaceFirst("\\$", left.head().toString());
                    left = left.removeHead();
                }
                return evaluateHead(PropertyChain.createChain(property)).concat(left);
            }
            return new PropertyChain(singletonList(head)).concat(left);
        }

        public AliasSet alias(String alias, String target) {
            aliases.put(alias, target);
            return this;
        }
    }
}