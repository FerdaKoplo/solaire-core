package dev.jreact.dom.elements;

import java.util.Arrays;

import dev.jreact.dom.VNode;

public class Core {

    private Core() {

    }

    public static VNode h(String tagName, Attrs attrs, VNode... children) {
        Attrs a = attrs != null ? attrs : Attrs.of();
        return new VNode(tagName, a.key, a.props, a.events, Arrays.asList(children));
    }

    public static VNode h(String tagName, Attrs attrs) {
        return h(tagName, attrs, new VNode[0]);
    }

    public static VNode text(String value) {
        return new VNode(value);
    }
}
