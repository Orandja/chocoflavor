package net.orandja.chocoflavor.utils;

public class LoopingArray<T> {

    private final T[] array;

    public LoopingArray(T... objects) {
        this.array = objects;
    }

    public T next(T t) {
        return this.array[nextIndex(indexOf(t))];
    }

    public int indexOf(T t) {
        for(int i = 0; i < array.length; i++) {
            if(array[i].equals(t)) {
                return i;
            }
        }
        return -1;
    }

    public int nextIndex(int index) {
        return (index + 1) % this.array.length;
    }

}
