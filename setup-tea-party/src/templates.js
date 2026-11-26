import { toAppTitle } from './args.js';

const CLASSNAMES_CALL = {
  tea: 'Classnames.tea()',
  tailwind: 'Classnames.tailwind()',
  css: 'Classnames.css()',
};

/**
 * Generate all text files for a scaffolded Tea Party app.
 * Returns a map of relative file path -> UTF-8 string contents.
 */
export function renderTemplates({ projectName, packageName, theme, solaireVersion }) {
  const appTitle = toAppTitle(projectName);
  const pkgPath = packageName.replaceAll('.', '/');
  const usesTailwind = theme === 'tea' || theme === 'tailwind';
  const classnamesCall = CLASSNAMES_CALL[theme];

  return {
    '.gitignore': renderGitignore(),
    '.vscode/solaire.code-snippets': renderSnippets(),
    'settings.gradle': renderSettingsGradle(projectName),
    'build.gradle': renderBuildGradle({ packageName, solaireVersion }),
    'package.json': renderPackageJson({ projectName, usesTailwind }),
    'vite.config.js': renderViteConfig(usesTailwind),
    'index.html': renderIndexHtml(appTitle),
    'README.md': renderReadme({ projectName, appTitle, packageName, theme, solaireVersion }),
    'src/main/web/main.js': renderMainJs(),
    'src/main/web/style.css': renderStyleCss(usesTailwind),
    [`src/main/java/${pkgPath}/Main.java`]: renderMainJava({ packageName, classnamesCall }),
    [`src/main/java/${pkgPath}/App.java`]: renderAppJava({ packageName, appTitle, theme }),
    [`src/test/java/${pkgPath}/AppTest.java`]: renderAppTestJava({ packageName, appTitle, classnamesCall }),
  };
}

function renderGitignore() {
  return `.gradle/
build/
dist/
node_modules/
.DS_Store
.idea/
*.iml
`;
}

function renderSettingsGradle(projectName) {
  return `rootProject.name = '${projectName}'
`;
}

function renderBuildGradle({ packageName, solaireVersion }) {
  return `plugins {
    id 'java'
    id 'org.teavm' version '0.15.0'
}

group = '${packageName}'
version = '0.1.0'

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.github.ferdakoplo:solaire-core:${solaireVersion}'
    testImplementation 'org.junit.jupiter:junit-jupiter:5.11.3'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}

testing {
    suites {
        test {
            useJUnitJupiter()
        }
    }
}

teavm {
    all {
        mainClass = '${packageName}.Main'
    }
    js {
        addedToWebApp = false
        targetFileName = 'classes.js'
        moduleType = org.teavm.gradle.api.JSModuleType.ES2015
        sourceMap = true
    }
}
`;
}

function renderPackageJson({ projectName, usesTailwind }) {
  const pkg = {
    name: projectName,
    private: true,
    version: '0.1.0',
    type: 'module',
    scripts: {
      'build:java': './gradlew generateJavaScript',
      'tea-party': 'npm run build:java && vite',
      dev: 'npm run tea-party',
      build: 'npm run build:java && vite build',
      preview: 'vite preview',
      test: './gradlew test',
    },
    dependencies: {
      vite: '^8.3.0',
      ...(usesTailwind ? { '@tailwindcss/vite': '^4.3.3' } : {}),
    },
    ...(usesTailwind ? { devDependencies: { tailwindcss: '^4.3.3' } } : {}),
  };
  return JSON.stringify(pkg, null, 2) + '\n';
}

function renderViteConfig(usesTailwind) {
  if (usesTailwind) {
    return `import { defineConfig } from 'vite';
import tailwindcss from '@tailwindcss/vite';

export default defineConfig({
  plugins: [tailwindcss()],
});
`;
  }
  return `import { defineConfig } from 'vite';

export default defineConfig({});
`;
}

function renderIndexHtml(appTitle) {
  return `<!doctype html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>${appTitle}</title>
  </head>
  <body>
    <div id="app"></div>
    <script type="module" src="/src/main/web/main.js"></script>
  </body>
</html>
`;
}

function renderMainJs() {
  return `import './style.css';
import { main } from '../../../build/generated/teavm/js/classes.js';

main();
`;
}

function renderStyleCss(usesTailwind) {
  if (usesTailwind) {
    // Include TeaVM output & Java sources so Tailwind v4 scans classes inside
    // solaire-core's TeaClassnames/TailwindClassnames even though build/ is gitignored.
    return `@import "tailwindcss";
@source "../java";
@source "../../../build/generated/teavm/js/classes.js";
`;
  }
  return `:root {
  font-family: system-ui, -apple-system, sans-serif;
  color: #1f2937;
  background: #f9fafb;
}

body {
  margin: 0;
}

.app-shell {
  min-height: 100vh;
  display: flex;
}

.sidebar {
  width: 14rem;
  flex-shrink: 0;
  background: #111827;
  color: #f9fafb;
  padding: 1rem;
}

.sidebar .brand {
  font-weight: 700;
  margin-bottom: 1rem;
}

.nav-item {
  display: block;
  padding: 0.5rem 0.75rem;
  border-radius: 0.375rem;
  color: #d1d5db;
  text-decoration: none;
  font-size: 0.875rem;
}

.nav-item.active,
.nav-item:hover {
  background: #1f2937;
  color: #ffffff;
}

.main {
  flex: 1;
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.card {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 0.5rem;
  padding: 1rem 1.25rem;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
}

.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 0.375rem;
  padding: 0.45rem 0.85rem;
  font-size: 0.875rem;
  font-weight: 500;
  border: 1px solid #d1d5db;
  background: #ffffff;
  cursor: pointer;
}

.btn-primary {
  background: #2563eb;
  border-color: #2563eb;
  color: #ffffff;
}

.btn-danger {
  background: #dc2626;
  border-color: #dc2626;
  color: #ffffff;
}

.btn-ghost {
  background: transparent;
  border-color: transparent;
}

.badge {
  display: inline-block;
  border-radius: 9999px;
  padding: 0.125rem 0.5rem;
  font-size: 0.75rem;
  font-weight: 500;
  background: #f3f4f6;
  color: #1f2937;
}

.badge-success {
  background: #dcfce7;
  color: #166534;
}

.badge-warning {
  background: #fef3c7;
  color: #92400e;
}

.badge-danger {
  background: #fee2e2;
  color: #991b1b;
}

.empty {
  padding: 1.5rem;
  text-align: center;
  color: #6b7280;
  font-size: 0.875rem;
}
`;
}

function renderMainJava({ packageName, classnamesCall }) {
  return `package ${packageName};

import dev.jreact.dom.JReact;
import dev.jreact.ui.Classnames;
import org.teavm.jso.dom.html.HTMLDocument;

public final class Main {

    public static void main(String[] args) {
        Classnames.use(${classnamesCall});
        JReact.mount(new App(), HTMLDocument.current().getElementById("app"));
    }

    private Main() {}
}
`;
}

function renderAppJava({ packageName, appTitle, theme }) {
  const rowCls = theme === 'css' ? 'actions' : 'flex items-center gap-2 mt-3';
  const metaCls = theme === 'css' ? 'meta' : 'flex items-center gap-2 mb-2';

  return `package ${packageName};

import dev.jreact.dom.FC;
import dev.jreact.dom.Renderer;
import dev.jreact.dom.State;
import dev.jreact.dom.nodes.VNode;
import dev.jreact.ui.AppShell;
import dev.jreact.ui.Ui;

import java.util.List;

import static dev.jreact.dom.H.el;

public final class App implements FC.NoProps {

    @Override
    public VNode render(Renderer.RenderScope scope) {
        State<Integer> cups = scope.useState(1);

        return AppShell.appShell(
                scope,
                "${appTitle}",
                List.of(AppShell.NavItem.item("/", "Tea Party")),
                () -> Ui.card(
                        "Welcome to ${appTitle}",
                        el("div").cls("${metaCls}").kids(
                                Ui.badge("solaire-core", "success"),
                                Ui.badge("${theme} theme", "warning")
                        ).end(),
                        el("p").kids("Cups poured: ", cups.get()).end(),
                        el("div").cls("${rowCls}").kids(
                                Ui.buttonPrimary("Pour another cup", () -> cups.set(c -> c + 1)),
                                Ui.button("Reset", "ghost", () -> cups.set(0))
                        ).end()
                )
        );
    }
}
`;
}

function renderAppTestJava({ packageName, appTitle, classnamesCall }) {
  return `package ${packageName};

import dev.jreact.dom.Renderer;
import dev.jreact.dom.nodes.ElementVNode;
import dev.jreact.dom.nodes.TextVNode;
import dev.jreact.dom.nodes.VNode;
import dev.jreact.ui.Classnames;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppTest {

    @AfterEach
    void cleanup() {
        Classnames.use(null);
    }

    @Test
    void appRendersWelcomeCard() {
        Classnames.use(${classnamesCall});
        Renderer.RenderScope scope = new Renderer.RenderScope(new Renderer.Instance());

        ElementVNode root = (ElementVNode) new App().render(scope);

        assertEquals("div", root.tagName());
        assertEquals(2, root.children().size());
        assertTrue(collectText(root).contains("${appTitle}"));
    }

    private static String collectText(VNode node) {
        if (node instanceof TextVNode t) return t.text();
        if (node instanceof ElementVNode e) {
            StringBuilder sb = new StringBuilder();
            for (VNode child : e.children()) sb.append(collectText(child));
            return sb.toString();
        }
        return "";
    }
}
`;
}

function renderReadme({ projectName, appTitle, packageName, theme, solaireVersion }) {
  return `# ${appTitle}

A [solaire-core](https://github.com/FerdaKoplo/solaire-core) (\`io.github.ferdakoplo:solaire-core:${solaireVersion}\`) TeaVM + Vite application scaffolded with \`setup-tea-party\`.

- **Java package**: \`${packageName}\`
- **Theme preset**: \`${theme}\`

## Getting started

\`\`\`bash
npm install
npm run tea-party
\`\`\`

## Scripts

- \`npm run tea-party\` — Compile Java to JS with TeaVM (\`./gradlew generateJavaScript\`) and start the Vite dev server
- \`npm run build:java\` — Run \`./gradlew generateJavaScript\` only
- \`npm run build\` — Compile Java and build the production Vite bundle into \`dist/\`
- \`npm test\` — Run JUnit 5 JVM tests (\`./gradlew test\`)
`;
}

function renderSnippets() {
  return JSON.stringify(
    {
      'solaire: Functional Component (NoProps)': {
        scope: 'java',
        prefix: 'jfc',
        description: 'Create a solaire-core props-free functional component',
        body: [
          'import dev.jreact.dom.FC;',
          'import dev.jreact.dom.Renderer;',
          'import dev.jreact.dom.nodes.VNode;',
          'import static dev.jreact.dom.H.el;',
          '',
          'public final class ${1:MyComponent} implements FC.NoProps {',
          '    @Override',
          '    public VNode render(Renderer.RenderScope scope) {',
          '        return el("${2:div}").kids(',
          '            $0',
          '        ).end();',
          '    }',
          '}',
        ],
      },
      'solaire: Functional Component with Props': {
        scope: 'java',
        prefix: 'jfcp',
        description: 'Create a solaire-core functional component with typed props',
        body: [
          'import dev.jreact.dom.FC;',
          'import dev.jreact.dom.Renderer;',
          'import dev.jreact.dom.nodes.VNode;',
          'import static dev.jreact.dom.H.el;',
          '',
          'public final class ${1:MyComponent} implements FC<${2:String}> {',
          '    @Override',
          '    public VNode render(Renderer.RenderScope scope, ${2:String} props) {',
          '        return el("${3:div}").kids(',
          '            $0',
          '        ).end();',
          '    }',
          '}',
        ],
      },
      'solaire: useState': {
        scope: 'java',
        prefix: 'jstate',
        description: 'Declare a reactive State cell in RenderScope',
        body: ['State<${1:String}> ${2:value} = scope.useState(${3:""});'],
      },
      'solaire: useEffect': {
        scope: 'java',
        prefix: 'jeffect',
        description: 'Run a side effect with cleanup and dependencies',
        body: [
          'scope.useEffect(() -> {',
          '    $0',
          '    return () -> {};',
          '}, ${1:dep});',
        ],
      },
      'solaire: useMemo': {
        scope: 'java',
        prefix: 'jmemo',
        description: 'Memoize a derived value by dependencies',
        body: ['${1:String} ${2:derived} = scope.useMemo(deps -> ${3:compute()}, ${4:dep});'],
      },
      'solaire: Context & Provider': {
        scope: 'java',
        prefix: 'jctx',
        description: 'Define a Context token and wrap children with El.provider',
        body: [
          'static final Context<${1:String}> ${2:MyContext} = Context.of(${3:"default"});',
          '',
          'El.provider(${2:MyContext}, ${4:"value"}, () -> ${0:child});',
        ],
      },
      'solaire: useTransition & useDeferredValue': {
        scope: 'java',
        prefix: 'jtrans',
        description: 'Use concurrent transition and deferred value',
        body: [
          'JReact.Transition ${1:transition} = scope.useTransition();',
          '${2:String} ${3:deferred} = scope.useDeferredValue(${4:value}.get());',
          '${1:transition}.start(() -> ${5:filter}.set(${4:value}.get()));',
        ],
      },
      'solaire: Query.use': {
        scope: 'java',
        prefix: 'jquery',
        description: 'Fetch and cache async data with Query.use and Http.get',
        body: [
          'var ${1:items} = Query.use("${2:items}", (onData, onError) ->',
          '    Http.get("${3:/api/items}", res -> {',
          '        if (res.ok()) onData.accept(res.body());',
          '        else onError.accept(res.body());',
          '    }));',
          'if (${1:items}.isLoading()) return Ui.spinner();',
          'if (${1:items}.error() != null) return Ui.toast(${1:items}.error(), "error");',
        ],
      },
      'solaire: Router.install': {
        scope: 'java',
        prefix: 'jroute',
        description: 'Install hash Router with routes',
        body: [
          'Router.install(HTMLDocument.current().getElementById("${1:app}"), List.of(',
          '    Router.route("/", () -> ${2:HomePage.render()}),',
          '    Router.route("/${3:items}/:id", params -> ${4:DetailPage.render(params)})',
          '));',
        ],
      },
      'solaire: AppShell & Card': {
        scope: 'java',
        prefix: 'jshell',
        description: 'Render AppShell sidebar layout with Ui.card',
        body: [
          'AppShell.appShell(',
          '    scope,',
          '    "${1:Tea Party}",',
          '    List.of(AppShell.NavItem.item("/", "${2:Home}")),',
          '    () -> Ui.card("${3:Overview}", $0)',
          ');',
        ],
      },
      'solaire: DataTable': {
        scope: 'java',
        prefix: 'jtable',
        description: 'Render a typed DataTable with columns',
        body: [
          'DataTable.table(',
          '    List.of(',
          '        DataTable.Col.of("${1:Name}", row -> ${2:row.name()})',
          '    ),',
          '    ${3:rows},',
          '    "${4:No items yet}"',
          ');',
        ],
      },
    },
    null,
    2
  ) + '\n';
}
