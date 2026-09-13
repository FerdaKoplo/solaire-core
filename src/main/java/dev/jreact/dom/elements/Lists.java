package dev.jreact.dom.elements;

import java.util.HashMap;
import java.util.Map;

import dev.jreact.dom.VNode;

public class Lists {
    public static VNode ul(VNode... children) {
        return Core.h("ul", new HashMap<>(), new HashMap<>(), children);
    }

    public static VNode ul(Map<String, String> props, VNode... children) {
        return Core.h("ul", props, new HashMap<>(), children);
    }

    public static VNode ol(VNode... children) {
        return Core.h("ol", new HashMap<>(), new HashMap<>(), children);
    }

    public static VNode ol(Map<String, String> props, VNode... children) {
        return Core.h("ol", props, new HashMap<>(), children);
    }

    public static VNode li(VNode... children) {
        return Core.h("li", new HashMap<>(), new HashMap<>(), children);
    }

    public static VNode li(Map<String, String> props, VNode... children) {
        return Core.h("li", props, new HashMap<>(), children);
    }

    public static VNode dl(VNode... children) {
        return Core.h("dl", new HashMap<>(), new HashMap<>(), children);
    }

    public static VNode dl(Map<String, String> props, VNode... children) {
        return Core.h("dl", props, new HashMap<>(), children);
    }

    public static VNode dt(VNode... children) {
        return Core.h("dt", new HashMap<>(), new HashMap<>(), children);
    }

    public static VNode dt(Map<String, String> props, VNode... children) {
        return Core.h("dt", props, new HashMap<>(), children);
    }

    public static VNode dd(VNode... children) {
        return Core.h("dd", new HashMap<>(), new HashMap<>(), children);
    }

    public static VNode dd(Map<String, String> props, VNode... children) {
        return Core.h("dd", props, new HashMap<>(), children);
    }
}
