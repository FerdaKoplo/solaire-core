package dev.jreact.dom;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import dev.jreact.dom.nodes.VNode;
import dev.jreact.patterns.strategy.DomOps;
import dev.jreact.patterns.strategy.FakeDom;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Reconciliation cost, measured deterministically via FakeDom op counters
 * (no wall-clock flakiness): identical trees cost zero mutations, a text
 * change costs one setText per changed node, and growth stays linear.
 */
public class ReconcilePerfTest {

    @AfterEach
    void cleanup() {
        DomOps.use(null);
        Renderer.gc(new HashSet<>());
    }

    private static FakeDom fake() {
        FakeDom f = new FakeDom();
        DomOps.use(f);
        return f;
    }

    private static FakeDom.FakeNode container(FakeDom f) {
        return (FakeDom.FakeNode) f.createElement("div");
    }

    private static void reset(FakeDom f) {
        f.creates = f.appends = f.inserts = f.removals = 0;
        f.textSets = f.attrSets = f.attrRemovals = f.listenerAdds = 0;
    }

    private static VNode resolve(VNode tree) {
        return Renderer.resolve(tree, "root", new HashSet<>());
    }

    private static FakeDom.FakeNode mount(FakeDom f, VNode tree) {
        FakeDom.FakeNode c = container(f);
        Renderer.mount(resolve(tree), c);
        return c;
    }

    private static VNode rows(int n) {
        List<VNode> kids = new ArrayList<>(n);
        for (int i = 0; i < n; i++) kids.add(El.li(Attr.of(), "row-" + i));
        return El.ul(null, kids);
    }

    @Test
    void identicalRerenderCostsZeroDomOps() {
        FakeDom f = fake();
        VNode tree = rows(50);
        FakeDom.FakeNode c = mount(f, tree);
        reset(f);

        Renderer.update(c, resolve(tree), resolve(tree));
        assertEquals(0, f.creates);
        assertEquals(0, f.appends);
        assertEquals(0, f.inserts);
        assertEquals(0, f.removals);
        assertEquals(0, f.textSets);
        assertEquals(0, f.attrSets);
    }

    @Test
    void oneTextChangeCostsOneTextSet() {
        FakeDom f = fake();
        VNode first = rows(50);
        FakeDom.FakeNode c = mount(f, first);

        List<VNode> kids = new ArrayList<>();
        for (int i = 0; i < 50; i++) kids.add(El.li(Attr.of(), i == 0 ? "row-0!" : ("row-" + i)));
        VNode second = El.ul(null, kids);
        reset(f);

        Renderer.update(c, resolve(second), resolve(first));
        assertEquals(1, f.textSets);
        assertEquals(0, f.creates);
        assertEquals(0, f.attrSets);
    }

    @Test
    void growingKeyedListAddsOnlyNewNodes() {
        FakeDom f = fake();
        List<VNode> first = new ArrayList<>();
        for (int i = 0; i < 100; i++) first.add(El.li(Attr.of().key("k" + i), "v" + i));
        VNode mount = El.ul(null, first);
        FakeDom.FakeNode c = mount(f, mount);

        List<VNode> next = new ArrayList<>(first);
        next.add(El.li(Attr.of().key("new"), "extra"));
        reset(f);

        Renderer.update(c, resolve(El.ul(null, next)), resolve(mount));
        assertEquals(2, f.creates, "new li element is itself created");
        assertEquals(0, f.textSets);
        assertEquals(1, f.appends);
    }

    @Test
    void keyedReorderCostsMovesNotRecreates() {
        FakeDom f = fake();
        List<VNode> first = new ArrayList<>();
        for (int i = 0; i < 200; i++) first.add(El.li(Attr.of().key("k" + i), "v" + i));
        VNode a = El.ul(null, first);
        FakeDom.FakeNode c = mount(f, a);

        List<VNode> reversed = new ArrayList<>(first);
        java.util.Collections.reverse(reversed);
        reset(f);
        Renderer.update(c, resolve(El.ul(null, reversed)), resolve(a));

        assertEquals(0, f.creates, "existing rows must not be rebuilt");
        assertEquals(199, f.inserts, "200 keyed rows reversed = 199 adjacent moves");
    }

    @Test
    void growthStaysLinearInDomOps() {
        double small = domOps(fake(), 400);
        double large = domOps(fake(), 1600);
        // ops = c*n + b: 4x rows must not blow up super-linearly...
        assertTrue(large <= small * 4 + 64, "ops 400=" + small + " 1600=" + large);
        // ...and must actually scale with tree size, not collapse to a constant.
        assertTrue(large > small, "ops 400=" + small + " 1600=" + large);
    }

    private static double domOps(FakeDom f, int rows) {
        reset(f);
        mount(f, rows(rows));
        return f.creates + f.appends;
    }
}