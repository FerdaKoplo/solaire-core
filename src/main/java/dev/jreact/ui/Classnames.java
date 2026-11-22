package dev.jreact.ui;

import dev.jreact.dom.Attr;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class-name provider for built-in components. One implementation emits plain
 * CSS classes (Ui.css), another Tailwind utilities (Ui.tailwind). Components
 * call Classnames.get().btn(variant) etc., so styling can be swapped per app.
 */
public interface Classnames {

    String appShell();

    String sidebar();

    String navItem(boolean active);

    String main();

    String card();

    String btn(String variant);

    String input();

    String label();

    String table();

    String tableHead();

    String tableRow(boolean striped);

    String badge(String variant);

    String tabs();

    String tab(boolean active);

    String modalBackdrop();

    String modalPanel();

    String toast(String variant);

    String fieldRow();

    String fieldError();

    String empty();

    String spinner();

    /** Plain-CSS classnames; pair with a small stylesheet. */
    static Classnames css() { return new CssClassnames(); }

    /** Tailwind utility classnames; pair with the Tailwind CDN or build. */
    static Classnames tailwind() { return new TailwindClassnames(); }

    /** Currently installed provider. */
    static Classnames get() { return Holder.current; }

    /** Install once at startup. */
    static void use(Classnames c) { Holder.current = c == null ? Classnames.css() : c; }

    final class Holder {
        private static Classnames current = new CssClassnames();
        private Holder() {}
    }
}
