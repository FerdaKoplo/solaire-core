package dev.jreact.dom.elements;

import java.util.HashMap;
import java.util.Map;

public class Attrs {
    private Attrs() {}
     public String key;
    public final Map<String, String> props = new HashMap<>();
    public final Map<String, Runnable> events = new HashMap<>();

    public static Attrs of() { return new Attrs(); }

    public Attrs key(String key) { this.key = key; return this; }
    public Attrs prop(String name, String value) { props.put(name, value); return this; }
    public Attrs on(String event, Runnable handler) { events.put(event, handler); return this; }
    
}
