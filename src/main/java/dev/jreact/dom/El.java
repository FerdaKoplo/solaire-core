package dev.jreact.dom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import dev.jreact.dom.nodes.ComponentVNode;
import dev.jreact.dom.nodes.ElementVNode;
import dev.jreact.dom.nodes.TextVNode;
import dev.jreact.dom.nodes.VNode;

/**
 * The JSX layer. Everything you need to build UI lives here: tags, forms,
 * lists, fragments, keys, components, routing links.
 *
 *  El.div(Attr.cls("card"), El.text("hi"))
 *  El.button("Save", () -> save())
 *  El.input(Attr.of().placeholder("name"), name.get(), v -> name.set(v))
 */
public final class El {
    private El() {}

    /** Raw element: exact control over tag/key/props/events. */
    public static VNode h(String tag, Attr attrs, VNode... children) {
        return new ElementVNode(tag,
                attrs == null ? null : attrs.key(),
                attrs == null ? Map.of() : attrs.props(),
                attrs == null ? Map.of() : attrs.events(),
                Arrays.asList(children));
    }

    /** Tag + attrs + loose children (Strings deepseek-v4-pro-wrap in text nodes). */
    public static VNode h(String tag, Attr attrs, Object... children) {
        return h(tag, attrs, toNodes(children));
    }

    public static VNode[] toNodes(Object... raw) {
        VNode[] out = new VNode[raw.length];
        for (int i = 0; i < raw.length; i++) out[i] = toNode(raw[i]);
        return out;
    }

    public static VNode toNode(Object c) {
        if (c instanceof VNode v) return v;
        if (c instanceof Iterable<?> it) {
            List<VNode> kids = new ArrayList<>();
            for (Object o : it) kids.add(toNode(o));
            return frag(kids);
        }
        return text(c);
    }

    /** Multiple roots without a wrapper element (rendered inline). */
    public static VNode frag(VNode... children) {
        return new ElementVNode("#frag", null, Map.of(), Map.of(), List.of(children));
    }

    public static VNode frag(List<VNode> children) {
        return new ElementVNode("#frag", null, Map.of(), Map.of(), children);
    }

    public static VNode text(Object value) {
        return new TextVNode(value == null ? "" : value.toString());
    }

    /** Declare a component usage (resolved by Renderer, key keeps state). */
    public static <P> VNode component(FC<P> fc, P props, String key) {
        return new ComponentVNode<>(fc, props, key);
    }

    public static <P> VNode component(FC<P> fc, P props) {
        return new ComponentVNode<>(fc, props, null);
    }

    public static VNode div(Attr a, Object... c) { return h("div", a, c); }
    public static VNode div(Object... c) { return h("div", null, c); }
    public static VNode span(Attr a, Object... c) { return h("span", a, c); }
    public static VNode span(Object... c) { return h("span", null, c); }
    public static VNode p(Attr a, Object... c) { return h("p", a, c); }
    public static VNode p(Object... c) { return h("p", null, c); }
    public static VNode section(Attr a, Object... c) { return h("section", a, c); }
    public static VNode header(Attr a, Object... c) { return h("header", a, c); }
    public static VNode footer(Attr a, Object... c) { return h("footer", a, c); }
    public static VNode nav(Attr a, Object... c) { return h("nav", a, c); }
    public static VNode aside(Attr a, Object... c) { return h("aside", a, c); }
    public static VNode main(Attr a, Object... c) { return h("main", a, c); }
    public static VNode article(Attr a, Object... c) { return h("article", a, c); }
    public static VNode br() { return h("br", null); }
    public static VNode hr() { return h("hr", null); }

    public static VNode h1(Attr a, Object... c) { return h("h1", a, c); }
    public static VNode h2(Attr a, Object... c) { return h("h2", a, c); }
    public static VNode h3(Attr a, Object... c) { return h("h3", a, c); }
    public static VNode h4(Attr a, Object... c) { return h("h4", a, c); }
    public static VNode h5(Attr a, Object... c) { return h("h5", a, c); }
    public static VNode h6(Attr a, Object... c) { return h("h6", a, c); }
    public static VNode strong(Attr a, Object... c) { return h("strong", a, c); }
    public static VNode em(Attr a, Object... c) { return h("em", a, c); }
    public static VNode code(Attr a, Object... c) { return h("code", a, c); }
    public static VNode small(Attr a, Object... c) { return h("small", a, c); }

    public static VNode img(String src, String alt) {
        return h("img", Attr.of().src(src).alt(alt));
    }

    public static VNode img(Attr a, String src, String alt) {
        return h("img", a.src(src).alt(alt));
    }

    /** Plain anchor. For app navigation prefer link() (no page reload). */
    public static VNode a(String href, Object... children) {
        return h("a", Attr.of().href(href), children);
    }

    /** App-internal link: renders <a href="#to"> and intercepts clicks. */
    public static VNode link(String to, Object... children) {
        return Router.link(to, toNodes(children));
    }

    /** Anchor for a hash route. */
    public static VNode anchor(String to, String label) {
        return Router.link(to, text(label));
    }

    public static VNode ul(Attr a, Object... c) { return h("ul", a, c); }
    public static VNode ul(Object... c) { return h("ul", null, c); }
    public static VNode ol(Attr a, Object... c) { return h("ol", a, c); }
    public static VNode ol(Object... c) { return h("ol", null, c); }
    public static VNode li(Attr a, Object... c) { return h("li", a, c); }
    public static VNode li(Object... c) { return h("li", null, c); }
    public static VNode li(String key, Object... c) { return h("li", Attr.of().key(key), c); }

    public static VNode form(Attr a, Object... children) { return h("form", a, children); }
    public static VNode form(Object... children) { return h("form", null, children); }

    public static VNode fieldset(Attr a, Object... children) { return h("fieldset", a, children); }
    public static VNode legend(String text) { return h("legend", null, text(text)); }
    public static VNode label(Attr a, Object... c) { return h("label", a, c); }
    public static VNode label(Object... c) { return h("label", null, c); }

    public static VNode input(Attr a) { return h("input", a); }

    /** Controlled text input: shows value, writes on each keystroke. */
    public static VNode input(Attr a, String value, Consumer<String> onChange) {
        return h("input", a.value(value).onInput(() -> onChange.accept(Attr.inputValue())));
    }

    public static VNode textarea(Attr a, String value) { return h("textarea", a, text(value)); }

    public static VNode textarea(Attr a, String value, Consumer<String> onChange) {
        return h("textarea", a.value(value).onInput(() -> onChange.accept(Attr.inputValue())));
    }

    public static VNode select(Attr a, Object... children) { return h("select", a, children); }

    public static VNode option(String value, String label, boolean selected) {
        return h("option", Attr.of().attr("value", value)
                .attr("selected", selected ? "selected" : null), text(label));
    }

    public static VNode button(String label, Runnable onClick) {
        return h("button", Attr.of().onClick(onClick), text(label));
    }

    public static VNode button(Attr a, String label, Runnable onClick) {
        return h("button", a.onClick(onClick), text(label));
    }

    public static VNode submit(String label) {
        return h("button", Attr.of().type("submit"), text(label));
    }

    public static VNode table(Attr a, Object... c) { return h("table", a, c); }
    public static VNode thead(Attr a, Object... c) { return h("thead", a, c); }
    public static VNode tbody(Attr a, Object... c) { return h("tbody", a, c); }
    public static VNode tr(Attr a, Object... c) { return h("tr", a, c); }
    public static VNode th(Attr a, Object... c) { return h("th", a, c); }
    public static VNode td(Attr a, Object... c) { return h("td", a, c); }
}
