package dev.jreact.ui;

/** Tailwind utility classnames. Requires Tailwind (CDN or build step). */
final class TailwindClassnames implements Classnames {
    @Override public String appShell() { return "min-h-screen flex bg-gray-50 text-gray-900"; }
    @Override public String sidebar() { return "w-56 shrink-0 bg-gray-900 text-gray-100 p-4 space-y-1"; }
    @Override public String navItem(boolean active) {
        return "block px-3 py-2 rounded-md text-sm " +
                (active ? "bg-gray-700 text-white" : "hover:bg-gray-800 text-gray-300");
    }
    @Override public String main() { return "flex-1 p-6 space-y-4"; }
    @Override public String card() { return "rounded-lg border border-gray-200 bg-white p-4 shadow-sm"; }
    @Override public String btn(String variant) {
        String base = "inline-flex items-center justify-center rounded-md px-3 py-1.5 text-sm font-medium transition ";
        switch (variant) {
            case "primary": return base + "bg-blue-600 text-white hover:bg-blue-700";
            case "danger": return base + "bg-red-600 text-white hover:bg-red-700";
            case "ghost": return base + "bg-transparent hover:bg-gray-100";
            default: return base + "border border-gray-300 bg-white hover:bg-gray-50";
        }
    }
    @Override public String input() {
        return "w-full rounded-md border border-gray-300 px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500";
    }
    @Override public String label() { return "block text-sm font-medium text-gray-700 mb-1"; }
    @Override public String table() { return "w-full text-sm border-collapse"; }
    @Override public String tableHead() { return "bg-gray-100 text-left font-semibold"; }
    @Override public String tableRow(boolean striped) {
        return striped ? "bg-gray-50" : "bg-white";
    }
    @Override public String badge(String variant) {
        String base = "inline-block rounded-full px-2 py-0.5 text-xs font-medium ";
        switch (variant) {
            case "success": return base + "bg-green-100 text-green-800";
            case "danger": return base + "bg-red-100 text-red-800";
            case "warning": return base + "bg-yellow-100 text-yellow-800";
            default: return base + "bg-gray-100 text-gray-800";
        }
    }
    @Override public String tabs() { return "flex gap-1 border-b border-gray-200"; }
    @Override public String tab(boolean active) {
        return "px-3 py-2 text-sm -mb-px border-b-2 " +
                (active ? "border-blue-600 text-blue-600 font-medium" : "border-transparent text-gray-500 hover:text-gray-700");
    }
    @Override public String modalBackdrop() { return "fixed inset-0 bg-black/40 flex items-center justify-center z-50"; }
    @Override public String modalPanel() { return "bg-white rounded-lg p-6 w-full max-w-md shadow-xl"; }
    @Override public String toast(String variant) {
        String base = "fixed bottom-4 right-4 px-4 py-2 rounded-md text-sm text-white shadow-lg z-50 ";
        switch (variant) {
            case "error": return base + "bg-red-600";
            case "success": return base + "bg-green-600";
            default: return base + "bg-gray-800";
        }
    }
    @Override public String fieldRow() { return "mb-3"; }
    @Override public String fieldError() { return "mt-1 text-xs text-red-600"; }
    @Override public String empty() { return "p-6 text-center text-sm text-gray-500"; }
    @Override public String spinner() { return "inline-block h-4 w-4 animate-spin rounded-full border-2 border-gray-300 border-t-blue-600"; }
}
