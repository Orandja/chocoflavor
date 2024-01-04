package net.orandja.chocoflavor.utils;

public class Consumers {
    private Consumers() {}

    public interface Tri<A, B, C> {
        void accept(A a, B b, C c);
    }

    public interface Quad<A, B, C, D> {
        void accept(A a, B b, C c, D d);
    }

    public interface Quin<A, B, C, D, E> {
        void accept(A a, B b, C c, D d, E e);
    }

    public interface Six<A, B, C, D, E, F> {
        void accept(A a, B b, C c, D d, E e, F f);
    }
}
