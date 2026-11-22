package dev.jreact.dom.nodes;

import dev.jreact.dom.Context;

import java.util.function.Supplier;

/**
 * Provides a value to the whole subtree beneath it. Resolved before diffing:
 * the Renderer pushes {@code context() -> value()} while rendering
 * {@code children}, so any {@code scope.useContext(ctx)} in a descendant sees it.
 */
public record ProviderVNode<T>(Context<T> context, T value, Supplier<VNode> children) implements VNode {
    @Override public String key() { return null; }
    @Override public ProviderVNode<T> withKey(String k) { return this; }
}