package dev.jreact.dom.elements;

import java.util.HashMap;
import java.util.Map;

import dev.jreact.dom.VNode;

public class Lists {
    public static VNode ul(VNode... children) {
        return Core.h("ul", null, children);
    }

    public static VNode ul(Attrs attrs, VNode... children) {
        return Core.h("ul", attrs, children);
    }

    public static VNode ol(VNode... children) {
        return Core.h("ol", null, children);
    }

    public static VNode ol(Attrs attrs, VNode... children) {
        return Core.h("ol", attrs, children);
    }

    public static VNode li(VNode... children) {
        return Core.h("li", null, children);
    }

    public static VNode li(Attrs attrs, VNode... children) {
        return Core.h("li", attrs, children);
    }

    public static VNode dl(VNode... children) {
        return Core.h("dl", null, children);
    }

    public static VNode dl(Attrs attrs, VNode... children) {
        return Core.h("dl", attrs, children);
    }

    public static VNode dt(VNode... children) {
        return Core.h("dt", null, children);
    }

    public static VNode dt(Attrs attrs, VNode... children) {
        return Core.h("dt", attrs, children);
    }

    public static VNode dd(VNode... children) {
        return Core.h("dd", null, children);
    }

    public static VNode dd(Attrs attrs, VNode... children) {
        return Core.h("dd", attrs, children);
    }
}
