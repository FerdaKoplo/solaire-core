package dev.jreact.dom.nodes;

import dev.jreact.dom.Context;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Provides a value to the whole subtree beneath it. Resolved before diffing:
 * the Renderer pushes {@code context() -> value()} while rendering
 * {@code children}, so any {@code scope.useContext(ctx)} in a descendant sees it.
 */
public record ProviderVNode<T>(Context<T> context, T value, String key, Supplier<VNode> children) implements VNode {

    public ProviderVNode(Context<T> context, T value, Supplier<VNode> children) {
        this(context, value, null, children);
    }

    @Override
    public ProviderVNode<T> withKey(String k) {
        return k == null || Objects.equals(k, key)
                ? this
                : new ProviderVNode<>(context, value, k, children);
    }
}
