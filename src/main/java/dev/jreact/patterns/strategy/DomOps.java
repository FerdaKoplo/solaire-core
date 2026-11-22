package dev.jreact.patterns.strategy;

import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.xml.Node;
import org.teavm.jso.dom.xml.NodeList;

/**
 * Strategy for all DOM mutation/access used by the Renderer. The browser
 * implementation delegates to TeaVM's DOM. Tests can supply a fake tree and
 * run reconciliation logic on the JVM.
 *
 * <p>Kept as an interface (strategy) so the renderer never touches a concrete
 * DOM object; the browser seam is selected by {@link #get()}. {@link #use}
 * overrides it (pass {@code null} to restore the default).
 */
public interface DomOps {

    /* --- creation --- */

    Node createText(String text);

    Node createElement(String tag);

    /* --- structure --- */

    void appendChild(Node parent, Node child);

    void insertBefore(Node parent, Node child, Node before);

    void replaceChild(Node parent, Node newChild, Node oldChild);

    void removeChild(Node parent, Node child);

    NodeList<? extends Node> childNodes(Node parent);

    /* --- content + attributes --- */

    void setText(Node textNode, String value);

    void setAttribute(Node el, String name, String value);

    void removeAttribute(Node el, String name);

    void addListener(Node el, String event, EventListener<?> handler);

    /* --- root --- */

    void clear(Node container);

    void appendToRoot(Node container, Node child);

    public static DomOps get() {
        return Holder.current();
    }

    /** Override; pass null to restore the default (FakeDom on JVM, browser DOM in browser). */
    public static void use(DomOps ops) {
        Holder.current = ops;
    }

    final class Holder {
        private static DomOps current;

        private Holder() {}

        private static DomOps current() {
            if (current == null) current = EffectScheduler.isBrowser() ? new BrowserDomOps() : new FakeDom();
            return current;
        }
    }
}
