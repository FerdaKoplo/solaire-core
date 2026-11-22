package dev.jreact.patterns.strategy;

import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.xml.Attr;
import org.teavm.jso.dom.xml.Element;
import org.teavm.jso.dom.xml.NamedNodeMap;
import org.teavm.jso.dom.xml.Node;
import org.teavm.jso.dom.xml.NodeList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Fake DOM tree for JVM tests. Nodes are plain objects implementing the TeaVM
 * Node/NodeList interfaces, so reconciliation can be driven and asserted
 * without a browser.
 */
public final class FakeDom implements DomOps {

    public static class FakeNode implements Node {

        public final String kind;        // "text" | "element" | "attr"
        public String name;              // text content, or tag name

        public FakeNode parent;
        public final List<FakeNode> children = new ArrayList<>();
        public final Map<String, String> attrs = new LinkedHashMap<>();
        public final Map<String, EventListener<?>> listeners = new LinkedHashMap<>();

        FakeNode(String kind, String name, FakeNode parent) {
            this.kind = kind;
            this.name = name;
            this.parent = parent;
        }

        @Override public String getNodeName() { return "text".equals(kind) ? "#text" : name; }

        @Override public String getNodeValue() { return name; }

        @Override public void setNodeValue(String value) {
            if ("text".equals(kind)) name = value;
        }

        @Override public short getNodeType() { return "text".equals(kind) ? Node.TEXT_NODE : Node.ELEMENT_NODE; }

        @Override public Node getParentNode() { return parent; }

        @Override public NodeList<Node> getChildNodes() { return list(children); }

        @Override public Node getFirstChild() { return children.isEmpty() ? null : children.get(0); }

        @Override public Node getLastChild() { return children.isEmpty() ? null : children.get(children.size() - 1); }

        @Override public Node getPreviousSibling() {
            if (parent == null) return null;
            int i = parent.children.indexOf(this);
            return i > 0 ? parent.children.get(i - 1) : null;
        }

        @Override public Node getNextSibling() {
            if (parent == null) return null;
            int i = parent.children.indexOf(this);
            return i >= 0 && i < parent.children.size() - 1 ? parent.children.get(i + 1) : null;
        }

        @Override public NamedNodeMap<Attr> getAttributes() { return new FakeNamedNodeMap(attrs); }

        @Override public Node insertBefore(Node newChild, Node refChild) {
            FakeNode c = fake(newChild);
            if (refChild == null) {
                children.add(c);
            } else {
                children.add(children.indexOf(fake(refChild)), c);
            }
            c.parent = this;
            return c;
        }

        @Override public Node replaceChild(Node newChild, Node oldChild) {
            FakeNode n = fake(newChild), o = fake(oldChild);
            children.set(children.indexOf(o), n);
            n.parent = this;
            return o;
        }

        @Override public Node removeChild(Node oldChild) {
            FakeNode o = fake(oldChild);
            children.remove(o);
            o.parent = null;
            return o;
        }

        @Override public Node appendChild(Node newChild) { return insertBefore(newChild, null); }

        @Override public boolean hasChildNodes() { return !children.isEmpty(); }

        @Override public boolean hasChildNodesJS() { return hasChildNodes(); }

        @Override public Node cloneNode(boolean deep) {
            FakeNode copy = new FakeNode(kind, name, null);
            copy.attrs.putAll(attrs);
            if (deep) for (FakeNode c : children) copy.appendChild(c.cloneNode(true));
            return copy;
        }

        @Override public void normalize() {}

        @Override public boolean isSupported(String feature, String version) { return false; }

        @Override public boolean hasAttributes() { return !attrs.isEmpty(); }

        @Override public String getNamespaceURI() { return null; }

        @Override public String getPrefix() { return null; }

        @Override public void setPrefix(String prefix) {}

        @Override public String getLocalName() { return name; }

        @Override public String getTextContent() { return "text".equals(kind) ? name : null; }

        @Override public void setTextContent(String textContent) {
            if ("text".equals(kind)) name = textContent;
        }

        @Override public org.teavm.jso.dom.xml.Document getOwnerDocument() { return null; }
    }

    private static final class FakeAttr extends FakeNode implements Attr {

        FakeAttr(String name, String value) { super("attr", value, null); this.name = value; }

        @Override public String getName() { return kind.equals("attr") ? name : name; }

        @Override public boolean isSpecified() { return true; }

        @Override public String getValue() { return name; }

        @Override public void setValue(String value) { name = value; }

        @Override public Element getOwnerElement() { return null; }

        @Override public String getNodeName() { return name; }

        @Override public String getNodeValue() { return name; }
    }

    private static final class FakeNamedNodeMap implements NamedNodeMap<Attr> {

        private final Map<String, String> attrs;

        FakeNamedNodeMap(Map<String, String> attrs) { this.attrs = attrs; }

        @Override public Attr getNamedItem(String name) { return null; }

        @Override public Attr setNamedItem(Attr arg) { throw new UnsupportedOperationException("read-only fake"); }

        @Override public Attr removeNamedItem(String name) { throw new UnsupportedOperationException("read-only fake"); }

        @Override public Attr item(int index) { throw new UnsupportedOperationException("read-only fake"); }

        @Override public int getLength() { return attrs.size(); }

        @Override public Attr getNamedItemNS(String namespaceURI, String localName) { return null; }

        @Override public Attr setNamedItemNS(Attr arg) { throw new UnsupportedOperationException("read-only fake"); }

        @Override public Attr removeNamedItemNS(String namespaceURI, String localName) {
            throw new UnsupportedOperationException("read-only fake");
        }

        @Override public Attr get(int index) { throw new UnsupportedOperationException("read-only fake"); }
    }

    private static final class FakeNodeList implements NodeList<Node> {

        private final List<Node> items;

        FakeNodeList(List<? extends Node> items) { this.items = new ArrayList<>(items); }

        @Override public Node item(int i) { return items.get(i); }

        @Override public Node get(int i) { return items.get(i); }

        @Override public int getLength() { return items.size(); }
    }

    private static NodeList<Node> list(List<? extends Node> items) { return new FakeNodeList(items); }

    private static FakeNode fake(Node n) {
        if (n instanceof FakeNode f) return f;
        throw new IllegalArgumentException("not a fake node: " + n);
    }

    public FakeNode root;

    public int creates;
    public int appends;
    public int inserts;
    public int removals;
    public int textSets;
    public int attrSets;
    public int attrRemovals;
    public int listenerAdds;

    @Override public Node createText(String text) { creates++; return new FakeNode("text", text, null); }

    @Override public Node createElement(String tag) { creates++; return new FakeNode("element", tag, null); }

    @Override public void appendChild(Node parent, Node child) { appends++; fake(parent).appendChild(child); }

    @Override public void insertBefore(Node parent, Node child, Node before) {
        inserts++;
        fake(parent).insertBefore(child, before);
    }

    @Override public void replaceChild(Node parent, Node newChild, Node oldChild) { fake(parent).replaceChild(newChild, oldChild); }

    @Override public void removeChild(Node parent, Node child) { removals++; fake(parent).removeChild(child); }

    @Override public NodeList<? extends Node> childNodes(Node parent) { return list(fake(parent).children); }

    @Override public void setText(Node textNode, String value) { textSets++; fake(textNode).setNodeValue(value); }

    @Override public void setAttribute(Node el, String name, String value) { attrSets++; fake(el).attrs.put(name, value); }

    @Override public void removeAttribute(Node el, String name) { attrRemovals++; fake(el).attrs.remove(name); }

    @Override public void addListener(Node el, String event, EventListener<?> handler) {
        listenerAdds++;
        fake(el).listeners.put(event, handler);
    }

    @Override public void clear(Node container) {
        FakeNode c = fake(container);
        c.children.clear();
        root = c;
    }

    @Override public void appendToRoot(Node container, Node child) { appends++; fake(container).appendChild(child); }
}
