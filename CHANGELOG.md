# Changelog

All notable changes to this project are documented here. Format follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/); versions use
[SemVer](https://semver.org/).

## Unreleased

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

### Fixed
- Keyed sibling reorder now moves DOM nodes instead of patching by index.
- `Renderer.setFactory` previously no-op: `El`/`H` hardcoded
  `NodeFactory.getDefault()`. They now go through the active factory.
- `BrowserDomOps`/`FakeDom` compile and honor the `DomOps` contract on the JVM.

## 1.3.1

- Existing baseline (see git history).