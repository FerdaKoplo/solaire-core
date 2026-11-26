package dev.jreact.dom;

import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.xml.Node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import dev.jreact.dom.nodes.VNode;
import dev.jreact.patterns.strategy.RenderScheduler;

/**
 * Entry point. Owns: root render, priority scheduling, mount/update.
 *
 * <p>Mental model: one render = one pass over the VNode tree. Urgent state
 * changes coalesce into one render via {@link RenderScheduler#scheduleUrgent}.
 * Updates inside {@link #startTransition(Runnable)} stay queued at lower
 * priority so urgent renders always preempt and commit first.
 */
public final class JReact {
    private JReact() {}

    /** Render scheduling priority lane. */
    public enum Priority { URGENT, TRANSITION }

    /** Handle returned by {@code scope.useTransition()}. */
    public static final class Transition {
        private boolean pending;

        public boolean isPending() { return pending; }

        /** Run {@code work} as a concurrent transition while marking {@link #isPending()}. */
        public void start(Runnable work) {
            if (work == null) return;
            pending = true;
            activeTransitions.add(this);
            requestRender();
            startTransition(work);
            if (!hasPendingTransitions()) {
                pending = false;
                activeTransitions.remove(this);
            }
        }

        void complete() { pending = false; }
    }

    private static Supplier<VNode> root;
    private static Node container;
    private static VNode current;
    private static boolean urgentScheduled = false;
    private static boolean transitionScheduled = false;
    private static int transitionDepth = 0;
    private static final Set<State<?>> pendingTransitionStates = new LinkedHashSet<>();
    private static final Set<Transition> activeTransitions = new LinkedHashSet<>();

    public static void mount(FC.NoProps component, HTMLElement el) {
        mount(component, (Node) el);
    }

    public static void mount(FC.NoProps component, Node el) {
        mount((owner, props) -> component.render(owner), null, el);
    }

    public static <P> void mount(FC<P> component, P props, HTMLElement el) {
        mount(component, props, (Node) el);
    }

    public static <P> void mount(FC<P> component, P props, Node el) {
        Renderer.gc(Set.of());
        root = () -> El.component(component, props);
        container = el;
        current = null;
        renderNow();
    }

    /** Reset root mount and pending queues (useful for tests). */
    public static void unmount() {
        root = null;
        container = null;
        current = null;
        urgentScheduled = false;
        transitionScheduled = false;
        transitionDepth = 0;
        for (State<?> s : pendingTransitionStates) s.clearTransition();
        pendingTransitionStates.clear();
        for (Transition t : activeTransitions) t.complete();
        activeTransitions.clear();
        Renderer.gc(Set.of());
    }

    /** Used by Router to push an externally-computed tree through the same diff pipeline. */
    static void renderExternal(VNode tree, HTMLElement el) {
        Supplier<VNode> prevRoot = root;
        root = () -> tree;
        container = el;
        renderNow();
        root = prevRoot;
    }

    /** Execute {@code work} so any state updates inside it run at transition priority. */
    public static void startTransition(Runnable work) {
        if (work == null) return;
        transitionDepth++;
        try {
            work.run();
        } finally {
            transitionDepth--;
        }
    }

    static boolean isInTransition() { return transitionDepth > 0; }

    static void enqueueTransitionState(State<?> state) {
        pendingTransitionStates.add(state);
    }

    private static boolean hasPendingTransitions() {
        for (State<?> s : pendingTransitionStates) {
            if (s.hasTransitionValue()) return true;
        }
        return false;
    }

    /** Coalesce urgent {@code set()} calls into a single re-render. */
    public static void requestRender() {
        if (urgentScheduled || root == null) return;
        urgentScheduled = true;
        RenderScheduler.get().scheduleUrgent(JReact::runUrgent);
    }

    /** Schedule a transition-priority render (runs after urgent updates). */
    public static void requestTransitionRender() {
        if (transitionScheduled || root == null) return;
        transitionScheduled = true;
        RenderScheduler.get().scheduleTransition(JReact::runTransition);
    }

    private static void runUrgent() {
        urgentScheduled = false;
        if (root != null) renderNow();
    }

    private static void runTransition() {
        // Urgent work always preempts pending transitions.
        if (urgentScheduled) {
            RenderScheduler.get().scheduleTransition(JReact::runTransition);
            return;
        }
        transitionScheduled = false;
        if (root == null) return;
        commitTransitions();
        renderNow();
    }

    /** Force synchronous urgent render (tests, router transitions). */
    public static void flush() {
        if (root == null) return;
        urgentScheduled = false;
        renderNow();
    }

    /** Flush pending urgent and transition updates synchronously (tests). */
    public static void flushTransitions() {
        if (root == null) return;
        urgentScheduled = false;
        transitionScheduled = false;
        commitTransitions();
        renderNow();
    }

    private static void commitTransitions() {
        for (State<?> s : List.copyOf(pendingTransitionStates)) {
            s.commitTransition();
        }
        pendingTransitionStates.clear();
        for (Transition t : List.copyOf(activeTransitions)) {
            t.complete();
        }
        activeTransitions.clear();
    }

    private static void renderNow() {
        if (root == null || container == null) return;
        QueryBridge.beginRender();
        VNode raw = root.get();
        Set<String> live = new HashSet<>();
        VNode resolved = Renderer.resolve(raw, "root", live);
        Renderer.gc(live);
        QueryBridge.endRenderGc();
        if (current == null) {
            Renderer.mount(resolved, container);
        } else {
            Renderer.update(container, resolved, current);
        }
        current = resolved;
    }

    /** Indirection so dom package doesn't import data package directly. */
    static final class QueryBridge {
        static void beginRender() { dev.jreact.data.Query.beginRender(); }
        static void endRenderGc() { dev.jreact.data.Query.endRenderGc(); }
    }
}
