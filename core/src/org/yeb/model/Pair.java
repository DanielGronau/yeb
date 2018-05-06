package org.yeb.model;

public class Pair<A, B> {
    public final A _1;
    public final B _2;


    public Pair(A a, B b) {
        _1 = a;
        _2 = b;
    }

    public static <A, B> Pair<A, B> of(A a, B b) {
        return new Pair<>(a, b);
    }
}
