package dev.jreact.dom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VNode {
    public final String tagName;
    public final Map<String, String> props;
    public final Map<String, Runnable> events;
    public final List<VNode> children;
    public final String text;

    public VNode(String tagName, Map<String, String> props, Map<String, Runnable> events, List<VNode> children) {
        this.tagName = tagName;
        this.props = props != null ? props : new HashMap<>();
        this.events = events != null ? events : new HashMap<>();
        this.children = children != null ? children : new ArrayList<>();
        this.text = null;
    }

    public VNode(String text) {
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