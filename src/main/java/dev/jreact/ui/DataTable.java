package dev.jreact.ui;

import dev.jreact.dom.Attr;
import dev.jreact.dom.nodes.VNode;

import java.util.List;
import java.util.function.Function;

import static dev.jreact.dom.H.el;

/**
 * Data table: columns + rows, optional striped rows, empty-state slot.
 *
 *   Ui.table(List.of(Col.of("Name", u -> u.name()), Col.of("Role", u -> u.role())),
 *            users, "No users yet")
 */
public final class DataTable {

    public record Col<T>(String title, Function<T, Object> cell) {
        public static <T> Col<T> of(String title, Function<T, Object> cell) {
            return new Col<>(title, cell);
        }
    }

    public static <T> VNode table(List<Col<T>> cols, List<T> rows, String emptyText) {
        Classnames cn = Classnames.get();
        if (rows == null || rows.isEmpty()) {
            return el("div").cls(cn.empty()).kids(emptyText == null ? "Nothing here" : emptyText).end();
        }
        return el("table").cls(cn.table()).kids(
                el("thead").cls(cn.tableHead()).kids(
                        el("tr").kids(cols.stream().map(c -> el("th").kids(c.title()).end()).toList())),
                el("tbody").kids(rows.stream()
                        .map(r -> el("tr").cls(cn.tableRow(rows.indexOf(r) % 2 == 1))
                                .kids(cols.stream().map(c -> el("td").kids(c.cell().apply(r)).end()).toList())
                                .end())
                        .toList())
        ).end();
    }

    private DataTable() {}
}
