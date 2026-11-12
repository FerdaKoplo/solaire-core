package dev.jreact.hooks.renders;

import org.teavm.jso.browser.Window;

import dev.jreact.hooks.libs.EffectCallback;

import java.util.Arrays;

public class EffectHook {
    private Object[] lastDeps = null;
    private Runnable cleanup = null;
    private boolean hasRun = false;
    private boolean cancelled = false;

    public void bind(EffectCallback effect, Object... deps) {
        boolean depsChanged = !hasRun || !Arrays.equals(lastDeps, deps);

        if (depsChanged) {
            hasRun = true;
            final Object[] currentDeps = Arrays.copyOf(deps, deps.length);

            Window.setTimeout(() -> {
                if (cancelled) return;
                if (cleanup != null) {
                    cleanup.run();
                }
                // Commit deps BEFORE running the effect: the effect body may
                // set() state, which re-renders synchronously. The re-render's
                // bind() then sees lastDeps already equal to currentDeps and
                // does not reschedule. (Same contract as React's post-commit
                // deps snapshot; prevents re-entrant effect storms.)
                lastDeps = currentDeps;
                cleanup = effect.invoke();
            }, 0);
        }
    }

    public void unmount() {
        cancelled = true;
        if (cleanup != null) {
            cleanup.run();
            cleanup = null;
        }
    }
}
