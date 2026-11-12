package dev.jreact.hooks;

import dev.jreact.hooks.libs.EffectCallback;
import dev.jreact.hooks.renders.RefHook;
import dev.jreact.hooks.states.LocalState;

public class Hooks {
    private Hooks() {}
    private static HookContext current;

    public static void setCurrent(HookContext ctx) { current = ctx; }
    public static void clearCurrent() { current = null; }

    public static <T> LocalState<T> useState(T initial) {
        if (current == null) throw new IllegalStateException("useState called outside render");
        return current.useState(initial);
    }

    public static void useEffect(Runnable effect, Object... deps) {
        if (current == null) throw new IllegalStateException("useEffect called outside render");
        current.useEffect(() -> { effect.run(); return null; }, deps);
    }

    public static void useEffect(EffectCallback effect, Object... deps) {
        if (current == null) throw new IllegalStateException("useEffect called outside render");
        current.useEffect(effect, deps);
    }

    public static <T> RefHook<T> useRef(T initial) {
        if (current == null) throw new IllegalStateException("useRef called outside render");
        return current.useRef(initial);
    }

    public static <T> T useMemo(java.util.function.Supplier<T> factory, Object... deps) {
        if (current == null) throw new IllegalStateException("useMemo called outside render");
        return current.useMemo(factory, deps);
    }

    public static <T> T useCallback(T callback, Object... deps) {
        if (current == null) throw new IllegalStateException("useCallback called outside render");
        return current.useCallback(callback, deps);
    }
}
