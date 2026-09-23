package dev.jreact.hooks;

import dev.jreact.hooks.states.LocalState;

public class Hooks {
    private Hooks() {}
    private static HookContext current;

    public static void setCurrent(HookContext ctx) { current = ctx; }
    public static void clearCurrent() { current = null; }

    public static <T> LocalState<T> useState(T initial) {
        if (current == null) throw new IllegalStateException("useState called outside render");
        return current.nextState(initial);
    }
}
