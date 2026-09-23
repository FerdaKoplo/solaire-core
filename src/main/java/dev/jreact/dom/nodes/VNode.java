package dev.jreact.dom.nodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public sealed interface VNode permits TextVNode, ElementVNode, ComponentVNode {
    String key();
}