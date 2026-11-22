package dev.jreact.dom.nodes;

/** Text leaf. Equal text => equal node, so unchanged text keeps its DOM node. */
public record TextVNode(String text) implements VNode {
    public TextVNode {
        if (text == null) throw new NullPointerException("text");
    }
    @Override public String key() { return null; }
}
