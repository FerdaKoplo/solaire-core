package dev.jreact.hooks.renders;

import org.teavm.jso.browser.Window;

import dev.jreact.hooks.libs.EffectCallback;

import java.util.Arrays;
import java.util.Objects;

public class EffectHook {
    private Object[] lastDeps = null;
    private Runnable cleanup = null;
    private boolean hasRun = false;

    public void bind(EffectCallback effect, Object... deps) {
        boolean depsChanged = !hasRun || !Arrays.equals(lastDeps, deps);

        if (depsChanged) {
            hasRun = true;
            final Object[] currentDeps = Arrays.copyOf(deps, deps.length);

            Window.setTimeout(() -> {
                if (cleanup != null) {
                    cleanup.run();
                }
                cleanup = effect.invoke();
                lastDeps = currentDeps;
            }, 0);
        }
    }

    public void unmount() {
        if (cleanup != null) {
            cleanup.run();
            cleanup = null;
        }
    }
}
