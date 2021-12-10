package com.github.leeonky.jfactory;

import com.github.leeonky.util.BeanClass;

import java.util.HashMap;
import java.util.Map;

class AliasSetStore {
    final Map<BeanClass<?>, AliasSet> aliasSetMap = new HashMap<>();

    public void append(Class<?> type, AliasSet aliasSet) {
        aliasSetMap.put(BeanClass.create(type), aliasSet);
    }

    public String evaluate(BeanClass<?> type, String alias) {
        AliasSet aliasSet = aliasSetMap.get(type);
        if (aliasSet != null) {
            return aliasSet.evaluate(alias);
        } else
            return alias;
    }
}