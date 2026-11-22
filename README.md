# solaire-core (jreact)

A small React-shaped UI core for TeaVM. Components are functions, state is stored
in per-call slots, rendering diffs the new node tree against the previous one and
patches the DOM.

## How it works

```
component  =  (scope, props) -> Node
state      =  scope.useState(initial)        // one cell per call position
update     =  cell.set(value | fn)          // marks dirty, one render per microtask
render     =  walk Node tree once, diff old vs new, patch DOM
```

- A component is a function of props plus its own `State` cells. Call
  `scope.use(...)` unconditionally, in the same order each render; slots persist
  by tree position.
- `State<T>` is one cell. `.get()` reads; `.set(v)` or `.set(fn)` writes and
  schedules one batched render per tick (`JReact.requestRender`). Setting an
  equal value is a no-op.
- Nodes are immutable values. An `ElementVNode` with equal tag/key/props/children
  is reused; only changed props/children touch the DOM.
- Included in core:
  - `dev.jreact.data.Query` — async cache (`use`/`invalidate`/`set`/`mutate`)
  - `dev.jreact.dom.Router` — hash router (`install`, `route`, `navigate`, `link`)
  - `dev.jreact.data.Http` — fetch wrapper (GET/POST/PUT/PATCH/DELETE)
  - `dev.jreact.dom.State` — app-scoped observable cells

## Built-in UI library (`dev.jreact.ui`)

Ready-made components for quick frontends. Styling is pluggable: call
`Classnames.use(Classnames.css())` for plain CSS,
`Classnames.use(Classnames.tailwind())` for Tailwind utilities, or
`Classnames.use(Classnames.tea())` for a warm tea-party Tailwind theme
(amber/cream/sage). Default is plain CSS.

```java
Classnames.use(Classnames.tailwind());  // once at startup

JReact.mount((scope, props) -> AppShell.appShell(scope, "Admin",
        List.of(AppShell.NavItem.item("/users", "Users")),
        () -> Ui.card("Users",
            DataTable.table(
                List.of(DataTable.Col.of("Name", u -> u.name()),
                        DataTable.Col.of("Role", u -> Ui.badge(u.role(), "success"))),
                users, "No users yet"),
            Ui.buttonPrimary("Add user", () -> open.set(true))),
), document.getElementById("app"));
```

Components: `AppShell` (sidebar + main), `DataTable` (columns/rows/empty
state), `Tabs`, `Modal`, `FormField`, and `Ui` primitives (`button`,
`buttonPrimary`, `buttonDanger`, `card`, `badge`, `spinner`, `empty`, `toast`).

## Seams

Extension points, all swappable without touching call sites. Pattern types live
in `dev.jreact.patterns` so the core stays uncluttered:

- `dev.jreact.patterns.factory.NodeFactory` (abstract factory) — how `El` builds
  `TextVNode`/`ElementVNode`/`ComponentVNode`. Override for instrumentation or
  alternative node types; set via `Renderer.setFactory`.
- `dev.jreact.patterns.strategy.RouteTable` (dictionary + strategy) — ordered
  pattern table with a pluggable `RouteTable.Matcher`. Wired in
  `Router.setMatcher`, or construct a table directly and `resolve(path)`.
- `dev.jreact.patterns.strategy.PropStrategies` (strategy dictionary) —
  attribute name to DOM write strategy (`value`/`checked` set properties,
  everything else `setAttribute`).
- `dev.jreact.patterns.strategy.DomOps` (strategy) — every DOM mutation the
  Renderer performs; `FakeDom` runs reconciliation headless on the JVM, used by
  `src/test` so the virtual DOM itself is testable without a browser.
- `dev.jreact.patterns.strategy.EffectScheduler` (strategy) — effect timing:
  `setTimeout(0)` in the browser, inline on the JVM.
- `Query.Phase` (state machine) — `LOADING -> SUCCESS | ERROR`, one transition
  per fetch result.
- `dev.jreact.patterns.observer.Subscription` (observer) —
  `Entry.subscribe(observer)` returns a handle; `unsubscribe()` detaches.
- `Renderer.Instance` — per-tree-position slot list plus cleanup list; slots
  addressed by call order, disposed on unmount.

## Write a screen

Two call styles. Pick one per file; they produce identical nodes and can mix.

Fluent builder (`H`) — reads like nested HTML, one line per level:

```java
import static dev.jreact.dom.H.el;

public final class CounterPage implements FC.NoProps {
    @Override public VNode render(Renderer.RenderScope scope) {
        State<Integer> count = scope.useState(0);
        return el("div").cls("card").kids(
            el("h1").kids("Count: ", count.get()),
            el("button").onClick(() -> count.set(v -> v + 1)).kids("+1"),
            el("button").onClick(() -> count.set(0)).kids("reset")
        ).end();
    }
}
```

Static factory (`El`) — compact, argument-list style:

```java
return El.div(Attr.cls("card"),
    El.h1(null, "Count: ", String.valueOf(count.get())),
    El.button("+1", () -> count.set(v -> v + 1)),
    El.button("reset", () -> count.set(0))
);
```

Entry point (either style):

```java
JReact.mount(new CounterPage(), document.getElementById("app"));
```

## Data fetching

```java
var users = Query.use("users", (onData, onError) ->
        Http.get("/api/users", res -> res.ok()
                ? onData.accept(res.body())
                : onError.accept(res.body())));

if (users.isLoading()) return El.text("loading…");
if (users.error() != null) return El.text("error: " + users.error());
return El.ul(null, users.data().stream().map(u -> El.li(null, u)).toArray(VNode[]::new));
```

```java
Query.invalidate("users");              // stale + refetch active keys
Query.set("users", list -> updated);    // local update
Query.mutate("users", work, onSuccess); // mutation then invalidate
```

## Routing

```java
Router.install(document.getElementById("app"), List.of(
    Router.route("/",          () -> Home.page()),
    Router.route("/users/:id", params -> UserPage.render(params))
));

El.link("/users/7", "User 7")   // <a> with click interception
Router.navigate("/users/7")
```

## Limits

- One global render queue. No concurrent mode, no Suspense, no context provider
  tree.
- Router is hash-based; replace `show()` internals to use the history API.
