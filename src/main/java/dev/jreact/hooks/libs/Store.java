package dev.jreact.hooks.libs;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import dev.jreact.dom.JReact;

public class Store<T> {
   private T state;
    private final Set<Consumer<T>> listeners = new LinkedHashSet<>(); 

    public Store(T initialState) {
        this.state = initialState;
    }

    public T getState() {
        return state;
    }

    public <R> R select(Function<T, R> selector) {
        return selector.apply(state);
    }

    protected void set(Consumer<T> mutator) {
        mutator.accept(this.state);
        notifyListeners();
    }

    protected void set(T newState) {
        this.state = newState;
        notifyListeners();
    }

    private void notifyListeners() {
        Set<Consumer<T>> snapshot = new LinkedHashSet<>(listeners);
        for (Consumer<T> listener : snapshot) {
            listener.accept(this.state);
        }
        
        JReact.render();
    }

    public Runnable subscribe(Consumer<T> listener) {
        listeners.add(listener);
        return () -> listeners.remove(listener);
    }
}
