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
        return aliasSet != null ? aliasSet.evaluate(chain) : chain;
    }

    public AliasSet createSet(BeanClass<?> type) {
        return aliasSetMap.computeIfAbsent(type, AliasSet::new);
    }

    public class AliasSet {
        private final BeanClass<?> type;
        private final Map<String, String> aliases = new HashMap<>();

        public AliasSet(BeanClass<?> type) {
            this.type = type;
        }

        public PropertyChain evaluate(PropertyChain chain) {
            Object head = chain.head();
            PropertyChain headChain = aliases.containsKey(head) ? PropertyChain.createChain(aliases.get(head))
                    : new PropertyChain(singletonList(head));
            return chain.isSingle() ? headChain : headChain.concat(AliasSetStore.this.evaluate(
                    type.getPropertyChainReader(headChain.toString()).getType(), chain.removeHead()));
//                TODO should use property chain writer
        }

        public AliasSet alias(String alias, String target) {
            aliases.put(alias, target);
            return this;
        }
    }
}