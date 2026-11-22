package dev.jreact.dom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.html.HTMLInputElement;
import org.teavm.jso.dom.xml.Node;
import org.teavm.jso.dom.xml.NodeList;

import dev.jreact.dom.nodes.ComponentVNode;
import dev.jreact.dom.nodes.ElementVNode;
import dev.jreact.dom.nodes.TextVNode;
import dev.jreact.dom.nodes.VNode;
import dev.jreact.patterns.factory.NodeFactory;
import dev.jreact.patterns.strategy.EffectScheduler;
import dev.jreact.patterns.strategy.PropStrategies;

/**
 * Reconciliation. Two jobs:
 *  1. resolve ComponentVNodes (call FC with a RenderScope) into elements
 *  2. diff old vs new VNode trees, patch real DOM in place
 *
 * mental model:
 *  - a component is a function (scope, props) -> Node
 *  - state lives in State slots keyed by tree position; survives re-renders
 *  - set() marks the app dirty; one render per microtask total (batching)
 *  - ElementVNode with equal value -> DOM untouched (structural sharing)
 */
public final class Renderer {

    private Renderer() {}

    private static final HTMLDocument document = HTMLDocument.current();

    /** Component instances by tree path. Dropped when a path disappears (unmount). */
    private static final Map<String, Instance> instances = new HashMap<>();

    /** Node construction strategy (abstract factory). */
    private static NodeFactory factory = NodeFactory.getDefault();

    public static void setFactory(NodeFactory next) { factory = next == null ? NodeFactory.getDefault() : next; }

    public static NodeFactory factory() { return factory; }

    /** Per-tree-position state: hook slots plus cleanup callbacks. */
    public static final class Instance {
        private final List<State<?>> states = new ArrayList<>();
        private final List<Runnable> cleanups = new ArrayList<>();

        <T> State<T> slot(int index, T initial) {
            if (index < states.size()) return cast(states.get(index));
            State<T> s = new State<>(initial, JReact::requestRender);
            states.add(s);
            return s;
        }

        void onCleanup(Runnable cleanup) { cleanups.add(cleanup); }

        void dispose() {
            for (State<?> s : states) if (s.rawValue() instanceof EffectBox eb) eb.dispose();
            for (Runnable c : cleanups) c.run();
            cleanups.clear();
        }

        public List<State<?>> statesView() { return states; }

        @SuppressWarnings("unchecked")
        private static <T> State<T> cast(State<?> s) { return (State<T>) s; }
    }

    /** Currently rendering scope (for library hooks like Query.use). */
    private static final ThreadLocal<RenderScope> CURRENT = new ThreadLocal<>();

    public static RenderScope currentScope() { return CURRENT.get(); }

    @SuppressWarnings("unchecked")
    static VNode resolve(VNode node, String path, Set<String> livePaths) {
        if (node instanceof ComponentVNode<?> cvn) {
            livePaths.add(path);
            Instance inst = instances.computeIfAbsent(path, p -> new Instance());
            RenderScope scope = new RenderScope(inst);
            CURRENT.set(scope);
            try {
                VNode out = ((ComponentVNode<Object>) cvn).component().render(scope, cvn.componentProps());
                return resolve(out, path + "/r", livePaths);
            } finally {
                CURRENT.remove();
            }
        }
        if (node instanceof TextVNode) {
            livePaths.add(path);
            return node;
        }
        if (node instanceof ElementVNode e) {
            livePaths.add(path);
            List<VNode> kids = new ArrayList<>(e.children().size());
            for (int i = 0; i < e.children().size(); i++) {
                VNode c = e.children().get(i);
                String id = c.key() != null ? c.key() : String.valueOf(i);
                kids.add(resolve(c, path + "/" + id, livePaths));
            }
            return e.withChildren(kids);
        }
        throw new IllegalStateException("Unknown node type: " + node.getClass());
    }

    /**
     * Handed to every component render. use(initial) allocates/reuses a state
     * slot; call order is the slot identity (rules-of-hooks: call unconditionally,
     * same order every render).
     */
    public static final class RenderScope {
        private final Instance inst;
        private int cursor;

        public RenderScope(Instance inst) { this.inst = inst; }

        /** useState: one cell per call position. */
        public <T> State<T> use(T initial) {
            return inst.slot(cursor++, initial);
        }

        public <T> State<T> useState(T initial) { return use(initial); }

        /** useRef: mutable box that never triggers re-render. */
        public <T> Ref<T> useRef(T initial) {
            State<Ref<T>> s = use(new Ref<>(initial));
            return s.get();
        }

        /** Derived value recomputed when deps change (useMemo). */
        public <T> T useMemo(Function<Object[], T> compute, Object... deps) {
            State<MemoBox<T>> s = use(new MemoBox<>());
            return s.get().compute(compute, deps);
        }

        public void useEffect(Runnable effect, Object... deps) {
            useEffect(() -> { effect.run(); return null; }, deps);
        }

        /** Effect with optional cleanup, deferred to after paint (see EffectScheduler). */
        public void useEffect(EffectCallback effect, Object... deps) {
            State<EffectBox> s = use(new EffectBox());
            s.get().run(effect, deps);
        }

        /** Register cleanup to run if this component unmounts. */
        public void onCleanup(Runnable cleanup) {
            inst.onCleanup(cleanup);
        }
    }

    public static final class Ref<T> {
        private T value;

        Ref(T v) { this.value = v; }

        public T get() { return value; }

        public void set(T next) { this.value = next; }
    }

    @FunctionalInterface
    public interface EffectCallback {
        /** Return cleanup or null. */
        Runnable invoke();
    }

    static final class MemoBox<T> {
        private T value;
        private Object[] deps;
        private boolean init;

        T compute(Function<Object[], T> fn, Object... d) {
            if (!init || !java.util.Arrays.equals(deps, d)) {
                value = fn.apply(d);
                deps = java.util.Arrays.copyOf(d, d.length);
                init = true;
            }
            return value;
        }
    }

    static final class EffectBox {
        private Object[] deps;
        private Runnable cleanup;
        private boolean init;
        private boolean disposed;

        void run(EffectCallback effect, Object... d) {
            if (init && java.util.Arrays.equals(deps, d)) return;
            deps = java.util.Arrays.copyOf(d, d.length);
            init = true;
            disposed = false;
            EffectScheduler.get().schedule(() -> {
                if (disposed) return;
                if (cleanup != null) cleanup.run();
                cleanup = effect.invoke();
            });
        }

        void dispose() {
            disposed = true;
            if (cleanup != null) { cleanup.run(); cleanup = null; }
        }
    }

    /** Unmount all instances not in livePaths (component removed from tree). */
    static void gc(Set<String> livePaths) {
        for (String path : List.copyOf(instances.keySet())) {
            if (!livePaths.contains(path)) {
                Instance inst = instances.remove(path);
                if (inst != null) inst.dispose();
            }
        }
    }

    static void mount(VNode node, HTMLElement container) {
        container.setInnerHTML("");
        container.appendChild(buildDom(node));
    }

    static void update(HTMLElement container, VNode next, VNode prev) {
        updateChildren(container, List.of(next), prev == null ? List.of() : List.of(prev));
    }

    private static Node buildDom(VNode node) {
        if (node instanceof TextVNode t) return document.createTextNode(t.text());
        ElementVNode e = (ElementVNode) node;
        HTMLElement el = document.createElement(e.tagName());
        for (Map.Entry<String, String> p : e.props().entrySet())
            PropStrategies.forName(p.getKey()).apply(el, p.getKey(), p.getValue());
        for (Map.Entry<String, Runnable> ev : e.events().entrySet()) {
            el.addEventListener(ev.getKey(), evt -> dispatch(ev.getValue(), evt));
        }
        for (VNode c : e.children()) el.appendChild(buildDom(c));
        return el;
    }

    private static void dispatch(Runnable handler, Event evt) {
        Attr.setCurrentEvent(evt);
        if (evt != null && evt.getTarget() instanceof HTMLInputElement input) {
            Attr.setLastInput(input.getValue());
        }
        try { handler.run(); }
        finally {
            Attr.clearLastInput();
            Attr.clearCurrentEvent();
        }
    }

    private static void updateChildren(Node parent, List<VNode> next, List<VNode> prev) {
        Map<String, Integer> prevByKey = new HashMap<>();
        for (int i = 0; i < prev.size(); i++) {
            VNode c = prev.get(i);
            if (c.key() != null) prevByKey.put(c.key(), i);
        }
        NodeList<? extends Node> dom = parent.getChildNodes();
        for (int i = 0; i < next.size(); i++) {
            VNode n = next.get(i);
            Integer oldIdx = n.key() != null ? prevByKey.get(n.key()) : (i < prev.size() ? i : null);
            Node existing = i < dom.getLength() ? dom.item(i) : null;
            VNode o = oldIdx != null && oldIdx < prev.size() ? prev.get(oldIdx) : null;
            if (o == null) {
                parent.insertBefore(buildDom(n), existing);
            } else if (isSameShape(n, o)) {
                patch(existing, n, o);
            } else {
                parent.replaceChild(buildDom(n), existing);
            }
        }
        while (parent.getChildNodes().getLength() > next.size()) {
            parent.removeChild(parent.getChildNodes().item(parent.getChildNodes().getLength() - 1));
        }
    }

    private static boolean isSameShape(VNode n, VNode o) {
        if (n instanceof TextVNode || o instanceof TextVNode)
            return n instanceof TextVNode && o instanceof TextVNode;
        ElementVNode a = (ElementVNode) n, b = (ElementVNode) o;
        return a.tagName().equals(b.tagName()) && java.util.Objects.equals(a.key(), b.key());
    }

    private static void patch(Node domNode, VNode next, VNode prev) {
        if (next instanceof TextVNode t) {
            if (!t.text().equals(((TextVNode) prev).text())) domNode.setNodeValue(t.text());
            return;
        }
        ElementVNode ne = (ElementVNode) next, oe = (ElementVNode) prev;
        HTMLElement el = (HTMLElement) domNode;
        for (String k : oe.props().keySet())
            if (!ne.props().containsKey(k)) el.removeAttribute(k);
        for (Map.Entry<String, String> p : ne.props().entrySet()) {
            String old = oe.props().get(p.getKey());
            if (!p.getValue().equals(old))
                PropStrategies.forName(p.getKey()).apply(el, p.getKey(), p.getValue());
        }
        updateChildren(el, ne.children(), oe.children());
    }
}
