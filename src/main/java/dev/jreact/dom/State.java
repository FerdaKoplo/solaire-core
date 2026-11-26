package dev.jreact.dom;

import java.util.function.Function;

/**
 * Per-component state cell. Lives as long as the component instance (tree
 * position). {@code get()} is a plain read; {@code set()} schedules an app
 * re-render (urgent by default, or deferred when called inside
 * {@link JReact#startTransition(Runnable)}).
 */
public final class State<T> {
    private T value;
    private T transitionValue;
    private boolean hasTransitionValue;
    private final Runnable onUrgent;
    private final Runnable onTransition;

    State(T initial, Runnable onChange) {
        this(initial, onChange, onChange);
    }

    State(T initial, Runnable onUrgent, Runnable onTransition) {
        this.value = initial;
        this.onUrgent = onUrgent;
        this.onTransition = onTransition;
    }

    public T get() { return value; }

    /** Replace value and request re-render. No-op if equal (same as React bailout). */
    public void set(T next) {
        if (JReact.isInTransition()) {
            if (same(value, next)) {
                if (hasTransitionValue) clearTransition();
                return;
            }
            if (hasTransitionValue && same(transitionValue, next)) return;
            transitionValue = next;
            hasTransitionValue = true;
            JReact.enqueueTransitionState(this);
            onTransition.run();
            return;
        }
        boolean hadTransition = hasTransitionValue;
        clearTransition();
        if (same(value, next) && !hadTransition) return;
        value = next;
        onUrgent.run();
    }

    /** Functional update: next = fn(current). */
    public void set(Function<T, T> fn) {
        T base = JReact.isInTransition() && hasTransitionValue ? transitionValue : value;
        set(fn.apply(base));
    }

    T transitionTarget() { return hasTransitionValue ? transitionValue : value; }

    boolean hasTransitionValue() { return hasTransitionValue; }

    void commitTransition() {
        if (hasTransitionValue) {
            value = transitionValue;
            transitionValue = null;
            hasTransitionValue = false;
        }
    }

    void clearTransition() {
        transitionValue = null;
        hasTransitionValue = false;
    }

    Object rawValue() { return value; }

    static boolean same(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }
}
