package dev.jreact.dom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VNode {
    public final String key;
    public final String tagName;
    public final Map<String, String> props;
    public final Map<String, Runnable> events;
    public final List<VNode> children;
    public final String text;
    public final Map<String, org.teavm.jso.dom.events.EventListener<?>> attachedListeners = new HashMap<>();

    public VNode(String tagName, String key, Map<String, String> props, Map<String, Runnable> events,
            List<VNode> children) {
        this.key = key;
        this.tagName = tagName;
        this.props = props != null ? props : new HashMap<>();
        this.events = events != null ? events : new HashMap<>();
        this.children = children != null ? children : new ArrayList<>();
        this.text = null;
    }

    public VNode(String tagName, Map<String, String> props, Map<String, Runnable> events, List<VNode> children) {
        this(tagName, null, props, events, children);
    }

    public VNode(String text) {
        this.key = null;
        this.tagName = null;
        this.props = Collections.emptyMap();
        this.children = Collections.emptyList();
        this.events = Collections.emptyMap();
        this.text = text;
    }

    public boolean isTextNode() {
        return text != null;
    }
}