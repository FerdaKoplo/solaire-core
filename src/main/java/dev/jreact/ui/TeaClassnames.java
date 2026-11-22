package dev.jreact.ui;

/**
 * Tea-party themed Tailwind classnames: warm browns, cream, sage, clotted
 * cream. Paired with Tailwind. Call {@code Classnames.use(Classnames.tea())}
 * once at startup.
 */
final class TeaClassnames implements Classnames {

    private static final String CREAM = "bg-amber-50 text-amber-950";
    private static final String BROWN = "bg-amber-900 text-amber-50";
    private static final String SAGE = "bg-emerald-700 text-emerald-50";
    private static final String BUTTER = "bg-yellow-300 text-amber-950";
    private static final String CRUST = "border-amber-200";
    private static final String STEEP = "focus:ring-amber-700";

    @Override public String appShell() { return "min-h-screen flex " + CREAM; }
    @Override public String sidebar() { return "w-56 shrink-0 " + BROWN + " p-4 space-y-1"; }
    @Override public String navItem(boolean active) {
        return "block px-3 py-2 rounded-md text-sm " +
                (active ? "bg-amber-700 text-amber-50" : "hover:bg-amber-800 text-amber-200");
    }
    @Override public String main() { return "flex-1 p-6 space-y-4"; }
    @Override public String card() { return "rounded-lg " + CRUST + " bg-white p-4 shadow-sm"; }
    @Override public String btn(String variant) {
        String base = "inline-flex items-center justify-center rounded-md px-3 py-1.5 text-sm font-medium transition ";
        switch (variant) {
            case "primary": return base + SAGE + " hover:bg-emerald-800";
            case "danger": return base + "bg-red-600 text-white hover:bg-red-700";
            case "ghost": return base + "bg-transparent hover:bg-amber-100";
            default: return base + CRUST + " bg-white hover:bg-amber-50";
        }
    }
    @Override public String input() {
        return "w-full rounded-md " + CRUST + " px-3 py-1.5 text-sm bg-white focus:outline-none " + STEEP;
    }
    @Override public String label() { return "block text-sm font-medium text-amber-900 mb-1"; }
    @Override public String table() { return "w-full text-sm border-collapse"; }
    @Override public String tableHead() { return "bg-amber-100 text-left font-semibold"; }
    @Override public String tableRow(boolean striped) {
        return striped ? "bg-amber-50" : "bg-white";
    }
    @Override public String badge(String variant) {
        String base = "inline-block rounded-full px-2 py-0.5 text-xs font-medium ";
        switch (variant) {
            case "success": return base + "bg-emerald-100 text-emerald-900";
            case "danger": return base + "bg-red-100 text-red-900";
            case "warning": return base + BUTTER;
            default: return base + "bg-amber-100 text-amber-900";
        }
    }
    @Override public String tabs() { return "flex gap-1 " + CRUST; }
    @Override public String tab(boolean active) {
        return "px-3 py-2 text-sm -mb-px border-b-2 " +
                (active ? "border-amber-900 text-amber-900 font-medium" : "border-transparent text-amber-600 hover:text-amber-800");
    }
    @Override public String modalBackdrop() { return "fixed inset-0 bg-amber-950/50 flex items-center justify-center z-50"; }
    @Override public String modalPanel() { return "bg-white rounded-lg p-6 w-full max-w-md shadow-xl " + CRUST; }
    @Override public String toast(String variant) {
        String base = "fixed bottom-4 right-4 px-4 py-2 rounded-md text-sm text-white shadow-lg z-50 ";
        switch (variant) {
            case "error": return base + "bg-red-600";
            case "success": return base + SAGE;
            default: return base + BROWN;
        }
    }
    @Override public String fieldRow() { return "mb-3"; }
    @Override public String fieldError() { return "mt-1 text-xs text-red-700"; }
    @Override public String empty() { return "p-6 text-center text-sm text-amber-700"; }
    @Override public String spinner() { return "inline-block h-4 w-4 animate-spin rounded-full border-2 border-amber-200 border-t-amber-900"; }
}