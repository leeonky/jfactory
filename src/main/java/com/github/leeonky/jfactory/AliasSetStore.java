package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonList;

public class AliasSetStore {
    final Map<BeanClass<?>, AliasSet> aliasSetMap = new HashMap<>();

    public String evaluate(BeanClass<?> type, String propertyChain, boolean collectionProperties) {

        return evaluate(type, PropertyChain.createChain(propertyChain), collectionProperties).toString();
    }

    private PropertyChain evaluate(BeanClass<?> type, PropertyChain chain, boolean collectionProperties) {
        AliasSet aliasSet = aliasSetMap.get(type);
        return aliasSet != null ? aliasSet.evaluateHead(chain, collectionProperties) : chain;
    }

    public AliasSet createSet(BeanClass<?> type) {
        return aliasSetMap.computeIfAbsent(type, key -> new AliasSet());
    }

    public static class AliasSet {
        private final Map<String, String> aliases = new HashMap<>();

        //        TODO refactor
        public PropertyChain evaluateHead(PropertyChain chain, boolean collectionProperties) {
            Object head = chain.head();
            PropertyChain left = chain.removeHead();
            String headString = head.toString();
            boolean intently = headString.endsWith("!");
            if (intently)
                headString = headString.substring(0, headString.length() - 1);
            if (aliases.containsKey(headString)) {
                String property = aliases.get(headString);
                if (property.contains("$") && !(property.contains("[$]") && collectionProperties)) {
                    property = property.replaceFirst("\\$", left.head().toString());
                    left = left.removeHead();
                }
                if (intently)
                    property = property + "!";
                return evaluateHead(PropertyChain.createChain(property), collectionProperties).concat(left);
            }
            return new PropertyChain(singletonList(head)).concat(left);
        }

        public AliasSet alias(String alias, String target) {
            aliases.put(alias, target);
            return this;
        }
    }
}