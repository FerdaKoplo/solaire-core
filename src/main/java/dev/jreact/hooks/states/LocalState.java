package dev.jreact.hooks.states;
import dev.jreact.dom.JReact;

public class LocalState<T> {
    private T value;

    public LocalState(T initialValue) {
        this.value = initialValue;
    }

    public T get() {
        return value;
    }

    public void set(T newValue) {
        this.value = newValue;

        JReact.render();
    }
}
