package dev.jreact.dom;

import java.util.HashMap;
import java.util.Map;

import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.html.HTMLInputElement;

/**
 * Strategy per attribute name, held in a dictionary. Unknown names fall back to
 * setAttribute; input value/checked need property assignment.
 */
final class PropStrategies {

    @FunctionalInterface
    interface Strategy {
        void apply(HTMLElement el, String name, String value);
    }

    private static final Strategy SET_ATTRIBUTE = (el, name, value) -> el.setAttribute(name, value);
    private static final Strategy INPUT_VALUE = (el, name, value) -> ((HTMLInputElement) el).setValue(value);
    private static final Strategy INPUT_CHECKED = (el, name, value) -> ((HTMLInputElement) el).setChecked(true);

    private static final Map<String, Strategy> BY_NAME = new HashMap<>();

    static {
        BY_NAME.put("value", INPUT_VALUE);
        BY_NAME.put("checked", INPUT_CHECKED);
    }

    static Strategy forName(String name) { return BY_NAME.getOrDefault(name, SET_ATTRIBUTE); }

    private PropStrategies() {}
}
