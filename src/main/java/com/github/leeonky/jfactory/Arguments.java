package com.github.leeonky.jfactory;

interface Arguments {
    <P> P param(String key);

    <P> P param(String key, P defaultValue);

// TODO
//    Arguments params(PropertyChain propertyChain);

    // TODO
//    Arguments params(String);

}