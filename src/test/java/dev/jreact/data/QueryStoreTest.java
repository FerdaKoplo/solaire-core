package dev.jreact.data;

import dev.jreact.dom.El;
import dev.jreact.dom.JReact;
import dev.jreact.dom.State;
import dev.jreact.patterns.strategy.DomOps;
import dev.jreact.patterns.strategy.FakeDom;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

/** Query.useStore swaps cache + clock (null restores a fresh default). */
public class QueryStoreTest {

    @AfterEach
    void cleanup() {
        JReact.unmount();
        DomOps.use(null);
        Query.useStore(null);
    }

    @Test
    void useStoreControlsClockForStaleness() {
        long t0 = 1000;
        AtomicLong now = new AtomicLong(t0);
        Query.useStore(new Query.Store(now::get));

        Query.Entry<Integer> e = Query.use("q", (onData, onError) -> onData.accept(42));
        assertEquals(42, e.data());
        assertFalse(e.isStale());

        now.set(t0 + Query.DEFAULT_STALE_MS + 1);   // strictly past staleness
        assertTrue(e.isStale());
    }

    @Test
    void nullRestoresFreshDefaultStore() {
        Query.useStore(new Query.Store(() -> 0));
        Query.useStore(null);
        assertNotNull(Query.getStore());
    }

    @Test
    void useSubscribesOnceAcrossRerendersAndCleansUpOnKeyChangeAndUnmount() {
        FakeDom fake = new FakeDom();
        DomOps.use(fake);
        Query.useStore(new Query.Store(() -> 1000L));
        FakeDom.FakeNode container = (FakeDom.FakeNode) fake.createElement("div");

        State<String>[] keyState = new State[1];
        JReact.mount(scope -> {
            keyState[0] = scope.useState("users");
            Query.Entry<String> entry = Query.use(keyState[0].get(), (onData, onError) -> onData.accept("ok"));
            return El.span(null, entry.data());
        }, container);

        Query.Entry<?> users = Query.peek("users");
        assertNotNull(users);
        assertEquals(1, users.observerCount());

        // Extra re-renders with the same key must not pile up duplicate observers
        JReact.flush();
        JReact.flush();
        assertEquals(1, users.observerCount());

        // Switching query key unsubscribes old entry and subscribes once to new entry
        keyState[0].set("posts");
        JReact.flush();
        Query.Entry<?> posts = Query.peek("posts");
        assertNotNull(posts);
        assertEquals(0, users.observerCount());
        assertEquals(1, posts.observerCount());

        // Unmounting removes the active subscription
        JReact.unmount();
        assertEquals(0, posts.observerCount());
    }

}
