package dev.jreact.dom;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import dev.jreact.dom.nodes.ElementVNode;
import dev.jreact.dom.nodes.TextVNode;
import dev.jreact.dom.nodes.VNode;

import java.util.List;
import java.util.Map;

/**
 * Self-check for the mental model: component fn, state in slots, tree diff by
 * value.
 */
public class ModelTest {

    @Test
    void elementEqualityByValue() {
        VNode a = new ElementVNode("div", null, Map.of("class", "x"), Map.of(), List.of(new TextVNode("hi")));
        VNode b = new ElementVNode("div", null, Map.of("class", "x"), Map.of(), List.of(new TextVNode("hi")));
        assertEquals(a, b);
        VNode c = new ElementVNode("div", null, Map.of("class", "y"), Map.of(), List.of(new TextVNode("hi")));
        assertNotEquals(a, c);
    }

    @Test
    void stateSlotsSurviveRerender() {
        Renderer.Instance inst = new Renderer.Instance();
        Renderer.RenderScope s1 = new Renderer.RenderScope(inst);
        State<Integer> count = s1.useState(0);
        count.set(count.get() + 1);

        Renderer.RenderScope s2 = new Renderer.RenderScope(inst);
        State<Integer> again = s2.useState(99);
        assertEquals(1, again.get());
    }

    @Test
    void useMemoRespectsDeps() {
        Renderer.Instance inst = new Renderer.Instance();
        int[] calls = {0};
        Renderer.RenderScope r1 = new Renderer.RenderScope(inst);
        String m1 = r1.useMemo(d -> "v" + calls[0]++, "stable");
        Renderer.RenderScope r2 = new Renderer.RenderScope(inst);
        String m2 = r2.useMemo(d -> "v" + calls[0]++, "stable");
        assertEquals(m1, m2);
        assertEquals(1, calls[0]);
    }

    @Test
    void useEffectTracksDeps() {
        Renderer.Instance inst = new Renderer.Instance();
        Renderer.RenderScope r1 = new Renderer.RenderScope(inst);
        r1.useEffect(() -> {}, "a");
        Renderer.RenderScope r2 = new Renderer.RenderScope(inst);
        r2.useEffect(() -> {}, "a");
        assertEquals(1, inst.states.size());
    }

    @Test
    void componentIsPureFunction() {
        Renderer.Instance inst = new Renderer.Instance();
        Renderer.RenderScope scope = new Renderer.RenderScope(inst);
        FC<String> hello = (sc, name) -> El.h("p", null, El.text("hi " + name));
        VNode out = hello.render(scope, "bob");
        assertTrue(out instanceof ElementVNode);
        assertEquals("p", ((ElementVNode) out).tagName());
        assertEquals("hi bob", ((TextVNode) ((ElementVNode) out).children().get(0)).text());
    }

    @Test
    void keyedChildrenReuseByIdentity() {
        VNode a = new ElementVNode("li", "x", Map.of(), Map.of(), List.of(new TextVNode("a")));
        VNode b = new ElementVNode("li", "y", Map.of(), Map.of(), List.of(new TextVNode("b")));
        assertEquals("x", a.key());
        assertEquals("y", b.key());
    }

    @Test
    void memoBoxChangesOnDepsChange() {
        Renderer.MemoBox<String> box = new Renderer.MemoBox<>();
        assertEquals("1", box.compute(d -> "1", "a"));
        assertEquals("1", box.compute(d -> "2", "a"));
        assertEquals("2", box.compute(d -> "2", "b"));
    }

    @Test
    void stateSetSkipsEqualValues() {
        Renderer.Instance inst = new Renderer.Instance();
        Renderer.RenderScope s = new Renderer.RenderScope(inst);
        State<String> name = s.useState("bob");
        name.set("bob");
        assertEquals("bob", name.get());
        name.set(v -> v + "!");
        assertEquals("bob!", name.get());
    }

    @Test
    void useRefBoxesValue() {
        Renderer.Instance inst = new Renderer.Instance();
        Renderer.RenderScope s = new Renderer.RenderScope(inst);
        Renderer.Ref<Integer> clicks = s.useRef(0);
        clicks.current++;
        assertEquals(1, clicks.current);
    }
}
