package dev.jreact.ui;

/** Plain CSS classnames. Pair with Ui.css(). */
final class CssClassnames implements Classnames {
    @Override public String appShell() { return "app-shell"; }
    @Override public String sidebar() { return "sidebar"; }
    @Override public String navItem(boolean active) { return "nav-item" + (active ? " active" : ""); }
    @Override public String main() { return "main"; }
    @Override public String card() { return "card"; }
    @Override public String btn(String variant) { return "btn btn-" + variant; }
    @Override public String input() { return "input"; }
    @Override public String label() { return "label"; }
    @Override public String table() { return "table"; }
    @Override public String tableHead() { return "table-head"; }
    @Override public String tableRow(boolean striped) { return "table-row" + (striped ? " striped" : ""); }
    @Override public String badge(String variant) { return "badge badge-" + variant; }
    @Override public String tabs() { return "tabs"; }
    @Override public String tab(boolean active) { return "tab" + (active ? " active" : ""); }
    @Override public String modalBackdrop() { return "modal-backdrop"; }
    @Override public String modalPanel() { return "modal-panel"; }
    @Override public String toast(String variant) { return "toast toast-" + variant; }
    @Override public String fieldRow() { return "field-row"; }
    @Override public String fieldError() { return "field-error"; }
    @Override public String empty() { return "empty"; }
    @Override public String spinner() { return "spinner"; }
}
