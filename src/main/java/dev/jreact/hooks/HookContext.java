package dev.jreact.hooks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import dev.jreact.hooks.libs.EffectCallback;
import dev.jreact.hooks.renders.CallbackHook;
import dev.jreact.hooks.renders.EffectHook;
import dev.jreact.hooks.renders.RefHook;
import dev.jreact.hooks.states.LocalState;

public class HookContext {
    private final List<Object> slots = new ArrayList<>();
    private int cursor = 0;

    public void startRender() { cursor = 0; }

    @SuppressWarnings("unchecked")
    private <T> T slot(Object seed) {
        if (cursor == slots.size()) slots.add(seed);
        return (T) slots.get(cursor++);
    }

    public <T> LocalState<T> useState(T initial) {
        return slot(new LocalState<>(initial));
    }

    public <T> RefHook<T> useRef(T initial) {
        return slot(new RefHook<>(initial));
    }

    public void useEffect(EffectCallback effect, Object... deps) {
        EffectHook hook = this.<EffectHook>slot(new EffectHook());
        hook.bind(effect, deps);
    }

    public <T> T useMemo(Supplier<T> factory, Object... deps) {
        MemoBox<T> box = this.<MemoBox<T>>slot(new MemoBox<>());
        return box.memo(factory, deps);
    }

    public <T> T useCallback(T callback, Object... deps) {
        CallbackHook<T> hook = this.<CallbackHook<T>>slot(new CallbackHook<>());
        return hook.bind(callback, deps);
    }

    public void unmount() {
        for (Object s : slots) {
            if (s instanceof EffectHook e) e.unmount();
        }
    }

    private static final class MemoBox<T> {
        private T value;
        private Object[] lastDeps;
        private boolean init;

        T memo(Supplier<T> factory, Object... deps) {
            if (!init || !Arrays.equals(lastDeps, deps)) {
                value = factory.get();
                lastDeps = Arrays.copyOf(deps, deps.length);
                init = true;
            }
            return value;
        }
    }
}
