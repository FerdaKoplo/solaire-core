# solaire-core (jreact)

A small React-shaped UI core for TeaVM, but **not a React clone**. The goal is a
simple mental model, not API parity.

## Mental model

React says: *components are functions, state lives in hooks, re-render the tree,
diff it.* That's accurate but the machinery (hook slot arrays, rules-of-hooks,
useMemo/useCallback/useRef, separate reconciler + renderer) is heavy.

Here:

```
component  =  (scope, props) -> Node        // pure function
state      =  scope.useState(initial)        // one cell per call position
update     =  cell.set(value | fn)          // marks dirty, one render per microtask
render     =  walk Node tree once, diff old vs new, patch DOM
```

- A component is a **pure function** of props + its own `State` cells. No hooks
  library, no hook order rules to learn: call `scope.use(...)` unconditionally,
  same order each render, and slots persist by tree position.
- State is a single `State<T>` cell. `.get()` reads, `.set(v)` or `.set(fn)`
  writes and schedules **one batched render per tick** (`JReact.requestRender`).
- Nodes are **immutable values**. `ElementVNode` with equal tag/key/props/
  children is reused; only changed props/children touch the DOM.
- Built-in libraries that React needs packages for are in core:
  - `dev.jreact.data.Query` — TanStack-Query-shaped cache (`use`/`invalidate`/`set`/`mutate`)
  - `dev.jreact.dom.Router` — hash router (`install`, `route`, `navigate`, `link`)
  - `dev.jreact.data.Http` — fetch wrapper (GET/POST/PUT/PATCH/DELETE)
  - `dev.jreact.dom.State` — app-scoped observable cells (counter, flag, list, store)

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

## Data fetching (TanStack-style)

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
Query.set("users", list -> updated);    // optimistic local update
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

## Design notes / tradeoffs

- Simplified on purpose: one global render queue, no concurrent mode, no
  Suspense, no context provider tree. Add when a real app needs them.
- `State.set` bails out on equal values (same as React's state bailout).
- Router is hash-based to keep it dependency-free; swap `show()` internals if
  you need history API later.
