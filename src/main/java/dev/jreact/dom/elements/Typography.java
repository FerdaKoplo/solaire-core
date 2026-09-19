package dev.jreact.dom.elements;

import java.util.HashMap;
import java.util.Map;

import dev.jreact.dom.VNode;

public class Typography {
    public static VNode h1(VNode... children) {
        return Core.h("h1", null, children);
    }

    public static VNode h1(Attrs attrs, VNode... children) {
        return Core.h("h1", attrs, children);
    }

    public static VNode h2(VNode... children) {
        return Core.h("h2", null, children);
    }

    public static VNode h2(Attrs attrs, VNode... children) {
        return Core.h("h2", attrs, children);
    }

    public static VNode h3(VNode... children) {
        return Core.h("h3", null, children);
    }

    public static VNode h3(Attrs attrs, VNode... children) {
        return Core.h("h3", attrs, children);
    }

    public static VNode h4(VNode... children) {
        return Core.h("h4", null, children);
    }

    public static VNode h4(Attrs attrs, VNode... children) {
        return Core.h("h4", attrs, children);
    }

    public static VNode h5(VNode... children) {
        return Core.h("h5", null, children);
    }

    public static VNode h5(Attrs attrs, VNode... children) {
        return Core.h("h5", attrs, children);
    }

    public static VNode h6(VNode... children) {
        return Core.h("h6", null, children);
    }

    public static VNode h6(Attrs attrs, VNode... children) {
        return Core.h("h6", attrs, children);
    }

    public static VNode p(VNode... children) {
        return Core.h("p", null, children);
    }

    public static VNode p(Attrs attrs, VNode... children) {
        return Core.h("p", attrs, children);
    }

    public static VNode a(VNode... children) {
        return Core.h("a", null, children);
    }

    public static VNode a(Attrs attrs, VNode... children) {
        return Core.h("a", attrs, children);
    }

    public static VNode strong(VNode... children) {
        return Core.h("strong", null, children);
    }

    public static VNode strong(Attrs attrs, VNode... children) {
        return Core.h("strong", attrs, children);
    }

    public static VNode em(VNode... children) {
        return Core.h("em", null, children);
    }

    public static VNode em(Attrs attrs, VNode... children) {
        return Core.h("em", attrs, children);
    }

    public static VNode b(VNode... children) {
        return Core.h("b", null, children);
    }

    public static VNode b(Attrs attrs, VNode... children) {
        return Core.h("b", attrs, children);
    }

    public static VNode i(VNode... children) {
        return Core.h("i", null, children);
    }

    public static VNode i(Attrs attrs, VNode... children) {
        return Core.h("i", attrs, children);
    }

    public static VNode blockquote(VNode... children) {
        return Core.h("blockquote", null, children);
    }

    public static VNode blockquote(Attrs attrs, VNode... children) {
        return Core.h("blockquote", attrs, children);
    }

    public static VNode code(VNode... children) {
        return Core.h("code", null, children);
    }

    public static VNode code(Attrs attrs, VNode... children) {
        return Core.h("code", attrs, children);
    }

    public static VNode pre(VNode... children) {
        return Core.h("pre", null, children);
    }

    public static VNode pre(Attrs attrs, VNode... children) {
        return Core.h("pre", attrs, children);
    }

    public static VNode br() {
        return Core.h("br", null);
    }

    public static VNode br(Attrs attrs) {
        return Core.h("br", attrs);
    }

    public static VNode hr() {
        return Core.h("hr", null);
    }

    public static VNode hr(Attrs attrs) {
        return Core.h("hr", attrs);
    }
}
