package dev.jreact.dom;

import dev.jreact.dom.nodes.VNode;

/**
 * A component is a function (scope, props) -> Node. Nothing to subclass.
 *
 * State comes from the RenderScope passed as first argument:
 *
 *   var count = scope.useState(0);
 *   count.get();
 *   count.set(count.get() + 1);
 *
 * The scope is positional: call use/useState/useEffect unconditionally in the
 * same order every render (same rule as React hooks).
 */
@FunctionalInterface
public interface FC<P> {
    VNode render(Renderer.RenderScope scope, P props);

    /** Props-free component. */
    interface NoProps extends FC<Void> {
        VNode render(Renderer.RenderScope scope);

        @Override
        default VNode render(Renderer.RenderScope scope, Void props) { return render(scope); }
    }

    /** Component that only renders children (like React's Fragment consumer). */
    interface Leaf extends FC<Void> {
        @Override
        default VNode render(Renderer.RenderScope scope, Void props) { return render(); }
        VNode render();
    }
}
