package dev.jreact.dom;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import dev.jreact.dom.nodes.ComponentVNode;
import dev.jreact.dom.nodes.ElementVNode;
import dev.jreact.dom.nodes.TextVNode;
import dev.jreact.dom.nodes.VNode;
import dev.jreact.hooks.HookContext;
import dev.jreact.hooks.Hooks;

public class Resolver {
    private Resolver() {
    }

    @SuppressWarnings("unchecked")
    public static VNode resolve(VNode node, String path, ComponentTree tree, Set<String> livePaths) {
        if (node instanceof ComponentVNode<?> c) {
            livePaths.add(path);
            HookContext ctx = tree.contextFor(path);
            ctx.startRender();
            Hooks.setCurrent(ctx);
            VNode rendered;
            try {
                rendered = ((ComponentVNode<Object>) c).component().render(c.props());
            } finally {
                Hooks.clearCurrent();
            }
            return resolve(rendered, path + "/render", tree, livePaths);
        }

        if (node instanceof TextVNode) {
            livePaths.add(path);
            return node;
        }

        ElementVNode e = (ElementVNode) node;
        List<VNode> resolvedChildren = new ArrayList<>();
        for (int i = 0; i < e.children().size(); i++) {
            VNode child = e.children().get(i);
            String childId = child.key() != null ? child.key() : String.valueOf(i);
            resolvedChildren.add(resolve(child, path + "/" + childId, tree, livePaths));
        }
        livePaths.add(path);
        return new ElementVNode(e.tagName(), e.key(), e.props(), e.events(), resolvedChildren);
    }
}
