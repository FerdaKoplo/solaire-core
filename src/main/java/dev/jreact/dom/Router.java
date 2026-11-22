package dev.jreact.dom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.html.HTMLElement;

import dev.jreact.dom.nodes.ElementVNode;
import dev.jreact.dom.nodes.VNode;

/**
 * Hash-based router, React-Router-shaped but tiny.
 *
 *  Router.install(rootEl, List.of(
 *      Router.route("/",          () -> Home.page()),
 *      Router.route("/users/:id", params -> UserPage.render(params))
 *  ));
 *  Router.navigate("/users/7");
 *
 * Reading state inside a page: Router.path() / Router.params().
 * <Link to="/x"> renders an <a> that navigates without a page reload.
 */
public final class Router {
    private Router() {}

    /** A page: given route params, produce the tree. */
    public interface Page {
        VNode render(Map<String, String> params);
    }

    /** A page that also owns its component state. */
    public interface ComponentPage<P> {
        VNode render(Renderer.RenderScope scope, P props, Map<String, String> params);
    }

    public static final class Route {
        final String pattern;
        final String[] segments;
        final Page page;

        Route(String pattern, Page page) {
            this.pattern = pattern;
            this.segments = pattern.split("/");
            this.page = page;
        }

        Map<String, String> match(String path) {
            String[] parts = path.split("/");
            if (parts.length != segments.length) return null;
            Map<String, String> params = new HashMap<>();
            for (int i = 0; i < segments.length; i++) {
                String s = segments[i];
                if (s.startsWith(":")) params.put(s.substring(1), parts[i]);
                else if (!s.equals(parts[i])) return null;
            }
            return params;
        }
    }

    private static final List<Route> routes = new ArrayList<>();
    private static HTMLElement container;
    private static Map<String, String> currentParams = Map.of();
    private static String currentPath = "/";

    /** Install the router; it owns the container (use instead of JReact.mount). */
    public static void install(HTMLElement root, List<Route> table) {
        routes.clear();
        routes.addAll(table);
        container = root;
        Window.current().addEventListener("popstate", e -> show());
        show();
    }

    public static Route route(String pattern, Page page) { return new Route(pattern, page); }

    public static Route route(String pattern, java.util.function.Supplier<VNode> page) {
        return new Route(pattern, params -> page.get());
    }

    /** Navigate programmatically: sets hash and re-renders the matched page. */
    public static void navigate(String path) {
        Window.current().getLocation().setHash(path);
        show();
    }

    public static String path() { return currentPath; }
    public static Map<String, String> params() { return currentParams; }

    /** Declarative navigation element (click intercepts, no reload). */
    public static VNode link(String to, VNode... children) {
        return new ElementVNode("a", null,
                Map.of("href", "#" + to),
                Map.of("click", () -> {
                    Event e = Attr.currentEvent();
                    if (e != null) e.preventDefault();
                    navigate(to);
                }),
                List.of(children));
    }

    public static VNode link(String to, String label) {
        return link(to, El.text(label));
    }

    private static void show() {
        String hash = Window.current().getLocation().getHash();
        String path = hash == null || hash.isEmpty() || "#".equals(hash) ? "/" : hash.substring(1);
        if (path.isEmpty()) path = "/";
        for (Route r : routes) {
            Map<String, String> params = r.match(path);
            if (params != null) {
                currentPath = path;
                currentParams = Map.copyOf(params);
                JReact.renderExternal(r.page.render(currentParams), container);
                return;
            }
        }
        currentPath = path;
        currentParams = Map.of();
        JReact.renderExternal(El.text("404 — no route for " + path), container);
    }
}
