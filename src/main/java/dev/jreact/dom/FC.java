package dev.jreact.dom;

import dev.jreact.dom.nodes.VNode;

@FunctionalInterface
public interface FC<P> {
    VNode render(P props);
}
