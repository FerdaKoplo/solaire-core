package dev.jreact.ui;

import dev.jreact.dom.Renderer;
import dev.jreact.dom.State;
import dev.jreact.dom.nodes.ElementVNode;
import dev.jreact.dom.nodes.VNode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UiTest {

    @Test
    void appShellRendersSidebarAndContent() {
        Renderer.Instance inst = new Renderer.Instance();
        Renderer.RenderScope scope = new Renderer.RenderScope(inst);
        VNode shell = AppShell.appShell(scope, "My App",
                List.of(AppShell.NavItem.item("/users", "Users")), () -> Ui.card("Hello"));
        ElementVNode root = (ElementVNode) shell;
        assertEquals("div", root.tagName());
        assertEquals(2, root.children().size()); // aside + main
    }

    @Test
    void dataTableShowsEmptyState() {
        VNode t = DataTable.table(List.of(DataTable.Col.of("A", x -> x)), List.of(), "none");
        ElementVNode e = (ElementVNode) t;
        assertEquals("div", e.tagName());
        assertTrue(((dev.jreact.dom.nodes.TextVNode) e.children().get(0)).text().contains("none"));
    }

    @Test
    void tabsSwitchContent() {
        Renderer.Instance inst = new Renderer.Instance();
        Renderer.RenderScope scope = new Renderer.RenderScope(inst);
        VNode t = Tabs.tabs(scope, List.of(
                Tabs.Tab.of("A", () -> Ui.badge("first", "success")),
                Tabs.Tab.of("B", () -> Ui.badge("second", "danger"))));
        assertEquals("div", ((ElementVNode) t).tagName());
        assertEquals(2, ((ElementVNode) t).children().size());
    }

    @Test
    void classnamesSwitch() {
        Classnames.use(Classnames.css());
        String cssBtn = Classnames.get().btn("primary");
        Classnames.use(Classnames.tailwind());
        String twBtn = Classnames.get().btn("primary");
        assertTrue(cssBtn.contains("btn-primary"));
        assertTrue(twBtn.contains("bg-blue-600"));
        Classnames.use(Classnames.css());
    }

    @Test
    void uiPrimitivesBuildNodes() {
        assertEquals("button", ((ElementVNode) Ui.buttonPrimary("go", () -> {})).tagName());
        assertEquals("span", ((ElementVNode) Ui.badge("ok", "success")).tagName());
        assertEquals("div", ((ElementVNode) Ui.empty("nothing")).tagName());
    }
}
