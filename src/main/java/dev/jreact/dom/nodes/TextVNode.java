package dev.jreact.dom.nodes;

public record TextVNode(String text) implements VNode {
    @Override
    public String key() {
        return null;
    }
}
