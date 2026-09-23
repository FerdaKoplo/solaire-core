package dev.jreact.dom;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import dev.jreact.hooks.HookContext;

public class ComponentTree {
    private final Map<String, HookContext> contexts = new HashMap<>();

    public HookContext contextFor(String path) {
        return contexts.computeIfAbsent(path, p -> new HookContext());
    }

    public void gc(Set<String> livePaths) {
        contexts.keySet().retainAll(livePaths);
    }
}
