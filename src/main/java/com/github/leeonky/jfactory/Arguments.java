package com.github.leeonky.jfactory;

interface Arguments {
    <P> P param(String key);

    <P> P param(String key, P defaultValue);
}