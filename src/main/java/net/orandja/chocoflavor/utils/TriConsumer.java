package net.orandja.chocoflavor.utils;

public interface TriConsumer<T, U, V> {
    void accept(T t, U u, V v);
}