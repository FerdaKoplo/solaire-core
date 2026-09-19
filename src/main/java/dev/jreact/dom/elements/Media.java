package dev.jreact.dom.elements;

import java.util.HashMap;
import java.util.Map;

import dev.jreact.dom.VNode;

public class Media {
    public static VNode img(Attrs attrs) {
        return Core.h("img", attrs);
    }

    public static VNode source(Attrs attrs) {
        return Core.h("source", attrs);
    }

    public static VNode track(Attrs attrs) {
        return Core.h("track", attrs);
    }

    public static VNode video(VNode... children) {
        return Core.h("video", null, children);
    }

    public static VNode video(Attrs attrs, VNode... children) {
        return Core.h("video", attrs, children);
    }

    public static VNode audio(VNode... children) {
        return Core.h("audio", null, children);
    }

    public static VNode audio(Attrs attrs, VNode... children) {
        return Core.h("audio", attrs, children);
    }

    public static VNode picture(VNode... children) {
        return Core.h("picture", null, children);
    }

    public static VNode picture(Attrs attrs, VNode... children) {
        return Core.h("picture", attrs, children);
    }

    public static VNode canvas(VNode... children) {
        return Core.h("canvas", null, children);
    }

    public static VNode canvas(Attrs attrs, VNode... children) {
        return Core.h("canvas", attrs, children);
    }

    public static VNode iframe(VNode... children) {
        return Core.h("iframe", null, children);
    }

    public static VNode iframe(Attrs attrs, VNode... children) {
        return Core.h("iframe", attrs, children);
    }
}
