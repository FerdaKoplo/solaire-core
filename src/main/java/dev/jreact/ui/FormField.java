package dev.jreact.ui;

import dev.jreact.dom.Attr;
import dev.jreact.dom.El;
import dev.jreact.dom.State;
import dev.jreact.dom.nodes.VNode;

import java.util.function.Consumer;

import static dev.jreact.dom.H.el;

/**
 * Label + input + optional error text. Controlled by a {@code State<String>}.
 *
 * {@code State<String> email = scope.useState("");}
 *   Ui.field("Email", email, v -> { if (!v.contains("@")) return "Invalid email"; return null; })
 */
public final class FormField {

    public static VNode field(String labelText, State<String> value, Validator validator) {
        Classnames cn = Classnames.get();
        String error = validator == null ? null : validator.validate(value.get());
        return el("div").cls(cn.fieldRow()).kids(
                el("label").cls(cn.label()).kids(labelText),
                el("input")
                        .attr("type", "text")
                        .cls(cn.input())
                        .onInput(() -> value.set(Attr.inputValue()))
                        .attr("value", value.get()),
                error == null ? El.text("") : el("div").cls(cn.fieldError()).kids(error).end()
        ).end();
    }

    @FunctionalInterface
    public interface Validator {
        /** Return error text, or null when valid. */
        String validate(String value);
    }

    private FormField() {}
}
