package dev.jreact.dom;

import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.xml.Node;
import org.teavm.jso.dom.xml.NodeList;

import dev.jreact.dom.nodes.ElementVNode;
import dev.jreact.dom.nodes.TextVNode;
import dev.jreact.dom.nodes.VNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

// what they actually used for? 
// create real doms nodes from VNodes
// diff old vs new VNodes and patch the DOM in place

public class Renderer {

    private Renderer() {
    }

    // Debug switch: flip to true (or set from DevTools if exported) to trace diffs.
    public static boolean DEBUG = true;

    private static final HTMLDocument document = HTMLDocument.current();

    private static final Map<Node, Map<String, EventListener<?>>> listenerRegistry = new WeakHashMap<>();

    public static Node renderNode(VNode vnode) {
        if (vnode instanceof TextVNode t) {
            return document.createTextNode(t.text());
        }
        if (vnode instanceof ElementVNode e) {
            HTMLElement element = document.createElement(e.tagName());

            // Debug fingerprint: match this DOM node back to its VNode in DevTools.
            if (DEBUG) {
                element.setAttribute("data-vn", e.tagName() + (e.key() != null ? "#" + e.key() : "") + "@" + System.identityHashCode(e));
            }

            for (Map.Entry<String, String> prop : e.props().entrySet()) {
                element.setAttribute(prop.getKey(), prop.getValue());
            }

            Map<String, EventListener<?>> attached = new HashMap<>();
            for (Map.Entry<String, Runnable> ev : e.events().entrySet()) {
                EventListener<Event> listener = evt -> ev.getValue().run();
                element.addEventListener(ev.getKey(), listener);
                attached.put(ev.getKey(), listener);
            }
            listenerRegistry.put(element, attached);

            for (VNode child : e.children()) {
                element.appendChild(renderNode(child));
            }
            return element;
        }
        // every ComponentVNode before Renderer sees the tree. If this fires, something
        // skipped resolution.
        throw new IllegalStateException("Unresolved ComponentVNode reached Renderer: " + vnode);
    }

    public static void mount(VNode vnode, HTMLElement container) {
        container.setInnerHTML("");
        container.appendChild(renderNode(vnode));
    }

    public static void updateElement(Node parent, VNode newNode, VNode oldNode, int index) {
        NodeList<? extends Node> childNodes = parent.getChildNodes();

        if (oldNode == null) {
            // Insert at `index`, not append: growth in the middle/end must keep order.
            Node ref = index < childNodes.getLength() ? childNodes.item(index) : null;
            parent.insertBefore(renderNode(newNode), ref);
        } else if (newNode == null) {
            parent.removeChild(childNodes.item(index));
        } else if (isChanged(newNode, oldNode)) {
            parent.replaceChild(renderNode(newNode), childNodes.item(index));
        } else if (newNode instanceof ElementVNode newE && oldNode instanceof ElementVNode oldE) {
            Node domElement = childNodes.item(index);
            patchProps((HTMLElement) domElement, newE, oldE);
            updateChildren(domElement, newE.children(), oldE.children());
        }
    }

    private static void updateChildren(Node parent, List<VNode> newChildren, List<VNode> oldChildren) {
        // Keyed path only when BOTH sides are fully keyed. Mixed keyed/unkeyed
        // would put null keys in the map, so fall back to positional reconcile.
        boolean keyed = !oldChildren.isEmpty() && allKeyed(oldChildren) && allKeyed(newChildren);

        if (DEBUG) {
            JSConsole.log("UPDATE parent=" + parent.getNodeName()
                    + " keyed=" + keyed
                    + " old=" + keys(oldChildren)
                    + " new=" + keys(newChildren));
        }

        if (!keyed) {
            updateChildrenByIndex(parent, newChildren, oldChildren);
            return;
        }

        // Old DOM nodes keyed by VNode key, captured BEFORE any mutation. => im honestly have no idea, just something i asked from deepseek
        // insertBefore moves existing nodes, so positions shift as we go.
        Map<String, VNode> oldVNodeByKey = new HashMap<>();
        Map<String, Node> oldDomByKey = new HashMap<>();
        NodeList<? extends Node> domChildren = parent.getChildNodes();
        for (int i = 0; i < oldChildren.size(); i++) {
            String key = oldChildren.get(i).key();
            oldVNodeByKey.put(key, oldChildren.get(i));
            oldDomByKey.put(key, domChildren.item(i));
        }

        Set<String> reusedKeys = new HashSet<>();
        Node refNode = null;
        for (int i = newChildren.size() - 1; i >= 0; i--) {
            VNode newChild = newChildren.get(i);
            String key = newChild.key();
            VNode oldChild = oldVNodeByKey.get(key);
            // If reconcile builds a FRESH node (old missing, or tag/text changed),
            // the old DOM node for this key must be removed later, not kept.
            boolean fresh = oldChild == null || isChanged(newChild, oldChild);
            Node domNode = reconcile(parent, newChild, oldChild, oldDomByKey.get(key));
            if (!fresh)
                reusedKeys.add(key);
            parent.insertBefore(domNode, refNode);
            refNode = domNode;
        }

        for (Map.Entry<String, Node> old : oldDomByKey.entrySet()) {
            if (!reusedKeys.contains(old.getKey()) && old.getValue().getParentNode() == parent) {
                parent.removeChild(old.getValue());
            }
        }
    }

    private static boolean allKeyed(List<VNode> children) {
        for (VNode c : children) {
            if (c.key() == null) return false;
        }
        return true;
    }

    private static String keys(List<VNode> children) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < children.size(); i++) {
            if (i > 0) sb.append(",");
            VNode c = children.get(i);
            sb.append(c.key() != null ? c.key() : "#" + i);
            if (c instanceof TextVNode t) sb.append(":'").append(t.text()).append("'");
        }
        return sb.append("]").toString();
    }

    private static Node reconcile(Node parent, VNode newChild, VNode oldChild, Node oldDom) {
        if (oldChild == null) {
            return renderNode(newChild);
        }
        if (isChanged(newChild, oldChild)) {
            return renderNode(newChild);
        }
        if (newChild instanceof ElementVNode newE && oldChild instanceof ElementVNode oldE) {
            patchProps((HTMLElement) oldDom, newE, oldE);
            updateChildren(oldDom, newE.children(), oldE.children());
        }
        return oldDom;
    }

    private static void updateChildrenByIndex(Node parent, List<VNode> newChildren, List<VNode> oldChildren) {
        int newLength = newChildren.size();
        int oldLength = oldChildren.size();
        for (int i = 0; i < newLength; i++) {
            updateElement(parent, newChildren.get(i), i < oldLength ? oldChildren.get(i) : null, i);
        }
        NodeList<? extends Node> childNodes = parent.getChildNodes();
        while (childNodes.getLength() > newLength) {
            parent.removeChild(childNodes.item(childNodes.getLength() - 1));
        }
    }

    private static boolean isChanged(VNode node1, VNode node2) {
        if (node1 instanceof TextVNode t1 && node2 instanceof TextVNode t2) {
            return !t1.text().equals(t2.text());
        }
        if (node1 instanceof ElementVNode e1 && node2 instanceof ElementVNode e2) {
            return !e1.tagName().equals(e2.tagName());
        }
        return true;
    }

    private static void patchProps(HTMLElement element, ElementVNode newNode, ElementVNode oldNode) {
        for (String key : oldNode.props().keySet()) {
            if (!newNode.props().containsKey(key)) {
                element.removeAttribute(key);
            }
        }
        for (Map.Entry<String, String> entry : newNode.props().entrySet()) {
            String oldVal = oldNode.props().get(entry.getKey());
            if (!entry.getValue().equals(oldVal)) {
                element.setAttribute(entry.getKey(), entry.getValue());
            }
        }

        Map<String, EventListener<?>> attached = listenerRegistry.computeIfAbsent(element, k -> new HashMap<>());
        for (Map.Entry<String, EventListener<?>> entry : new HashMap<>(attached).entrySet()) {
            if (!newNode.events().containsKey(entry.getKey())) {
                element.removeEventListener(entry.getKey(), entry.getValue());
                attached.remove(entry.getKey());
            }
        }
        for (Map.Entry<String, Runnable> entry : newNode.events().entrySet()) {
            EventListener<?> old = attached.get(entry.getKey());
            if (old != null) {
                element.removeEventListener(entry.getKey(), old);
            }
            EventListener<Event> listener = evt -> entry.getValue().run();
            element.addEventListener(entry.getKey(), listener);
            attached.put(entry.getKey(), listener);
        }
    }

}
