package dev.jreact.hooks;

import dev.jreact.dom.JReact;

public class State<T> {
    private T value;

    public State(T initialValue) {
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
