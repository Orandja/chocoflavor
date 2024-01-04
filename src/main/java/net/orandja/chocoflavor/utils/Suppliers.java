package net.orandja.chocoflavor.utils;

public class Suppliers {
    private Suppliers() {}

    public interface Bi<S, A, B> {
        S get(A a, B b);
    }

    public interface Tri<S, A, B, C> {
        S get(A a, B b, C c);
    }

    public interface Quad<S, A, B, C, D> {
        S get(A a, B b, C c, D d);
    }

    public interface Quin<S, A, B, C, D, E> {
        S get(A a, B b, C c, D d, E e);
    }

    public interface Six<S, A, B, C, D, E, F> {
        S get(A a, B b, C c, D d, E e, F f);
    }
}
