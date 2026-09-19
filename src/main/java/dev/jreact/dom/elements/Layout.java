package dev.jreact.dom.elements;

import dev.jreact.dom.VNode;

public class Layout {
    public static VNode div(VNode... children) {
        return Core.h("div", null, children);
    }

    public static VNode div(Attrs attrs, VNode... children) {
        return Core.h("div", attrs, children);
    }

    public static VNode span(VNode... children) {
        return Core.h("span", null, children);
    }

    public static VNode span(Attrs attrs, VNode... children) {
        return Core.h("span", attrs, children);
    }

    public static VNode main(VNode... children) {
        return Core.h("main", null, children);
    }

    public static VNode main(Attrs attrs, VNode... children) {
        return Core.h("main", attrs, children);
    }

    public static VNode section(VNode... children) {
        return Core.h("section", null, children);
    }

    public static VNode section(Attrs attrs, VNode... children) {
        return Core.h("section", attrs, children);
    }

    public static VNode article(VNode... children) {
        return Core.h("article", null, children);
    }

    public static VNode article(Attrs attrs, VNode... children) {
        return Core.h("article", attrs, children);
    }

    public static VNode header(VNode... children) {
        return Core.h("header", null, children);
    }

    public static VNode header(Attrs attrs, VNode... children) {
        return Core.h("header", attrs, children);
    }

    public static VNode footer(VNode... children) {
        return Core.h("footer", null, children);
    }

    public static VNode footer(Attrs attrs, VNode... children) {
        return Core.h("footer", attrs, children);
    }

    public static VNode nav(VNode... children) {
        return Core.h("nav", null, children);
    }

    public static VNode nav(Attrs attrs, VNode... children) {
        return Core.h("nav", attrs, children);
    }

    public static VNode aside(VNode... children) {
        return Core.h("aside", null, children);
    }

    public static VNode aside(Attrs attrs, VNode... children) {
        return Core.h("aside", attrs, children);
    }
}
