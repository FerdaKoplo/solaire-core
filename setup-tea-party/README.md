# setup-tea-party

Scaffold a [solaire-core](https://github.com/FerdaKoplo/solaire-core) (`io.github.ferdakoplo:solaire-core`) web app powered by **TeaVM**, **Vite**, and **Tailwind CSS v4**.

## Usage

```bash
npx setup-tea-party my-app
```

Or non-interactively with flags:

```bash
npx setup-tea-party my-app --package com.example.myapp --theme tea --yes
```

### Options

- `-p, --package <pkg>`: Java base package (default: `com.example.<project>`)
- `-t, --theme <tea|tailwind|css>`: `Classnames` preset (`tea`, `tailwind`, or `css`)
- `--solaire-version <version>`: `solaire-core` Maven version (default: `1.4.1`)
- `-y, --yes`: Accept defaults without prompting
- `-f, --force`: Scaffold into a non-empty directory
- `-h, --help`: Show usage
- `-v, --version`: Show CLI version

## What it creates

- Gradle project (`build.gradle`, `settings.gradle`, and bundled `./gradlew` wrapper) configured with `org.teavm` `0.15.0` (`ES2015` output) and `io.github.ferdakoplo:solaire-core`
- Starter `App.java` + `Main.java` using `JReact.mount`, `AppShell`, `Ui`, and `Classnames`
- JVM unit test (`AppTest.java`) using `Renderer.RenderScope` + JUnit 5
- Vite dev/build setup (`package.json`, `vite.config.js`, `index.html`, `src/main/web/main.js`, `src/main/web/style.css`)
