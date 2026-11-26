package dev.jreact.dom;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import dev.jreact.dom.nodes.ProviderVNode;
import dev.jreact.dom.nodes.VNode;

/**
 * Element builder that reads like nested HTML: an open tag, indented children,
 * a closing call. One line per level of markup.
 *
 *   import static dev.jreact.dom.H.el;
 *
 *   return el("div").cls("card").kids(
 *       el("h1").kids("Count: ", count.get()),
 *       el("button").onClick(() -> count.set(v -> v + 1)).kids("+1"),
 *       el("button").onClick(() -> count.set(0)).kids("reset")
 *   ).end();
 *
 * same call style. Immutable builder; node construction goes through the
 * active {@link Renderer#factory()} (default: NodeFactory.getDefault()).
 */
public final class H {

    private final String tag;
    private final String key;
    private final Map<String, String> props;
    private final Map<String, Runnable> events;
    private final List<VNode> children;

    private H(String tag, String key, Map<String, String> props,
              Map<String, Runnable> events, List<VNode> children) {
        this.tag = tag;
        this.key = key;
        this.props = props;
        this.events = events;
        this.children = children;
    }

    public static H el(String tag) { return new H(tag, null, Map.of(), Map.of(), new ArrayList<>()); }

    public static H el(String tag, String key) {
        return new H(tag, key, Map.of(), Map.of(), new ArrayList<>());
    }

    /** Publish a Context value to the subtree built by {@code children}. */
    public static <T> VNode provider(Context<T> context, T value, Supplier<VNode> children) {
        return new ProviderVNode<>(context, value, children);
    }

    /** Keyed Context provider for lists/conditionals. */
    public static <T> VNode provider(Context<T> context, T value, String key, Supplier<VNode> children) {
        return new ProviderVNode<>(context, value, key, children);
    }

    public H kid(Object child) {
        List<VNode> merged = new ArrayList<>(children);
        merged.add(El.toNode(child));
        return new H(tag, key, props, events, merged);
    }

    public H kids(Object... items) {
        List<VNode> merged = new ArrayList<>(children);
        for (Object c : items) merged.add(El.toNode(c));
        return new H(tag, key, props, events, merged);
    }

    public H kids(Iterable<?> items) {
        List<VNode> merged = new ArrayList<>(children);
        for (Object c : items) merged.add(El.toNode(c));
        return new H(tag, key, props, events, merged);
    }

    public H cls(String... classes) { return attr("class", String.join(" ", classes)); }

    public H attr(String name, String value) {
        if (value == null) return this;
        Map<String, String> next = new LinkedHashMap<>(props);
        next.put(name, value);
        return new H(tag, key, next, events, children);
    }

    public H on(String event, Runnable handler) {
        if (handler == null) return this;
        Map<String, Runnable> next = new LinkedHashMap<>(events);
        next.put(event, handler);
        return new H(tag, key, props, next, children);
    }

    public H onClick(Runnable r) { return on("click", r); }

    public H onInput(Runnable r) { return on("input", r); }

    public H key(String key) { return new H(tag, key, props, events, children); }

    public VNode end() {
        return Renderer.factory().element(tag, key, props, events, List.copyOf(children));
    }
}
