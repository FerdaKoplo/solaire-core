package dev.jreact.hooks.states;

import dev.jreact.dom.JReact;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class GlobalState<T> {
    private T value;

    private final Set<Consumer<T>> listeners = new LinkedHashSet<>();

    public GlobalState(T initialValue) {
        this.value = initialValue;
    }

    public T get() {
        return value;
    }

    public void set(T newValue) {
        this.value = newValue;
        Set<Consumer<T>> snapshot = new LinkedHashSet<>(listeners);
        for (Consumer<T> listener : snapshot) {
            listener.accept(newValue);
        }
        
        JReact.render();
    }

    public Runnable subscribe(Consumer<T> listener) {
        listeners.add(listener);
        
        return () -> listeners.remove(listener);
    }

}
