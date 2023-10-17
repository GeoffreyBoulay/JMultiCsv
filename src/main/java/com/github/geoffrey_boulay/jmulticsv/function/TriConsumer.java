package com.github.geoffrey_boulay.jmulticsv.function;

@FunctionalInterface
public interface TriConsumer<A,B,C> {

    void accept(A a, B b, C c);

}
