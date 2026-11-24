package dev.jreact.patterns.strategy;

import org.teavm.jso.browser.TimerHandler;
import org.teavm.jso.browser.Window;

/**
 * Strategy for effect timing. Browser: after paint (setTimeout 0). JVM/tests:
 * inline. Selected by {@link #get()}; {@link #use} overrides (pass null to
 * restore the default).
 */
public interface EffectScheduler {

    void schedule(Runnable body);

    public static void setTimeout(Runnable body, int delayMs) {
        Window.setTimeout((TimerHandler) body::run, delayMs);
    }

    static EffectScheduler get() { return Holder.current(); }

    /** Override; pass null to restore the default (async in browser, inline on JVM). */
    static void use(EffectScheduler next) { Holder.current = next; }

    static boolean isBrowser() {
        return System.getProperty("java.vm.name", "").toLowerCase().contains("machine")
                && Window.current() != null;
    }

    final class Holder {
        private static EffectScheduler current;

        private Holder() {}

        private static EffectScheduler current() {
            if (current == null) current = isBrowser() ? body -> setTimeout(body, 0) : Runnable::run;
            return current;
        }
    }
}
