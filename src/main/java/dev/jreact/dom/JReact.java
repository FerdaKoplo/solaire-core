package dev.jreact.dom;

import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.html.HTMLElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import dev.jreact.dom.nodes.VNode;

/**
 * Entry point. Owns: root render, scheduling, mount/update.
 *
 * mental model: one render = one pass over the VNode tree. State changes mark
 * dirty and coalesce into a single render per microtask (automatic batching).
 */
public final class JReact {
    private JReact() {}

    private static Supplier<VNode> root;
    private static HTMLElement container;
    private static VNode current;
    private static boolean scheduled = false;

    public static void mount(FC.NoProps component, HTMLElement el) {
        mount((owner, props) -> component.render(owner), null, el);
    }

    public static <P> void mount(FC<P> component, P props, HTMLElement el) {
        root = () -> {
            Renderer.RenderScope scope = new Renderer.RenderScope(new Renderer.Instance());
            return component.render(scope, props);
        };
        container = el;
        renderNow();
    }

    /** Used by Router to push an externally-computed tree through the same diff pipeline. */
    static void renderExternal(VNode tree, HTMLElement el) {
        Supplier<VNode> prevRoot = root;
        root = () -> tree;
        container = el;
        renderNow();
        root = prevRoot;
    }

    /** Coalesce all set() calls in one tick into a single re-render. */
    public static void requestRender() {
        if (scheduled || root == null) return;
        scheduled = true;
        Window.setTimeout(() -> { scheduled = false; renderNow(); }, 0);
    }

    /** Force synchronous render (tests, router transitions). */
    public static void flush() { if (root != null) renderNow(); }

    private static void renderNow() {
        if (root == null || container == null) return;
        VNode raw = root.get();
        java.util.Set<String> live = new java.util.HashSet<>();
        VNode resolved = Renderer.resolve(raw, "root", live);
        Renderer.gc(live);
        QueryBridge.gc(live);
        if (current == null) {
            Renderer.mount(resolved, container);
        } else {
            Renderer.update(container, resolved, current);
        }
        current = resolved;
    }

    /** Indirection so dom package doesn't import data package directly. */
    static final class QueryBridge {
        static void gc(java.util.Set<String> livePaths) {
            dev.jreact.data.Query.gc(livePaths);
        }
    }
}
