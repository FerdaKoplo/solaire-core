package dev.jreact.patterns.strategy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** EffectScheduler.use swaps scheduling (tests can queue manually); null restores default. */
public class EffectSchedulerTest {

    @AfterEach
    void cleanup() { EffectScheduler.use(null); }

    @Test
    void useRoutesSchedulingAndNullRestoresDefault() {
        List<Runnable> queued = new ArrayList<>();
        EffectScheduler.use(queued::add);
        EffectScheduler.get().schedule(() -> {});
        assertEquals(1, queued.size());

        EffectScheduler.use(null);  // default: inline on JVM
        EffectScheduler.get().schedule(() -> {});
        assertEquals(1, queued.size());
    }

}
