package dev.jreact.dom;

import dev.jreact.dom.Renderer;
import dev.jreact.dom.VNode;

import org.teavm.jso.dom.html.HTMLElement;

public class JReact {
    private static Component rootComponent;
    private static HTMLElement rootElement;
    private static VNode currentTree;

    public static void mount(Component component, HTMLElement container) {
        rootComponent = component;
        rootElement = container;
        render();
    }

    public static void render() {
        if (rootComponent != null && rootElement != null) {
            VNode newTree = rootComponent.render();

            if (currentTree == null) {
                Renderer.mount(newTree, rootElement);
            } else {
                Renderer.updateElement(rootElement, newTree, currentTree, 0);
            }

            currentTree = newTree;
        }
    }
}
