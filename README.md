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

## Write a screen

```java
public final class CounterPage implements FC.NoProps {
    @Override public VNode render(Renderer.RenderScope scope) {
        State<Integer> count = scope.useState(0);
        return El.div(Attr.cls("card"),
            El.h1(null, "Count: ", String.valueOf(count.get())),
            El.button("+1", () -> count.set(v -> v + 1)),
            El.button("reset", () -> count.set(0))
        );
    }
}

// entry point
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
