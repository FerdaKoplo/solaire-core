package dev.jreact.dom.nodes;

import dev.jreact.dom.FC;

/**
 * Lazy reference to a component plus its props. Resolved before diffing, so the
 * Renderer never sees it.
 */
public record ComponentVNode<P>(FC<P> component, P componentProps, String key) implements VNode { }
