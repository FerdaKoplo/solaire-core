package dev.jreact.dom.elements;

import java.util.HashMap;
import java.util.Map;

import dev.jreact.dom.VNode;

public class Media {
   public static VNode img(Map<String, String> props) {
        return Core.h("img", props, new HashMap<>());
    }

    public static VNode img(Map<String, String> props, Map<String, Runnable> events) {
        return Core.h("img", props, events);
    }

    public static VNode video(VNode... children) {
        return Core.h("video", new HashMap<>(), new HashMap<>(), children);
    }

    public static VNode video(Map<String, String> props, VNode... children) {
        return Core.h("video", props, new HashMap<>(), children);
    }

    public static VNode video(Map<String, String> props, Map<String, Runnable> events, VNode... children) {
        return Core.h("video", props, events, children);
    }

    public static VNode audio(VNode... children) {
        return Core.h("audio", new HashMap<>(), new HashMap<>(), children);
    }

    public static VNode audio(Map<String, String> props, VNode... children) {
        return Core.h("audio", props, new HashMap<>(), children);
    }

    public static VNode audio(Map<String, String> props, Map<String, Runnable> events, VNode... children) {
        return Core.h("audio", props, events, children);
    }

    public static VNode source(Map<String, String> props) {
        return Core.h("source", props, new HashMap<>());
    }

    public static VNode track(Map<String, String> props) {
        return Core.h("track", props, new HashMap<>());
    }

    public static VNode picture(VNode... children) {
        return Core.h("picture", new HashMap<>(), new HashMap<>(), children);
    }

    public static VNode picture(Map<String, String> props, VNode... children) {
        return Core.h("picture", props, new HashMap<>(), children);
    }

    public static VNode canvas(Map<String, String> props, VNode... children) {
        return Core.h("canvas", props, new HashMap<>(), children);
    }

    public static VNode canvas(Map<String, String> props, Map<String, Runnable> events, VNode... children) {
        return Core.h("canvas", props, events, children);
    }

    public static VNode iframe(Map<String, String> props, VNode... children) {
        return Core.h("iframe", props, new HashMap<>(), children);
    } 
}
