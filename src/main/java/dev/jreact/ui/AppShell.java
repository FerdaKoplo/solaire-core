package dev.jreact.ui;

import dev.jreact.dom.Attr;
import dev.jreact.dom.Renderer;
import dev.jreact.dom.State;
import dev.jreact.dom.nodes.VNode;

import java.util.List;
import java.util.function.Supplier;

import static dev.jreact.dom.H.el;

/**
 * App shell: fixed sidebar navigation plus a main content area. Navigation
 * items are (label, href) pairs; the active one is highlighted by path prefix.
 *
 *   Ui.appShell(scope,
 *       "My App",
 *       List.of(Nav.item("/users", "Users"), Nav.item("/settings", "Settings")),
 *       () -> content())
 */
public final class AppShell {

    public record NavItem(String href, String label) {
        public static NavItem item(String href, String label) { return new NavItem(href, label); }
    }

    public static VNode appShell(Renderer.RenderScope scope, String brand,
                                 List<NavItem> items, Supplier<VNode> content) {
        State<String> active = scope.useState("");
        String current = dev.jreact.dom.Router.path();
        for (NavItem n : items) {
            if (current.equals(n.href()) || current.startsWith(n.href() + "/")) {
                if (!active.get().equals(n.href())) active.set(n.href());
            }
        }
        Classnames cn = Classnames.get();
        return el("div").cls(cn.appShell()).kids(
                el("aside").cls(cn.sidebar()).kids(
                        el("div").cls("brand").kids(brand),
                        el("nav").kids(items.stream()
                                .map(n -> el("a").attr("href", "#" + n.href())
                                        .cls(cn.navItem(active.get().equals(n.href())))
                                        .kids(n.label()).end())
                                .toList())),
                el("main").cls(cn.main()).kids(content.get())
        ).end();
    }

    private AppShell() {}
}
