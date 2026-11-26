package dev.jreact.patterns.strategy;

/**
 * Strategy for render scheduling across priority lanes.
 *
 * <p>Urgent updates (direct {@code state.set}) schedule via
 * {@link #scheduleUrgent(Runnable)}, while concurrent transitions
 * ({@code JReact.startTransition}, {@code useTransition}, {@code useDeferredValue})
 * schedule via {@link #scheduleTransition(Runnable)} so urgent renders always
 * preempt and flush first.
 *
 * <p>Browser default: {@code setTimeout(0)} for urgent, {@code setTimeout(1)}
 * for transitions so the browser can paint urgent updates first. JVM default:
 * manual/deferred (driven by {@code JReact.flush()} /
 * {@code JReact.flushTransitions()} or a custom scheduler installed via
 * {@link #use(RenderScheduler)}).
 */
public interface RenderScheduler {

    void scheduleUrgent(Runnable task);

    default void scheduleTransition(Runnable task) {
        scheduleUrgent(task);
    }

    static RenderScheduler get() { return Holder.current(); }

    /** Override; pass {@code null} to restore the default. */
    static void use(RenderScheduler next) { Holder.current = next; }

    final class Holder {
        private static RenderScheduler current;

        private Holder() {}

        private static RenderScheduler current() {
            if (current == null) {
                current = EffectScheduler.isBrowser()
                        ? new RenderScheduler() {
                            @Override public void scheduleUrgent(Runnable task) {
                                EffectScheduler.setTimeout(task, 0);
                            }

                            @Override public void scheduleTransition(Runnable task) {
                                EffectScheduler.setTimeout(task, 1);
                            }
                        }
                        : task -> {};
            }
            return current;
        }
    }
}
