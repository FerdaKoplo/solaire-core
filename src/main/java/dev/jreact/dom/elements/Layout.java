package dev.jreact.dom.elements;

import java.util.HashMap;
import java.util.Map;

import dev.jreact.dom.VNode;

public class Layout {
    public static VNode div(VNode... children) {
        return Core.h("div", new HashMap<>(), new HashMap<>(), children);
    }

    public static VNode div(Map<String, String> props, VNode... children) {
        return Core.h("div", props, new HashMap<>(), children);
    }

    public static VNode span(VNode... children) {
        return Core.h("span", new HashMap<>(), new HashMap<>(), children);
    }

    public static VNode span(Map<String, String> props, VNode... children) {
        return Core.h("span", props, new HashMap<>(), children);
    }

    public static VNode main(VNode... children) {
        return Core.h("main", new HashMap<>(), new HashMap<>(), children);
    }

    public static VNode main(Map<String, String> props, VNode... children) {
        return Core.h("main", props, new HashMap<>(), children);
    }

    public static VNode section(VNode... children) {
        return Core.h("section", new HashMap<>(), new HashMap<>(), children);
    }

    public static VNode section(Map<String, String> props, VNode... children) {
        return Core.h("section", props, new HashMap<>(), children);
    }public static VNode article(VNode... children) {
        return Core.h("article", new HashMap<>(), new HashMap<>(), children);
    }

    public static VNode article(Map<String, String> props, VNode... children) {
        return Core.h("article", props, new HashMap<>(), children);
    }

    public static VNode header(VNode... children) {
        return Core.h("header", new HashMap<>(), new HashMap<>(), children);
    }

    public static VNode header(Map<String, String> props, VNode... children) {
        return Core.h("header", props, new HashMap<>(), children);
    }

    public static VNode footer(VNode... children) {
        return Core.h("footer", new HashMap<>(), new HashMap<>(), children);
    }

    public static VNode footer(Map<String, String> props, VNode... children) {
        return Core.h("footer", props, new HashMap<>(), children);
    }

    public static VNode nav(VNode... children) {
        return Core.h("nav", new HashMap<>(), new HashMap<>(), children);
    }

    public static VNode nav(Map<String, String> props, VNode... children) {
        return Core.h("nav", props, new HashMap<>(), children);
    }

    public static VNode aside(VNode... children) {
        return Core.h("aside", new HashMap<>(), new HashMap<>(), children);
    }

    public static VNode aside(Map<String, String> props, VNode... children) {
        return Core.h("aside", props, new HashMap<>(), children);
    }
}
