package dev.jreact.dom.elements;

import java.util.HashMap;
import java.util.Map;

import dev.jreact.dom.VNode;

public class Typography {
   public static VNode h1(VNode... children) { return Core.h("h1", new HashMap<>(), new HashMap<>(), children); }
    public static VNode h1(Map<String, String> props, VNode... children) { return Core.h("h1", props, new HashMap<>(), children); }
    
    public static VNode h2(VNode... children) { return Core.h("h2", new HashMap<>(), new HashMap<>(), children); }
    public static VNode h2(Map<String, String> props, VNode... children) { return Core.h("h2", props, new HashMap<>(), children); }
    
    public static VNode h3(VNode... children) { return Core.h("h3", new HashMap<>(), new HashMap<>(), children); }
    public static VNode h3(Map<String, String> props, VNode... children) { return Core.h("h3", props, new HashMap<>(), children); }
    
    public static VNode h4(VNode... children) { return Core.h("h4", new HashMap<>(), new HashMap<>(), children); }
    public static VNode h4(Map<String, String> props, VNode... children) { return Core.h("h4", props, new HashMap<>(), children); }
    
    public static VNode h5(VNode... children) { return Core.h("h5", new HashMap<>(), new HashMap<>(), children); }
    public static VNode h5(Map<String, String> props, VNode... children) { return Core.h("h5", props, new HashMap<>(), children); }
    
    public static VNode h6(VNode... children) { return Core.h("h6", new HashMap<>(), new HashMap<>(), children); }
    public static VNode h6(Map<String, String> props, VNode... children) { return Core.h("h6", props, new HashMap<>(), children); }

    public static VNode p(VNode... children) { return Core.h("p", new HashMap<>(), new HashMap<>(), children); }
    public static VNode p(Map<String, String> props, VNode... children) { return Core.h("p", props, new HashMap<>(), children); }

    public static VNode a(VNode... children) { return Core.h("a", new HashMap<>(), new HashMap<>(), children); }
    public static VNode a(Map<String, String> props, VNode... children) { return Core.h("a", props, new HashMap<>(), children); }

    public static VNode strong(VNode... children) { return Core.h("strong", new HashMap<>(), new HashMap<>(), children); }
    public static VNode strong(Map<String, String> props, VNode... children) { return Core.h("strong", props, new HashMap<>(), children); }
    
    public static VNode em(VNode... children) { return Core.h("em", new HashMap<>(), new HashMap<>(), children); }
    public static VNode em(Map<String, String> props, VNode... children) { return Core.h("em", props, new HashMap<>(), children); }

    public static VNode b(VNode... children) { return Core.h("b", new HashMap<>(), new HashMap<>(), children); }
    public static VNode b(Map<String, String> props, VNode... children) { return Core.h("b", props, new HashMap<>(), children); }

    public static VNode i(VNode... children) { return Core.h("i", new HashMap<>(), new HashMap<>(), children); }
    public static VNode i(Map<String, String> props, VNode... children) { return Core.h("i", props, new HashMap<>(), children); }

    public static VNode blockquote(VNode... children) { return Core.h("blockquote", new HashMap<>(), new HashMap<>(), children); }
    public static VNode blockquote(Map<String, String> props, VNode... children) { return Core.h("blockquote", props, new HashMap<>(), children); }
    
    public static VNode code(VNode... children) { return Core.h("code", new HashMap<>(), new HashMap<>(), children); }
    public static VNode code(Map<String, String> props, VNode... children) { return Core.h("code", props, new HashMap<>(), children); }
    
    public static VNode pre(VNode... children) { return Core.h("pre", new HashMap<>(), new HashMap<>(), children); }
    public static VNode pre(Map<String, String> props, VNode... children) { return Core.h("pre", props, new HashMap<>(), children); }

    public static VNode br() { return Core.h("br", new HashMap<>(), new HashMap<>()); }
    
    public static VNode hr() { return Core.h("hr", new HashMap<>(), new HashMap<>()); }
    public static VNode hr(Map<String, String> props) { return Core.h("hr", props, new HashMap<>()); } 
}
