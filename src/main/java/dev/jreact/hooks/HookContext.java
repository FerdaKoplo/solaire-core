package dev.jreact.hooks;

import java.util.ArrayList;
import java.util.List;

import dev.jreact.hooks.states.LocalState;

public class HookContext {
    private final List<Object> slots = new ArrayList<>();
    private int cursor = 0;

    public void startRender() { cursor = 0; }

    @SuppressWarnings("unchecked")
    public <T> LocalState<T> nextState(T initial) {
        if (cursor == slots.size()) slots.add(new LocalState<>(initial));
        return (LocalState<T>) slots.get(cursor++);
    }
}
