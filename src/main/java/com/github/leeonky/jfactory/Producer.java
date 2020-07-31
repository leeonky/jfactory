package com.github.leeonky.jfactory;

abstract class Producer<T> {
    public abstract T produce(ObjectReference<T> tObjectReference);
}
