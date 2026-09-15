package dev.jreact.hooks.renders;

public class RefHook<T> {
    public T current;

    public RefHook(T initialValue) {
        this.current = initialValue;
    }

    public RefHook() {
        this.current = null;
    }
}
