package dev.jreact.dom.elements;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import dev.jreact.dom.VNode;

public class Core {
    public static VNode h(String tagName, Map<String, String> props, Map<String, Runnable> events, VNode... children) {
        return new VNode(tagName, props, events, Arrays.asList(children));
    }

    public static VNode text(String value) {
        return new VNode(value);
    }

    public static VNode tag(String tagName, Map<String, String> props, VNode... children) {
        return h(tagName, props != null ? props : new HashMap<>(), new HashMap<>(), children);
    }
    
}
