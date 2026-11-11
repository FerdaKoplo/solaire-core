package dev.jreact.hooks.states;
import dev.jreact.dom.JReact;

public class LocalState<T> {
    private T value;

    public LocalState(T initialValue) {
        this.value = initialValue;
    }

    public T get() {
        dev.jreact.dom.JSConsole.log("GET -> " + value + " @ " + System.nanoTime());
        return value;
    }

    public void set(T newValue) {
        dev.jreact.dom.JSConsole.log("SET " + this.value + " -> " + newValue);
        this.value = newValue;

        JReact.render();
    }
}
