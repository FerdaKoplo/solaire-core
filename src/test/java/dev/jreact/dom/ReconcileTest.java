package dev.jreact.dom;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import dev.jreact.dom.nodes.VNode;
import dev.jreact.patterns.strategy.DomOps;
import dev.jreact.patterns.strategy.FakeDom;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Reconciliation against FakeDom: mount, patch, keyed reorder, grow/shrink,
 * replace, events, component cleanup — the virtual DOM contract without a
 * browser. Renderer routes all DOM work through DomOps; trees are resolved
 * first, like JReact.renderNow does before mount/update.
 */
public class ReconcileTest {

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

    private static FakeDom.FakeNode element(FakeDom fake, String tag) {
        return (FakeDom.FakeNode) fake.createElement(tag);
    }

    private static VNode resolve(VNode tree) {
        Set<String> live = new HashSet<>();
        return Renderer.resolve(tree, "root", live);
    }

    private static FakeDom.FakeNode child(FakeDom.FakeNode n, int i) {
        return n.children.get(i);
    }

    private static String textOf(FakeDom.FakeNode n) {
        return n.getNodeValue();
    }

    @Test
    void mountBuildsTreeInDocumentOrder() {
        FakeDom fake = fake();
        FakeDom.FakeNode container = element(fake, "div");
        Renderer.mount(resolve(El.div(Attr.of().cls("app"), El.span(null, "a"), El.span(null, "b"))), container);

        FakeDom.FakeNode app = child(fake.root, 0);
        assertEquals("div", app.name);
        assertEquals("app", app.attrs.get("class"));
        assertEquals(2, app.children.size());
        assertEquals("a", textOf(child(child(app, 0), 0)));
        assertEquals("b", textOf(child(child(app, 1), 0)));
    }

    @Test
    void patchTouchesOnlyChangedTextAndProps() {
        FakeDom fake = fake();
        FakeDom.FakeNode container = element(fake, "main");
        VNode first = resolve(El.div(Attr.of().cls("card"), El.text("v1")));
        VNode second = resolve(El.div(Attr.of().cls("card"), El.text("v2")));

        Renderer.mount(first, container);
        Renderer.update(container, second, first);

        FakeDom.FakeNode card = child(fake.root, 0);
        assertEquals("card", card.attrs.get("class"));
        assertEquals("v2", textOf(child(card, 0)));
    }

    @Test
    void removesAbsentAttributesAndUnusedChildren() {
        FakeDom fake = fake();
        FakeDom.FakeNode container = element(fake, "div");
        VNode first = resolve(El.div(Attr.of().cls("a").id("gone"), El.text("x"), El.text("y")));
        VNode second = resolve(El.div(Attr.of().cls("a"), El.text("x")));

        Renderer.mount(first, container);
        Renderer.update(container, second, first);

        FakeDom.FakeNode div = child(fake.root, 0);
        assertEquals("a", div.attrs.get("class"));
        assertFalse(div.attrs.containsKey("id"));
        assertEquals(1, div.children.size());
        assertEquals("x", textOf(child(div, 0)));
    }

    @Test
    void reordersKeyedSiblingsInPlace() {
        FakeDom fake = fake();
        FakeDom.FakeNode container = element(fake, "ul");
        VNode first = resolve(El.ul(Attr.of(), El.li(Attr.of().key("a"), "A"), El.li(Attr.of().key("b"), "B")));
        VNode second = resolve(El.ul(Attr.of(), El.li(Attr.of().key("b"), "B"), El.li(Attr.of().key("a"), "A")));

        Renderer.mount(first, container);
        Renderer.update(container, second, first);

        FakeDom.FakeNode ul = child(fake.root, 0);
        assertEquals("B", textOf(child(child(ul, 0), 0)));
        assertEquals("A", textOf(child(child(ul, 1), 0)));
    }

    @Test
    void appendsNewKeyedChildAndDropsRemovedKey() {
        FakeDom fake = fake();
        FakeDom.FakeNode container = element(fake, "div");
        VNode first = resolve(El.ul(Attr.of(), El.li(Attr.of().key("a"), "A")));
        VNode second = resolve(El.ul(Attr.of(), El.li(Attr.of().key("a"), "A"), El.li(Attr.of().key("b"), "B")));

        Renderer.mount(first, container);
        Renderer.update(container, second, first);
        assertEquals(2, child(fake.root, 0).children.size());
        assertEquals("B", textOf(child(child(child(fake.root, 0), 1), 0)));

        Renderer.update(container, first, second);
        FakeDom.FakeNode ul = child(fake.root, 0);
        assertEquals(1, ul.children.size());
        assertEquals("A", textOf(child(child(ul, 0), 0)));
    }

    @Test
    void textReplacedByElementIsRebuiltNotPatched() {
        FakeDom fake = fake();
        FakeDom.FakeNode container = element(fake, "div");
        VNode first = resolve(El.div(null, El.text("str")));
        VNode second = resolve(El.div(null, El.span(null, "el")));

        Renderer.mount(first, container);
        Renderer.update(container, second, first);

        FakeDom.FakeNode div = child(fake.root, 0);
        assertEquals("span", child(div, 0).name);
        assertEquals("el", textOf(child(child(div, 0), 0)));
    }

    @Test
    void eventsAttachAsListeners() {
        FakeDom fake = fake();
        FakeDom.FakeNode container = element(fake, "button");
        Renderer.mount(resolve(El.button("go", () -> {})), container);

        FakeDom.FakeNode button = child(fake.root, 0);
        assertEquals(1, button.listeners.size());
        assertTrue(button.listeners.containsKey("click"));
    }

    @Test
    void componentStateSurvivesPatch() {
        FakeDom fake = fake();
        FakeDom.FakeNode container = element(fake, "div");
        State<Integer>[] holder = new State[1];
        FC.NoProps counter = scope -> {
            holder[0] = scope.useState(0);
            return El.button("+1", () -> holder[0].set(v -> v + 1));
        };
        VNode comp = resolve(El.div(null, El.component(counter, null)));

        Renderer.mount(comp, container);
        holder[0].set(5);
        Renderer.update(container, comp, comp);
        assertEquals(5, holder[0].get());
    }

    @Test
    void unmountRunsComponentCleanup() {
        FakeDom fake = fake();
        FakeDom.FakeNode container = element(fake, "div");
        int[] runs = {0};
        FC.NoProps boom = scope -> {
            scope.onCleanup(() -> runs[0]++);
            return El.span(null, "boom");
        };
        VNode with = resolve(El.div(null, El.component(boom, null)));

        Set<String> live = new HashSet<>();
        Renderer.resolve(with, "root", live); // components tracked under "root"
        Renderer.mount(with, container);
        Renderer.gc(live); // "root" still live -> no-op
        assertEquals(0, runs[0]);

        Renderer.gc(new HashSet<>()); // "root" gone -> instance disposed
        assertEquals(1, runs[0]);
    }

    @Test
    void domOpsDefaultIsFakeDomOnJvm() {
        assertSame(DomOps.get().getClass(), FakeDom.class);
    }

    @Test
    void listHelperAttachesKeysAndRenders() {
        FakeDom fake = fake();
        FakeDom.FakeNode container = element(fake, "ul");
        VNode tree = resolve(El.list("ul",
                java.util.List.of("a", "b", "c"),
                s -> "k-" + s,
                s -> El.h("li", null, s)));

        Renderer.mount(tree, container);
        FakeDom.FakeNode ul = child(fake.root, 0);
        assertEquals(3, ul.children.size());
        assertEquals("a", textOf(child(child(ul, 0), 0)));
        assertEquals("c", textOf(child(child(ul, 2), 0)));
    }

    @Test
    void providerFeedsUseContextAndFallsBackToDefault() {
        FakeDom fake = fake();
        FakeDom.FakeNode container = element(fake, "div");
        Context<String> Theme = Context.of("light");

        FC.NoProps read = scope -> El.h("span", null, scope.useContext(Theme));
        VNode inside = resolve(El.div(null,
                El.provider(Theme, "dark", () -> El.component(read, null)),
                El.component(read, null)));

        Renderer.mount(inside, container);
        FakeDom.FakeNode div = child(fake.root, 0);
        assertEquals("dark", textOf(child(child(div, 0), 0)));
        assertEquals("light", textOf(child(child(div, 1), 0)));
    }

    @Test
    void nestedProviderNullOverridesOuterAndKeyedProviderPreservesKey() {
        FakeDom fake = fake();
        FakeDom.FakeNode container = element(fake, "div");
        Context<String> User = Context.of("guest");

        FC.NoProps read = scope -> {
            String u = scope.useContext(User);
            return El.span(null, u == null ? "null-user" : u);
        };
        VNode keyed = H.provider(User, "alice", "user-k", () -> El.component(read, null));
        assertEquals("user-k", keyed.key());

        VNode tree = resolve(El.provider(User, "admin", () -> El.div(null,
                El.component(read, null),
                El.provider(User, null, () -> El.component(read, null)),
                keyed)));

        Renderer.mount(tree, container);
        FakeDom.FakeNode div = child(fake.root, 0);
        assertEquals("admin", textOf(child(child(div, 0), 0)));
        assertEquals("null-user", textOf(child(child(div, 1), 0)));
        assertEquals("alice", textOf(child(child(div, 2), 0)));
    }
}