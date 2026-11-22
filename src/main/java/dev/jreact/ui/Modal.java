package dev.jreact.ui;

import dev.jreact.dom.Attr;
import dev.jreact.dom.El;
import dev.jreact.dom.nodes.VNode;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static dev.jreact.dom.H.el;

/**
 * Modal dialog controlled by a boolean State. Render when open, else nothing.
 *
 *   State<Boolean> open = scope.useState(false);
 *   Ui.modal(open, "Confirm", () -> body(), () -> footer())
 */
public final class Modal {

    public static VNode modal(dev.jreact.dom.State<Boolean> open, String title,
                              Supplier<VNode> body, Supplier<VNode> footer) {
        if (!Boolean.TRUE.equals(open.get())) return El.text("");
        Classnames cn = Classnames.get();
        return el("div").cls(cn.modalBackdrop())
                .onClick(() -> open.set(false))
                .kids(
                        el("div").cls(cn.modalPanel())
                                .onClick(() -> { Attr.currentEvent(); })
                                .kids(
                                        el("h2").kids(title),
                                        el("div").cls("modal-body").kids(body.get()),
                                        el("div").cls("modal-footer").kids(footer.get())
                                )
                ).end();
    }

    private Modal() {}
}
