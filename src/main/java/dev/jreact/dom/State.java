package dev.jreact.dom;

import java.util.function.Function;

/**
 * Per-component state cell. Lives as long as the component instance (tree
 * position). get() is a plain read; set() schedules an app re-render.
 *
 * One mental model for every kind of state (local, effect, memo): they are
 * all cells in the component's slot list, addressed by call order.
 */
public final class State<T> {
    private T value;
    private final Runnable onChange;

    State(T initial, Runnable onChange) {
        this.value = initial;
        this.onChange = onChange;
    }

    public T get() { return value; }

    /** Replace value and request re-render. No-op if equal (same as React bailout). */
    public void set(T next) {
        if (same(value, next)) return;
        value = next;
        onChange.run();
    }

    /** Functional update: next = fn(current). */
    public void set(Function<T, T> fn) { set(fn.apply(value)); }

    Object rawValue() { return value; }

    static boolean same(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }
}
