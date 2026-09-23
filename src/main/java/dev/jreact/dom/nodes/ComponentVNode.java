package dev.jreact.dom.nodes;

import dev.jreact.dom.FC;

public record ComponentVNode<P>(
        FC<P> component,
        P props,
        String key
) implements VNode {}
