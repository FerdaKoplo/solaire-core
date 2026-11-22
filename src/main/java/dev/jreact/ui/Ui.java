package dev.jreact.ui;

import dev.jreact.dom.Attr;
import dev.jreact.dom.El;
import dev.jreact.dom.nodes.VNode;

import static dev.jreact.dom.H.el;

/**
 * Small primitives: buttons, badges, cards, spinner, empty state, toasts.
 */
public final class Ui {

    /** Button. variant: "primary" | "secondary" | "danger" | "ghost". */
    public static VNode button(String label, String variant, Runnable onClick) {
        Classnames cn = Classnames.get();
        return el("button").cls(cn.btn(variant)).onClick(onClick).kids(label).end();
    }

    public static VNode buttonPrimary(String label, Runnable onClick) { return button(label, "primary", onClick); }

    public static VNode buttonDanger(String label, Runnable onClick) { return button(label, "danger", onClick); }

    /** Card with title and body nodes. */
    public static VNode card(String title, Object... body) {
        Classnames cn = Classnames.get();
        return el("div").cls(cn.card()).kids(
                title == null ? El.text("") : el("h3").kids(title),
                el("div").cls("card-body").kids(body)
        ).end();
    }
    /** Inline status badge. variant: "default" | "success" | "warning" | "danger". */
    public static VNode badge(String text, String variant) {
        return el("span").cls(Classnames.get().badge(variant)).kids(text).end();
    }

    public static VNode spinner() {
        return el("span").cls(Classnames.get().spinner()).end();
    }

    public static VNode empty(String text) {
        return el("div").cls(Classnames.get().empty()).kids(text).end();
    }

    /** Toast notification. variant: "info" | "success" | "error". */
    public static VNode toast(String text, String variant) {
        return el("div").cls(Classnames.get().toast(variant)).kids(text).end();
    }

    private Ui() {}
}
