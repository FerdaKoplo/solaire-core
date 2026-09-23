package dev.jreact.dom;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.teavm.jso.dom.html.HTMLElement;

import dev.jreact.dom.nodes.VNode;

public class JReact {
    private JReact() {
    }

    private static Supplier<VNode> rootRenderer;
    private static HTMLElement rootElement;
    private static VNode currentResolvedTree;
    private static final ComponentTree componentTree = new ComponentTree();

    public static void mount(Component component, HTMLElement container) {
        mountComponents(component::render, container);
    }

    public static <P> void mount(FC<P> fc, P props, HTMLElement container) {
        mountComponents(() -> fc.render(props), container);
    }

    public static void render() {
        if (rootRenderer == null || rootElement == null)
            return;

        VNode raw = rootRenderer.get();
        Set<String> livePaths = new HashSet<>();
        VNode resolved = Resolver.resolve(raw, "root", componentTree, livePaths);
        componentTree.gc(livePaths);

        if (currentResolvedTree == null) {
            Renderer.mount(resolved, rootElement);
        } else {
            Renderer.updateElement(rootElement, resolved, currentResolvedTree, 0);
        }
        currentResolvedTree = resolved;
    }

    private static void mountComponents(Supplier<VNode> renderer, HTMLElement container) {
        rootRenderer = renderer;
        rootElement = container;
        render();
    }
}
