# Changelog

All notable changes to this project are documented here. Format follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/); versions use
[SemVer](https://semver.org/).

## 1.4.0 - 2026-11-26

### Added
- Concurrent transitions: `JReact.startTransition(Runnable)`,
  `scope.useTransition()` (`Transition.isPending()` / `Transition.start(...)`),
  `scope.useDeferredValue(T)`, `JReact.flushTransitions()`, `JReact.unmount()`,
  and `RenderScheduler` seam (`URGENT` lane preempts `TRANSITION` lane).
- Keyed `ProviderVNode` (`El.provider(ctx, value, key, children)` and
  `H.provider(...)`).
- `setup-tea-party/` npm CLI package to scaffold `solaire-core` + TeaVM + Vite
  + Tailwind CSS v4 applications.

### Fixed
- `JReact.mount` now resolves the root component via `El.component(...)` so
  root component state persists across re-renders.
- Nested `Context` providers supplying `null` now override outer providers and
  restore outer values cleanly on exit.

## 1.3.2 - 2026-11-25

### Added
- `El.list(tag, items, keyFn, renderFn)` — keyed repeatable lists; keys keep
  the reconciler's move-not-rebuild path.
- `Context` + `El.provider(...)` + `scope.useContext(...)` — dependency
  injection down the tree without prop drilling; unwritten contexts fall back
  to their default.
- Headless DOM strategy: `Renderer` now runs all DOM mutation through
  `DomOps`; `FakeDom` (JVM) enables reconciliation + perf tests without a
  browser.
- `Classnames.tea()` — warm amber/cream/sage "tea party" Tailwind theme.
- CI: `.github/workflows/ci.yml` runs `./gradlew test` on push/PR.
- MIT license.
- `EffectScheduler` is now a proper seam: `EffectScheduler.use(...)`
  overrides effect scheduling (tests queue manually), `null` restores the
  default (async after paint in browser, inline on the JVM).
- `Query.useStore(Store)` / `getStore()` — cache + clock swapped per store;
  `null` restores the default (fresh cache, system clock). Cache entries use
  `store.now()` instead of `System.currentTimeMillis()` so tests control
  staleness deterministically.

### Changed
- Automatic publishing: `.github/workflows/publish.yml` runs `test` -> `com.vanniktech.maven.publish` to the Sonatype Central Publisher Portal when a GitHub release is created (auto-publishes to Maven Central after validation passes; set the `publish` dispatch input to `false` to stage instead). `gradle.properties` documents signing/token env (never hardcoded).
### Fixed
- Keyed sibling reorder now moves DOM nodes instead of patching by index.
- `Renderer.setFactory` previously no-op: `El`/`H` hardcoded
  `NodeFactory.getDefault()`. They now go through the active factory.
- `BrowserDomOps`/`FakeDom` compile and honor the `DomOps` contract on the JVM.

## 1.3.1

- Existing baseline (see git history).