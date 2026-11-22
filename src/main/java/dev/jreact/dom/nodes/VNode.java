package dev.jreact.dom.nodes;

/**
 * Declarative UI tree. One immutable snapshot per render.
 *
 * mental model: a component is a function props -> Node. Nodes are never
 * mutated; every update builds a new tree and the Renderer diffs old vs new.
 * Only ElementVNode carries props/children; TextVNode and ComponentVNode are
 * leaves from the tree's point of view (components resolve before diffing).
 */
public sealed interface VNode permits TextVNode, ElementVNode, ComponentVNode {

    /** Reconcile identity within a sibling list. Null = positional. */
    String key();
}
