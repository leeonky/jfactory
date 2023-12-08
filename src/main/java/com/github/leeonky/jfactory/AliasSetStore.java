package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.HashMap;
import java.util.Map;

import static com.github.leeonky.jfactory.PropertyChain.propertyChain;
import static java.util.Collections.singletonList;

public class AliasSetStore {
    private final Map<BeanClass<?>, AliasSet> aliasSetMap = new HashMap<>();
    private final Map<Class<?>, AliasSet> specAliasSetMap = new HashMap<>();

    public <T> String resolve(ObjectFactory<T> objectFactory, String propertyChain, boolean collectionProperties) {
        if (objectFactory instanceof SpecClassFactory) {
            String evaluated = specAliasSet(((SpecClassFactory<T>) objectFactory).getSpecClass())
                    .resolve(propertyChain(propertyChain), collectionProperties).toString();
            if (!evaluated.equals(propertyChain))
                return evaluated;
            return resolve(objectFactory.getBase(), propertyChain, collectionProperties);
        }
        return aliasSet(objectFactory.getType()).resolve(propertyChain(propertyChain), collectionProperties).toString();
    }

    public AliasSet aliasSet(BeanClass<?> type) {
        return aliasSetMap.computeIfAbsent(type, key -> new AliasSet());
    }

    public <T, S extends Spec<T>> AliasSet specAliasSet(Class<S> specClass) {
        return specAliasSetMap.computeIfAbsent(specClass, key -> new AliasSet());
    }

    public static class AliasSet {
        private final Map<String, String> aliases = new HashMap<>();

        //        TODO refactor
        public PropertyChain resolve(PropertyChain chain, boolean collectionProperties) {
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
                return resolve(propertyChain(property), collectionProperties).concat(left);
            }
            return new PropertyChain(singletonList(head)).concat(left);
        }

        public AliasSet alias(String alias, String target) {
            aliases.put(alias, target);
            return this;
        }
    }
}