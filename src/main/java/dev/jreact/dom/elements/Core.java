package dev.jreact.dom.elements;

import java.util.Arrays;
import java.util.HashMap;

import dev.jreact.dom.FC;
import dev.jreact.dom.nodes.ComponentVNode;
import dev.jreact.dom.nodes.ElementVNode;
import dev.jreact.dom.nodes.TextVNode;
import dev.jreact.dom.nodes.VNode;

public class Core {
    private Core() {

    }

    public static VNode h(String tagName, Attrs attrs, VNode... children) {
        Attrs a = attrs != null ? attrs : Attrs.of();
        return new ElementVNode(
                tagName,
                a.key,
                new HashMap<>(a.props),
                new HashMap<>(a.events),
                Arrays.asList(children));
    }

    public static VNode h(String tagName, Attrs attrs) {
        return h(tagName, attrs, new VNode[0]);
    }

    public static VNode text(String value) {
        return new TextVNode(value);
    }

    public static <P> VNode component(FC<P> fc, P props) {
        return new ComponentVNode<>(fc, props, null);
    }

    public static <P> VNode component(FC<P> fc, P props, String key) {
        return new ComponentVNode<>(fc, props, key);
    }
}
