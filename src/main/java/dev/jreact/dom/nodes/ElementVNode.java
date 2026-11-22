package dev.jreact.dom.nodes;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * DOM element. Value semantics: same tag + key + props + children => same node,
 * so the Renderer can skip patching entirely (structural sharing).
 *
 * events are NOT part of equals (lambdas are not comparable); they attach on
 * build and re-attach only when the DOM node is rebuilt.
 */
public final class ElementVNode implements VNode {

    private final String tagName;
    private final String key;
    private final Map<String, String> props;
    private final List<VNode> children;
    private final Map<String, Runnable> events;

    public ElementVNode(String tagName, String key, Map<String, String> props,
                        Map<String, Runnable> events, List<VNode> children) {
        this.tagName = Objects.requireNonNull(tagName, "tagName");
        this.key = key;
        this.props = props == null ? Map.of() : Map.copyOf(props);
        this.events = events == null ? Map.of() : Map.copyOf(events);
        if (children == null) {
            this.children = List.of();
        } else {
            List<VNode> list = new java.util.ArrayList<>(children.size());
            for (VNode c : children) list.add(c == null ? new TextVNode("") : c);
            this.children = List.copyOf(list);
        }
    }

    /** Copy with replaced children (used by resolve to attach rendered subtrees). */
    public ElementVNode withChildren(List<VNode> next) {
        return new ElementVNode(tagName, key, props, events, next);
    }

    public String tagName() { return tagName; }
    @Override public String key() { return key; }
    public Map<String, String> props() { return props; }
    public Map<String, Runnable> events() { return events; }
    public List<VNode> children() { return children; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ElementVNode e)) return false;
        return tagName.equals(e.tagName) && Objects.equals(key, e.key)
                && props.equals(e.props) && children.equals(e.children);
    }

    @Override public int hashCode() {
        return Objects.hash(tagName, key, props, children);
    }

    @Override public String toString() {
        return "<" + tagName + (key != null ? " key=" + key : "") + ">";
    }
}
