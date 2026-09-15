package dev.jreact.dom;

@FunctionalInterface
public interface FC<P> {
    VNode render(P props);
}
