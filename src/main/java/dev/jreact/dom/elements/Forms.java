package dev.jreact.dom.elements;

import java.util.HashMap;
import java.util.Map;

import dev.jreact.dom.VNode;

public class Forms {
    public static VNode form(VNode... children) {
        return Core.h("form", new HashMap<>(), new HashMap<>(), children);
    }

    public static VNode form(Map<String, String> props, VNode... children) {
        return Core.h("form", props, new HashMap<>(), children);
    }

    public static VNode form(Map<String, String> props, Map<String, Runnable> events, VNode... children) {
        return Core.h("form", props, events, children);
    }

    public static VNode input(Map<String, String> props) {
        return Core.h("input", props, new HashMap<>());
    }

    public static VNode input(Map<String, String> props, Map<String, Runnable> events) {
        return Core.h("input", props, events);
    }

    public static VNode button(VNode... children) {
        return Core.h("button", new HashMap<>(), new HashMap<>(), children);
    }

    public static VNode button(Map<String, String> props, VNode... children) {
        return Core.h("button", props, new HashMap<>(), children);
    }

    public static VNode button(Map<String, String> props, Map<String, Runnable> events, VNode... children) {
        return Core.h("button", props, events, children);
    }

    public static VNode button(Map<String, String> props, String text, Runnable onClick) {
        Map<String, Runnable> events = new HashMap<>();
        if (onClick != null) events.put("click", onClick);
        return Core.h("button", props != null ? props : new HashMap<>(), events, Core.text(text));
    }

    public static VNode label(VNode... children) {
        return Core.h("label", new HashMap<>(), new HashMap<>(), children);
    }

    public static VNode label(Map<String, String> props, VNode... children) {
        return Core.h("label", props, new HashMap<>(), children);
    }

    public static VNode textarea(Map<String, String> props) {
        return Core.h("textarea", props, new HashMap<>());
    }

    public static VNode textarea(Map<String, String> props, Map<String, Runnable> events) {
        return Core.h("textarea", props, events);
    }

    public static VNode select(VNode... children) {
        return Core.h("select", new HashMap<>(), new HashMap<>(), children);
    }

    public static VNode select(Map<String, String> props, VNode... children) {
        return Core.h("select", props, new HashMap<>(), children);
    }

    public static VNode select(Map<String, String> props, Map<String, Runnable> events, VNode... children) {
        return Core.h("select", props, events, children);
    }

    public static VNode option(VNode... children) {
        return Core.h("option", new HashMap<>(), new HashMap<>(), children);
    }

    public static VNode option(Map<String, String> props, VNode... children) {
        return Core.h("option", props, new HashMap<>(), children);
    }
}
