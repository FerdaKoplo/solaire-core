package dev.jreact.hooks;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import dev.jreact.hooks.renders.RefHook;
import dev.jreact.hooks.states.LocalState;

public class HookContextTest {

    @Test
    public void stateRetainedAcrossRenders() {
        HookContext ctx = new HookContext();

        ctx.startRender();
        Hooks.setCurrent(ctx);
        LocalState<Integer> s = Hooks.useState(0);
        Hooks.clearCurrent();
        assertEquals(0, s.get());

        s.set(5);

        ctx.startRender();
        Hooks.setCurrent(ctx);
        LocalState<Integer> s2 = Hooks.useState(99); // same slot, initial ignored
        Hooks.clearCurrent();
        assertEquals(5, s2.get());
    }

    @Test
    public void hookOrderMustBeStable() {
        HookContext ctx = new HookContext();

        ctx.startRender();
        Hooks.setCurrent(ctx);
        Hooks.useState(0);
        Hooks.useRef("a");
        Hooks.clearCurrent();

        ctx.startRender();
        Hooks.setCurrent(ctx);
        // same count, different types at slot 1 -> rules-of-hooks violation
        assertThrows(ClassCastException.class, () -> {
            Hooks.useState(1);
            Hooks.useState(2); // slot 1 holds RefHook, now read as LocalState
        });
        Hooks.clearCurrent();
    }

    @Test
    public void memoRespectsDeps() {
        HookContext ctx = new HookContext();
        int[] calls = {0};

        ctx.startRender();
        Hooks.setCurrent(ctx);
        String m1 = Hooks.useMemo(() -> "v" + calls[0]++, new Object[]{"stable"});
        Hooks.clearCurrent();
        assertEquals("v0", m1);

        ctx.startRender();
        Hooks.setCurrent(ctx);
        String m2 = Hooks.useMemo(() -> "v" + calls[0]++, new Object[]{"stable"});
        String m3 = Hooks.useMemo(() -> "v" + calls[0]++, new Object[]{"changed"});
        Hooks.clearCurrent();
        assertEquals("v0", m2);   // deps same -> cached
        assertEquals("v1", m3);   // deps changed -> recomputed
        assertEquals(2, calls[0]);
    }

    @Test
    public void unmountWithNoEffectsIsNoop() {
        new HookContext().unmount();
    }

    @Test
    public void hooksOutsideRenderThrow() {
        assertThrows(IllegalStateException.class, () -> Hooks.useState(0));
        assertThrows(IllegalStateException.class, () -> Hooks.useRef("a"));
        assertThrows(IllegalStateException.class, () -> Hooks.useMemo(() -> 1));
        assertThrows(IllegalStateException.class, () -> Hooks.useCallback((Runnable) () -> {}));
    }
}
