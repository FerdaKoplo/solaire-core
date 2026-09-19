package dev.jreact.dom;

import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.xml.Node;
import org.teavm.jso.dom.xml.NodeList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// what they actually used for? 
// create real doms nodes from VNodes
// diff old vs new VNodes and patch the DOM in place

public class Renderer {

    private Renderer() {
    }

    private static final HTMLDocument document = HTMLDocument.current();

    public static Node renderNode(VNode vnode) {
        if (vnode.isTextNode()) {
            return document.createTextNode(vnode.text);
        }
        HTMLElement element = document.createElement(vnode.tagName);

        for (Map.Entry<String, String> prop : vnode.props.entrySet()) {
            element.setAttribute(prop.getKey(), prop.getValue());
        }

        for (Map.Entry<String, Runnable> event : vnode.events.entrySet()) {
            EventListener<Event> listener = evt -> event.getValue().run();
            element.addEventListener(event.getKey(), listener);
            vnode.attachedListeners.put(event.getKey(), listener);
        }

        for (VNode child : vnode.children) {
            element.appendChild(renderNode(child));
        }

        return element;
    }

    public static void mount(VNode vnode, HTMLElement container) {
        container.setInnerHTML("");
        container.appendChild(renderNode(vnode));
    }

    private static void updateChildren(Node parent, List<VNode> newChildren, List<VNode> oldChildren) {
        boolean keyed = !oldChildren.isEmpty() && oldChildren.get(0).key != null;

        if (!keyed) {
            int newLength = newChildren.size();
            int oldLength = oldChildren.size();
            for (int i = 0; i < newLength || i < oldLength; i++) {
                updateElement(parent, i < newLength ? newChildren.get(i) : null,
                        i < oldLength ? oldChildren.get(i) : null, i);
            }
            return;
        }

        Map<String, VNode> oldByKey = new HashMap<>();
        Map<String, Integer> oldIndexByKey = new HashMap<>();
        for (int i = 0; i < oldChildren.size(); i++) {
            oldByKey.put(oldChildren.get(i).key, oldChildren.get(i));
            oldIndexByKey.put(oldChildren.get(i).key, i);
        }

        NodeList<? extends Node> domChildren = parent.getChildNodes();
        Node refNode = null;
        for (int i = newChildren.size() - 1; i >= 0; i--) {
            VNode newChild = newChildren.get(i);
            VNode oldChild = oldByKey.get(newChild.key);
            Node domNode;
            if (oldChild != null) {
                Integer oldIdx = oldIndexByKey.get(newChild.key);
                domNode = domChildren.item(oldIdx);
                if (isChanged(newChild, oldChild)) {
                    Node fresh = renderNode(newChild);
                    parent.replaceChild(fresh, domNode);
                    domNode = fresh;
                } else if (!newChild.isTextNode()) {
                    patchProps((HTMLElement) domNode, newChild, oldChild);
                    updateChildren(domNode, newChild.children, oldChild.children);
                }
                oldByKey.remove(newChild.key);
            } else {
                domNode = renderNode(newChild);
            }
            parent.insertBefore(domNode, refNode);
            refNode = domNode;
        }

        // remove any old nodes not reused
        for (String leftoverKey : oldByKey.keySet()) {
            Node stale = domChildren.item(oldIndexByKey.get(leftoverKey));
            if (stale != null && stale.getParentNode() == parent) {
                parent.removeChild(stale);
            }
        }
    }

    public static void updateElement(Node parent, VNode newNode, VNode oldNode, int index) {
        NodeList<? extends Node> childNodes = parent.getChildNodes();

        if (oldNode == null) {
            parent.appendChild(renderNode(newNode));
        } else if (newNode == null) {
            parent.removeChild(childNodes.item(index));
        } else if (isChanged(newNode, oldNode)) {
            parent.replaceChild(renderNode(newNode), childNodes.item(index));
        } else if (!newNode.isTextNode()) {
            Node domElement = childNodes.item(index);
            patchProps((HTMLElement) domElement, newNode, oldNode);
            updateChildren(domElement, newNode.children, oldNode.children);
        }
    }

    private static boolean isChanged(VNode node1, VNode node2) {
        if (node1.isTextNode() != node2.isTextNode()) {
            return true;
        }
        if (node1.isTextNode() && node2.isTextNode()) {
            return !node1.text.equals(node2.text);
        }
        return !node1.tagName.equals(node2.tagName);
    }

    private static void patchProps(HTMLElement element, VNode newNode, VNode oldNode) {
        for (String key : oldNode.props.keySet()) {
            if (!newNode.props.containsKey(key)) {
                element.removeAttribute(key);
            }
        }
        for (Map.Entry<String, String> entry : newNode.props.entrySet()) {
            String oldVal = oldNode.props.get(entry.getKey());
            if (!entry.getValue().equals(oldVal)) {
                element.setAttribute(entry.getKey(), entry.getValue());
            }
        }

        for (Map.Entry<String, EventListener<?>> entry : oldNode.attachedListeners.entrySet()) {
            if (!newNode.events.containsKey(entry.getKey())) {
                element.removeEventListener(entry.getKey(), entry.getValue());
            }
        }
        for (Map.Entry<String, Runnable> entry : newNode.events.entrySet()) {
            EventListener<?> old = oldNode.attachedListeners.get(entry.getKey());
            if (old != null) {
                element.removeEventListener(entry.getKey(), old);
            }
            EventListener<Event> listener = evt -> entry.getValue().run();
            element.addEventListener(entry.getKey(), listener);
            newNode.attachedListeners.put(entry.getKey(), listener);
        }
    }

}
