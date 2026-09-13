package dev.jreact.dom;

import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.xml.Node;
import org.teavm.jso.dom.xml.NodeList;

import java.util.Map;

public class Renderer {

    private static final HTMLDocument document = HTMLDocument.current();

    public static Node renderNode(VNode vnode){
        if (vnode.isTextNode()) {
            return document.createTextNode(vnode.text);
        }
        HTMLElement element = document.createElement(vnode.tagName);

        for (Map.Entry<String, String> prop : vnode.props.entrySet()){
            element.setAttribute(prop.getKey(), prop.getValue());
        }

        for (VNode child : vnode.children) {
            element.appendChild(renderNode(child));
        }

        for (Map.Entry<String, String> prop : vnode.props.entrySet()) {
            element.setAttribute(prop.getKey(), prop.getValue());
        }

        for (Map.Entry<String, Runnable> event : vnode.events.entrySet()) {
            element.addEventListener(event.getKey(), evt -> event.getValue().run());
        }

        return element;
    }

    public static void mount(VNode vnode, HTMLElement container) {
        container.setInnerHTML(""); 
        container.appendChild(renderNode(vnode));
    }

    public static void updateElement(Node parent, VNode newNode, VNode oldNode, int index) {
        NodeList<? extends Node> childNodes = parent.getChildNodes();

        //if the old node is null, we'll create new node 
        if (oldNode == null) {
            parent.appendChild(renderNode(newNode));
        }

        else if (newNode == null) {
            parent.removeChild(childNodes.item(index));
        }

        else if (isChanged(newNode, oldNode)) {
            parent.replaceChild(renderNode(newNode), childNodes.item(index));
        }

        else if (!newNode.isTextNode()) {
            Node domElement = childNodes.item(index);
            int newLength = newNode.children.size();
            int oldLength = oldNode.children.size();
            
            for (int i = 0; i < newLength || i < oldLength; i++) {
                updateElement(
                    domElement,
                    i < newLength ? newNode.children.get(i) : null,
                    i < oldLength ? oldNode.children.get(i) : null,
                    i
                );
            }
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
    
}
