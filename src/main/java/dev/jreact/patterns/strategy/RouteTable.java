package dev.jreact.patterns.strategy;

import dev.jreact.dom.Router;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Dictionary of route patterns plus a pluggable matching strategy. Patterns are
 * kept in insertion order; resolve() walks the dictionary and returns the first
 * match.
 */
public final class RouteTable {

    @FunctionalInterface
    public interface Matcher {
        Map<String, String> match(String[] segments, String path);
    }

    private static final Matcher DEFAULT_MATCHER = (segments, path) -> {
        String[] parts = path.split("/");
        if (parts.length != segments.length) return null;
        Map<String, String> params = new LinkedHashMap<>();
        for (int i = 0; i < segments.length; i++) {
            String s = segments[i];
            if (s.startsWith(":")) params.put(s.substring(1), parts[i]);
            else if (!s.equals(parts[i])) return null;
        }
        return params;
    };

    public static Matcher defaultMatcher() { return DEFAULT_MATCHER; }

    private record Entry(String[] segments, Router.Page page) {}

    private final Map<String, Entry> routes = new LinkedHashMap<>();
    private final Matcher matcher;

    public RouteTable() { this(DEFAULT_MATCHER); }

    public RouteTable(Matcher matcher) { this.matcher = matcher; }

    public void register(String pattern, Router.Page page) {
        routes.put(pattern, new Entry(pattern.split("/"), page));
    }

    public RouteMatch resolve(String path) {
        for (Map.Entry<String, Entry> e : routes.entrySet()) {
            Map<String, String> params = matcher.match(e.getValue().segments(), path);
            if (params != null) return new RouteMatch(e.getKey(), e.getValue().page(), params);
        }
        return null;
    }

    public record RouteMatch(String pattern, Router.Page page, Map<String, String> params) {}
}
