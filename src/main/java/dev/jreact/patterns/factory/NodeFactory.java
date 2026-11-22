package dev.jreact.patterns.factory;

import dev.jreact.dom.FC;

import java.util.List;
import java.util.Map;

import dev.jreact.dom.nodes.ComponentVNode;
import dev.jreact.dom.nodes.ElementVNode;
import dev.jreact.dom.nodes.TextVNode;
import dev.jreact.dom.nodes.VNode;

/**
 * Abstract factory for the VNode family. El delegates here so node
 * construction can be substituted without touching the builders.
 */
public abstract class NodeFactory {

    public abstract VNode text(String text);

    public abstract VNode element(String tag, String key, Map<String, String> props,
                                  Map<String, Runnable> events, List<VNode> children);

    public abstract <P> VNode component(FC<P> fc, P props, String key);

    private static final NodeFactory DEFAULT = new NodeFactory() {
        @Override public VNode text(String text) { return new TextVNode(text); }

        @Override public VNode element(String tag, String key, Map<String, String> props,
                                      Map<String, Runnable> events, List<VNode> children) {
            return new ElementVNode(tag, key, props, events, children);
        }

        @Override public <P> VNode component(FC<P> fc, P props, String key) {
            return new ComponentVNode<>(fc, props, key);
        }
    };

    public static NodeFactory getDefault() { return DEFAULT; }
}
