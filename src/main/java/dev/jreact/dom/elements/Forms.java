package dev.jreact.dom.elements;

import java.util.HashMap;
import java.util.Map;

import dev.jreact.dom.VNode;

public class Forms {
    public static VNode form(VNode... children) {
        return Core.h("form", null, children);
    }

    public static VNode form(Attrs attrs, VNode... children) {
        return Core.h("form", attrs, children);
    }

    public static VNode input(Attrs attrs) {
        return Core.h("input", attrs);
    }

    public static VNode button(VNode... children) {
        return Core.h("button", null, children);
    }

    public static VNode button(Attrs attrs, VNode... children) {
        return Core.h("button", attrs, children);
    }

    public static VNode button(Attrs attrs, String text, Runnable onClick) {
        Attrs a = attrs != null ? attrs : Attrs.of();
        if (onClick != null)
            a.on("click", onClick);
        return Core.h("button", a, Core.text(text));
    }

    public static VNode label(VNode... children) {
        return Core.h("label", null, children);
    }

    public static VNode label(Attrs attrs, VNode... children) {
        return Core.h("label", attrs, children);
    }

    public static VNode textarea(Attrs attrs) {
        return Core.h("textarea", attrs);
    }

    public static VNode select(VNode... children) {
        return Core.h("select", null, children);
    }

    public static VNode select(Attrs attrs, VNode... children) {
        return Core.h("select", attrs, children);
    }

    public static VNode option(VNode... children) {
        return Core.h("option", null, children);
    }

    public static VNode option(Attrs attrs, VNode... children) {
        return Core.h("option", attrs, children);
    }
}
