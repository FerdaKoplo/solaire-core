package dev.jreact.dom;

import dev.jreact.dom.nodes.VNode;
import dev.jreact.patterns.strategy.DomOps;
import dev.jreact.patterns.strategy.FakeDom;
import dev.jreact.patterns.strategy.RenderScheduler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Concurrent mode tests: priority lanes, startTransition, useTransition,
 * useDeferredValue, urgent preemption, and RenderScheduler seam.
 */
public class ConcurrentTest {

    @AfterEach
    void cleanup() {
        JReact.unmount();
        RenderScheduler.use(null);
        DomOps.use(null);
    }

    private static FakeDom fake() {
        FakeDom f = new FakeDom();
        DomOps.use(f);
        return f;
    }

    private static String textAt(FakeDom.FakeNode container, int... path) {
        FakeDom.FakeNode n = container;
        for (int idx : path) n = n.children.get(idx);
        return n.name;
    }

    @Test
    void urgentUpdatesFlushBeforeDeferredTransitions() {
        FakeDom fake = fake();
        FakeDom.FakeNode container = (FakeDom.FakeNode) fake.createElement("div");

        State<String>[] refs = new State[2];
        JReact.mount(scope -> {
            refs[0] = scope.useState("a");
            refs[1] = scope.useState("a");
            return El.div(null,
                    El.span(null, refs[0].get()),
                    El.span(null, refs[1].get()));
        }, container);

        refs[0].set("ab"); // urgent
        JReact.startTransition(() -> refs[1].set("ab")); // transition

        JReact.flush(); // urgent pass only
        assertEquals("ab", textAt(container, 0, 0, 0));
        assertEquals("a", textAt(container, 0, 1, 0));

        JReact.flushTransitions(); // transition pass commits
        assertEquals("ab", textAt(container, 0, 0, 0));
        assertEquals("ab", textAt(container, 0, 1, 0));
    }

    @Test
    void urgentUpdateSupersedesStaleTransition() {
        FakeDom fake = fake();
        FakeDom.FakeNode container = (FakeDom.FakeNode) fake.createElement("div");

        State<String>[] ref = new State[1];
        List<String> committed = new ArrayList<>();
        JReact.mount(scope -> {
            ref[0] = scope.useState("init");
            committed.add(ref[0].get());
            return El.span(null, ref[0].get());
        }, container);

        JReact.startTransition(() -> ref[0].set("slow-1"));
        JReact.startTransition(() -> ref[0].set("slow-2"));
        ref[0].set("urgent-now");
        JReact.flush();
        assertEquals("urgent-now", textAt(container, 0, 0));

        // Stale transition was cleared by the urgent override
        JReact.flushTransitions();
        assertEquals("urgent-now", textAt(container, 0, 0));
        assertFalse(committed.contains("slow-1"));
        assertFalse(committed.contains("slow-2"));
    }

    @Test
    void useTransitionTracksPendingState() {
        FakeDom fake = fake();
        FakeDom.FakeNode container = (FakeDom.FakeNode) fake.createElement("div");

        State<Integer>[] count = new State[1];
        JReact.Transition[] tr = new JReact.Transition[1];
        JReact.mount(scope -> {
            count[0] = scope.useState(0);
            tr[0] = scope.useTransition();
            return El.div(null,
                    El.span(null, tr[0].isPending() ? "pending" : "idle"),
                    El.span(null, String.valueOf(count[0].get())));
        }, container);

        assertEquals("idle", textAt(container, 0, 0, 0));
        assertEquals("0", textAt(container, 0, 1, 0));

        tr[0].start(() -> count[0].set(1));
        assertTrue(tr[0].isPending());

        JReact.flush(); // urgent frame shows pending=true while count is still 0
        assertEquals("pending", textAt(container, 0, 0, 0));
        assertEquals("0", textAt(container, 0, 1, 0));

        JReact.flushTransitions(); // transition commits count=1 and clears pending
        assertFalse(tr[0].isPending());
        assertEquals("idle", textAt(container, 0, 0, 0));
        assertEquals("1", textAt(container, 0, 1, 0));
    }

    @Test
    void useTransitionNoOpDoesNotStayPending() {
        FakeDom fake = fake();
        FakeDom.FakeNode container = (FakeDom.FakeNode) fake.createElement("div");

        State<Integer>[] count = new State[1];
        JReact.Transition[] tr = new JReact.Transition[1];
        JReact.mount(scope -> {
            count[0] = scope.useState(5);
            tr[0] = scope.useTransition();
            return El.span(null, String.valueOf(count[0].get()));
        }, container);

        tr[0].start(() -> count[0].set(5));
        assertFalse(tr[0].isPending());
    }

    @Test
    void useDeferredValueLagsBehindUrgentRenderAndCatchesUp() {
        FakeDom fake = fake();
        FakeDom.FakeNode container = (FakeDom.FakeNode) fake.createElement("div");

        State<String>[] query = new State[1];
        JReact.mount(scope -> {
            query[0] = scope.useState("tea");
            String deferred = scope.useDeferredValue(query[0].get());
            return El.div(null,
                    El.span(null, query[0].get()),
                    El.span(null, deferred));
        }, container);

        assertEquals("tea", textAt(container, 0, 0, 0));
        assertEquals("tea", textAt(container, 0, 1, 0));

        query[0].set("matcha");
        JReact.flush();
        assertEquals("matcha", textAt(container, 0, 0, 0));
        assertEquals("tea", textAt(container, 0, 1, 0));

        JReact.flushTransitions();
        assertEquals("matcha", textAt(container, 0, 0, 0));
        assertEquals("matcha", textAt(container, 0, 1, 0));
    }

    @Test
    void renderSchedulerPreemptsTransitionWhenUrgentStillPending() {
        FakeDom fake = fake();
        FakeDom.FakeNode container = (FakeDom.FakeNode) fake.createElement("div");

        List<Runnable> urgentQ = new ArrayList<>();
        List<Runnable> transitionQ = new ArrayList<>();
        RenderScheduler.use(new RenderScheduler() {
            @Override public void scheduleUrgent(Runnable task) { urgentQ.add(task); }
            @Override public void scheduleTransition(Runnable task) { transitionQ.add(task); }
        });

        State<String>[] refs = new State[2];
        JReact.mount(scope -> {
            refs[0] = scope.useState("u0");
            refs[1] = scope.useState("t0");
            return El.div(null, El.span(null, refs[0].get()), El.span(null, refs[1].get()));
        }, container);

        refs[0].set("u1");
        JReact.startTransition(() -> refs[1].set("t1"));

        assertEquals(1, urgentQ.size());
        assertEquals(1, transitionQ.size());

        // Even if the transition timer fires first while urgent is still pending,
        // transition yields and re-queues instead of committing before urgent!
        transitionQ.remove(0).run();
        assertEquals("u0", textAt(container, 0, 0, 0));
        assertEquals("t0", textAt(container, 0, 1, 0));
        assertEquals(1, transitionQ.size());

        urgentQ.remove(0).run();
        assertEquals("u1", textAt(container, 0, 0, 0));
        assertEquals("t0", textAt(container, 0, 1, 0));

        transitionQ.remove(0).run();
        assertEquals("u1", textAt(container, 0, 0, 0));
        assertEquals("t1", textAt(container, 0, 1, 0));
    }
}
