import { readFile } from 'node:fs/promises';
import path from 'node:path';
import { stdout } from 'node:process';
import { fileURLToPath } from 'node:url';
import { DEFAULT_SOLAIRE_VERSION, THEMES, parseArgs } from './args.js';
import { resolveConfig } from './prompts.js';
import { scaffoldProject } from './scaffold.js';
import { c, printBanner } from './ui.js';

const __dirname = path.dirname(fileURLToPath(import.meta.url));

/** Print CLI usage help. */
export function formatHelp() {
  return `Usage: setup-tea-party [project-name] [options]

Scaffold a solaire-core (jreact) TeaVM + Vite web application.

Options:
  -p, --package <pkg>        Java base package (e.g. com.example.teaparty)
  -t, --theme <theme>        UI theme preset: ${THEMES.join(' | ')} (default: tea)
      --solaire-version <v>  solaire-core Maven version (default: ${DEFAULT_SOLAIRE_VERSION})
  -y, --yes                  Skip prompts and use defaults
  -f, --force                Allow writing into a non-empty directory
  -h, --help                 Show this help message
  -v, --version              Show CLI version
`;
}

async function readCliVersion() {
  const pkgRaw = await readFile(path.resolve(__dirname, '../package.json'), 'utf8');
  return JSON.parse(pkgRaw).version;
}

/** Run the setup-tea-party CLI. Returns an integer exit code. */
export async function run(argv) {
  const opts = parseArgs(argv);

  if (opts.help) {
    console.log(formatHelp());
    return 0;
  }

  const version = await readCliVersion();

  if (opts.version) {
    console.log(version);
    return 0;
  }

  if (stdout.isTTY && !opts.yes) {
    printBanner(version);
  }

  const config = await resolveConfig(opts, {
    basenameOfCwd: path.basename(process.cwd()),
  });

  const result = await scaffoldProject(config);
  const cdHint = config.targetDir === '.' ? '' : `    ${c.cyan(`cd ${config.targetDir}`)}\n`;

  console.log('');
  console.log(`  ${c.sage('✔')} ${c.bold('Tea Party ready!')} ${c.dim(result.targetDir)}`);
  console.log(`    ${c.dim('package')}  ${config.packageName}`);
  console.log(`    ${c.dim('theme')}    ${config.theme}`);
  console.log(`    ${c.dim('core')}     io.github.ferdakoplo:solaire-core:${config.solaireVersion}`);
  console.log('');
  console.log(`  ${c.bold('Next steps:')}`);
  process.stdout.write(cdHint);
  console.log(`    ${c.cyan('npm install')}`);
  console.log(`    ${c.cyan('npm run tea-party')}`);
  console.log('');
  return 0;
}
