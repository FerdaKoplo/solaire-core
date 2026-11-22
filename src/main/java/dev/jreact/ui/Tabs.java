package dev.jreact.ui;

import dev.jreact.dom.Attr;
import dev.jreact.dom.Renderer;
import dev.jreact.dom.State;
import dev.jreact.dom.nodes.VNode;

import java.util.List;
import java.util.function.Supplier;

import static dev.jreact.dom.H.el;

/**
 * Tab bar with content panels. Local state; no router involvement.
 *
 *   Ui.tabs(scope, List.of(Tab.of("Summary", () -> ...), Tab.of("Logs", () -> ...)))
 */
public final class Tabs {

    public record Tab(String label, Supplier<VNode> content) {
        public static Tab of(String label, Supplier<VNode> content) { return new Tab(label, content); }
    }

    public static VNode tabs(Renderer.RenderScope scope, List<Tab> tabs) {
        State<Integer> selected = scope.useState(0);
        Classnames cn = Classnames.get();
        int i = selected.get();
        return el("div").kids(
                el("div").cls(cn.tabs()).kids(tabs.stream()
                        .map(t -> el("button")
                                .cls(cn.tab(tabs.indexOf(t) == i))
                                .onClick(() -> selected.set(tabs.indexOf(t)))
                                .kids(t.label()).end())
                        .toList()),
                el("div").cls("tab-panel").kids(tabs.get(Math.min(i, tabs.size() - 1)).content().get())
        ).end();
    }

    private Tabs() {}
}
