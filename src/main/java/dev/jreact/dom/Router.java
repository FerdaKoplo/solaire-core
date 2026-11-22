package dev.jreact.dom;

import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.html.HTMLElement;

import dev.jreact.dom.nodes.ElementVNode;
import dev.jreact.dom.nodes.VNode;
import dev.jreact.patterns.strategy.RouteTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Hash router. A {@link RouteTable} (dictionary of patterns + matching
 * strategy) is scanned in order; the matched page renders into the container.
 *
 *  Router.install(rootEl, List.of(
 *      Router.route("/",          () -> Home.page()),
 *      Router.route("/users/:id", params -> UserPage.render(params))
 *  ));
 *  Router.navigate("/users/7");
 *
 * Reading state inside a page: Router.path() / Router.params().
 * Router.link(...) renders an <a> that navigates without a page reload.
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
        private final String pattern;
        private final Page page;

        Route(String pattern, Page page) {
            this.pattern = pattern;
            this.page = page;
        }

        public String pattern() { return pattern; }
        public Page page() { return page; }
    }

    private static final List<Route> table = new ArrayList<>();
    private static RouteTable.Matcher matcher = RouteTable.defaultMatcher();
    private static HTMLElement container;
    private static Map<String, String> currentParams = Map.of();
    private static String currentPath = "/";

    /** Install the router; it owns the container (use instead of JReact.mount). */
    public static void install(HTMLElement root, List<Route> routes) {
        table.clear();
        table.addAll(routes);
        container = root;
        Window.current().addEventListener("popstate", e -> show());
        show();
    }

    public static void setMatcher(RouteTable.Matcher next) { matcher = next == null ? RouteTable.defaultMatcher() : next; }

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
                    org.teavm.jso.dom.events.Event e = Attr.currentEvent();
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
        RouteTable t = new RouteTable(matcher);
        for (Route r : table) t.register(r.pattern(), r.page());
        RouteTable.RouteMatch m = t.resolve(path);
        currentPath = path;
        if (m != null) {
            currentParams = Map.copyOf(m.params());
            JReact.renderExternal(m.page().render(currentParams), container);
        } else {
            currentParams = Map.of();
            JReact.renderExternal(El.text("404 — no route for " + path), container);
        }
    }
}
