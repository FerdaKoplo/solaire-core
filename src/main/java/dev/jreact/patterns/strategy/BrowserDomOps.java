package dev.jreact.patterns.strategy;

import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.html.HTMLInputElement;
import org.teavm.jso.dom.xml.Node;
import org.teavm.jso.dom.xml.NodeList;

/** Browser implementation of DomOps; delegates to TeaVM DOM. */
final class BrowserDomOps implements DomOps {

    private static final HTMLDocument document = HTMLDocument.current();

    @Override public Node createText(String text) { return document.createTextNode(text); }

    @Override public Node createElement(String tag) { return document.createElement(tag); }

    @Override public void appendChild(Node parent, Node child) { parent.appendChild(child); }

    @Override public void insertBefore(Node parent, Node child, Node before) { parent.insertBefore(child, before); }

    @Override public void replaceChild(Node parent, Node newChild, Node oldChild) { parent.replaceChild(newChild, oldChild); }

    @Override public void removeChild(Node parent, Node child) { parent.removeChild(child); }

    @Override public NodeList<? extends Node> childNodes(Node parent) { return parent.getChildNodes(); }

    @Override public void setText(Node textNode, String value) { textNode.setNodeValue(value); }

    @Override public void setAttribute(Node el, String name, String value) {
        if ("value".equals(name) && el instanceof HTMLInputElement input) { input.setValue(value); return; }
        if ("checked".equals(name) && el instanceof HTMLInputElement input) { input.setChecked(true); return; }
        ((HTMLElement) el).setAttribute(name, value);
    }

    @Override public void removeAttribute(Node el, String name) { ((HTMLElement) el).removeAttribute(name); }

    @Override public void addListener(Node el, String event, EventListener<?> handler) {
        ((HTMLElement) el).addEventListener(event, handler);
    }

    @Override public void clear(Node container) { ((HTMLElement) container).setInnerHTML(""); }

    @Override public void appendToRoot(Node container, Node child) { ((HTMLElement) container).appendChild(child); }
}
