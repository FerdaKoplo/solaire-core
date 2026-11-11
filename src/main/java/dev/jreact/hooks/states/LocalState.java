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
        if (dev.jreact.dom.Renderer.DEBUG) {
            dev.jreact.dom.JSConsole.log("SET " + this.value + " -> " + newValue);
        }
        this.value = newValue;

        JReact.render();
    }
}
