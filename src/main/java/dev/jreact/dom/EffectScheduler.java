package dev.jreact.dom;

import org.teavm.jso.browser.TimerHandler;
import org.teavm.jso.browser.Window;

/**
 * Strategy for effect timing. Browser: after paint (setTimeout 0). JVM/tests:
 * inline. Selected by {@link #get()}.
 */
public interface EffectScheduler {

    void schedule(Runnable body);

    public static void setTimeout(Runnable body, int delayMs) {
        Window.setTimeout((TimerHandler) body::run, delayMs);
    }

    static EffectScheduler get() {
        if (isBrowser()) return body -> setTimeout(body, 0);
        return Runnable::run;
    }

    static boolean isBrowser() {
        return System.getProperty("java.vm.name", "").toLowerCase().contains("machine")
                && Window.current() != null;
    }
}
