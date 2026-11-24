package dev.jreact.data;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

/** Query.useStore swaps cache + clock (null restores a fresh default). */
public class QueryStoreTest {

    @AfterEach
    void cleanup() { Query.useStore(null); }

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

}
