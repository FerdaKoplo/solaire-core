package dev.jreact.dom;

import java.util.HashMap;
import java.util.Map;

/**
 * Attribute/event builder. Chainable.
 * mental model: props -> attr(), events -> on*(), key -> key().
 * Inside an on("input") handler, Attr.inputValue() gives the input's value;
 * inside an onClick handler, Attr.currentEvent() gives the DOM event.
 */
public final class Attr {
    private static final ThreadLocal<String> LAST_INPUT = new ThreadLocal<>();
    private static final ThreadLocal<org.teavm.jso.dom.events.Event> CURRENT_EVENT = new ThreadLocal<>();

    private final Map<String, String> props;
    private final Map<String, Runnable> events;
    private final String key;

    private Attr(Map<String, String> props, Map<String, Runnable> events, String key) {
        this.props = props;
        this.events = events;
        this.key = key;
    }

    public static Attr of() { return new Attr(Map.of(), Map.of(), null); }

    static void setLastInput(String v) { LAST_INPUT.set(v); }
    static void clearLastInput() { LAST_INPUT.remove(); }
    static void setCurrentEvent(org.teavm.jso.dom.events.Event e) { CURRENT_EVENT.set(e); }
    static void clearCurrentEvent() { CURRENT_EVENT.remove(); }

    /** Value of the input that fired the current event ("" outside input events). */
    public static String inputValue() {
        String v = LAST_INPUT.get();
        return v == null ? "" : v;
    }

    /** The DOM event currently being dispatched (null outside an event handler). */
    public static org.teavm.jso.dom.events.Event currentEvent() { return CURRENT_EVENT.get(); }

    public Attr attr(String name, String value) {
        if (value == null) return this;
        Map<String, String> next = new HashMap<>(props);
        next.put(name, value);
        return new Attr(next, events, key);
    }

    public Attr cls(String... classes) { return attr("class", String.join(" ", classes)); }

    public Attr clsAdd(String extra) {
        String prev = props.get("class");
        return attr("class", prev == null || prev.isEmpty() ? extra : prev + " " + extra);
    }

    public Attr style(String css) { return attr("style", css); }
    public Attr id(String id) { return attr("id", id); }
    public Attr type(String t) { return attr("type", t); }
    public Attr placeholder(String p) { return attr("placeholder", p); }
    public Attr value(String v) { return attr("value", v); }
    public Attr name(String n) { return attr("name", n); }
    public Attr href(String h) { return attr("href", h); }
    public Attr src(String s) { return attr("src", s); }
    public Attr alt(String a) { return attr("alt", a); }
    public Attr title(String t) { return attr("title", t); }
    public Attr forId(String htmlFor) { return attr("for", htmlFor); }
    public Attr min(String m) { return attr("min", m); }
    public Attr max(String m) { return attr("max", m); }
    public Attr step(String s) { return attr("step", s); }
    public Attr rows(int n) { return attr("rows", String.valueOf(n)); }
    public Attr disabled(boolean d) { return d ? attr("disabled", "disabled") : this; }
    public Attr checked(boolean c) { return c ? attr("checked", "checked") : this; }
    public Attr hidden(boolean h) { return h ? attr("hidden", "hidden") : this; }
    public Attr required(boolean r) { return r ? attr("required", "required") : this; }
    public Attr readonly(boolean r) { return r ? attr("readonly", "readonly") : this; }

    public Attr onClick(Runnable r) { return on("click", r); }
    public Attr onInput(Runnable r) { return on("input", r); }
    public Attr onChange(Runnable r) { return on("change", r); }
    public Attr onSubmit(Runnable r) { return on("submit", r); }
    public Attr onBlur(Runnable r) { return on("blur", r); }
    public Attr onFocus(Runnable r) { return on("focus", r); }
    public Attr onKeyDown(Runnable r) { return on("keydown", r); }
    public Attr onMouseEnter(Runnable r) { return on("mouseenter", r); }
    public Attr onMouseLeave(Runnable r) { return on("mouseleave", r); }

    public Attr on(String event, Runnable handler) {
        Map<String, Runnable> next = new HashMap<>(events);
        next.put(event, handler);
        return new Attr(props, next, key);
    }

    public Attr key(String key) { return new Attr(props, events, key); }

    Map<String, String> props() { return props; }
    Map<String, Runnable> events() { return events; }
    String key() { return key; }
}
