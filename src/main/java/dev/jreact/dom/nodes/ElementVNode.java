package dev.jreact.dom.nodes;

import java.util.List;
import java.util.Map;

public record ElementVNode(
        String tagName,
        String key,
        Map<String, String> props,
        Map<String, Runnable> events,
        List<VNode> children
) implements VNode {}
