package net.orandja.chocoflavor.utils;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.function.Consumer;

public class ObservableValue<T> {

    protected List<Consumer<T>> observers = Lists.newArrayList();
    protected T value;
    public void observe(Consumer<T> observer) {
        this.observers.add(observer);
    }

    protected void dispatch() {
        this.observers.forEach(observer -> observer.accept(this.value));
    }

    public T getValue() {
        return this.value;
    }

    public void setValue(T value) {
        this.value = value;
        this.dispatch();
    }

    public void setValueSilently(T value) {
        this.value = value;
    }
}
