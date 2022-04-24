package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonList;

public class AliasSetStore {
    private final Map<BeanClass<?>, AliasSet> aliasSetMap = new HashMap<>();
    private final Map<Class<?>, AliasSet> specAliasSetMap = new HashMap<>();

    public String evaluate(BeanClass<?> type, String propertyChain, boolean collectionProperties) {
        AliasSet aliasSet = aliasSetMap.get(type);
        PropertyChain chain = PropertyChain.createChain(propertyChain);
        return (aliasSet != null ? aliasSet.evaluateHead(chain, collectionProperties) : chain).toString();
    }

    public <T> String evaluateViaSpec(SpecClassFactory<T> specClassFactory, String propertyChain, boolean collectionProperties) {
        PropertyChain chain = PropertyChain.createChain(propertyChain);
        AliasSet aliasSet = specAliasSetMap.get(specClassFactory.getSpecClass());
        if (aliasSet != null) {
            String evaluated = aliasSet.evaluateHead(chain, collectionProperties).toString();
            if (!evaluated.equals(propertyChain))
                return evaluated;
        }
        return evaluate(specClassFactory.getType(), propertyChain, collectionProperties);
    }

    public AliasSet createSet(BeanClass<?> type) {
        return aliasSetMap.computeIfAbsent(type, key -> new AliasSet());
    }

    public <T, S extends Spec<T>> AliasSet createAliasSet(Class<S> specClass) {
        return specAliasSetMap.computeIfAbsent(specClass, key -> new AliasSet());
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