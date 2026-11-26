import readline from 'node:readline';
import { createInterface } from 'node:readline/promises';
import { stdin, stdout } from 'node:process';
import {
  DEFAULT_PROJECT_NAME,
  DEFAULT_THEME,
  THEMES,
  toDefaultPackage,
  validatePackageName,
} from './args.js';
import { c } from './ui.js';

const THEME_OPTIONS = [
  {
    value: 'tea',
    title: 'Tea Party',
    hint: 'Warm amber, cream & sage palette (Tailwind v4)',
  },
  {
    value: 'tailwind',
    title: 'Tailwind',
    hint: 'Clean slate & blue utility classes (Tailwind v4)',
  },
  {
    value: 'css',
    title: 'Plain CSS',
    hint: 'Semantic CSS stylesheet without Tailwind',
  },
];

/**
 * Pure navigation helper for arrow-key / j-k / numeric selection menus.
 * Returns { index, submit, cancel }.
 */
export function handleMenuKey(currentIndex, key, total) {
  if (!key || total <= 0) return { index: currentIndex, submit: false, cancel: false };
  if (key.ctrl && key.name === 'c') return { index: currentIndex, submit: false, cancel: true };
  if (key.name === 'escape') return { index: currentIndex, submit: false, cancel: true };
  if (key.name === 'return' || key.name === 'enter') {
    return { index: currentIndex, submit: true, cancel: false };
  }
  if (key.name === 'up' || key.name === 'k') {
    return { index: (currentIndex - 1 + total) % total, submit: false, cancel: false };
  }
  if (key.name === 'down' || key.name === 'j') {
    return { index: (currentIndex + 1) % total, submit: false, cancel: false };
  }
  const num = Number.parseInt(key.sequence ?? '', 10);
  if (num >= 1 && num <= total) {
    return { index: num - 1, submit: false, cancel: false };
  }
  return { index: currentIndex, submit: false, cancel: false };
}

/**
 * Resolve configuration interactively when running in a TTY without --yes,
 * or non-interactively using flags + defaults.
 */
export async function resolveConfig(opts, { basenameOfCwd } = {}) {
  const interactive = Boolean(stdin.isTTY && stdout.isTTY && !opts.yes);

  if (!interactive) {
    const targetDir = opts.targetDir ?? DEFAULT_PROJECT_NAME;
    const projectName = targetDir === '.' ? (basenameOfCwd || DEFAULT_PROJECT_NAME) : leafName(targetDir);
    const packageName = opts.packageName ?? toDefaultPackage(projectName);
    const theme = opts.theme ?? DEFAULT_THEME;
    return {
      targetDir,
      projectName,
      packageName,
      theme,
      solaireVersion: opts.solaireVersion,
      force: opts.force,
    };
  }

  const targetDir =
    opts.targetDir ?? (await promptText('Project directory', DEFAULT_PROJECT_NAME));
  const projectName =
    targetDir === '.' ? (basenameOfCwd || DEFAULT_PROJECT_NAME) : leafName(targetDir);
  const defaultPkg = toDefaultPackage(projectName);

  const packageName =
    opts.packageName ??
    (await promptText('Java base package', defaultPkg, validatePackageName));

  const theme = opts.theme ?? (await promptSelect('Choose a UI theme', THEME_OPTIONS, DEFAULT_THEME));

  return {
    targetDir,
    projectName,
    packageName,
    theme,
    solaireVersion: opts.solaireVersion,
    force: opts.force,
  };
}

async function promptText(label, fallback, validate) {
  while (true) {
    const rl = createInterface({ input: stdin, output: stdout });
    let raw;
    try {
      raw = await rl.question(
        `  ${c.amber('◇')} ${c.bold(label)} ${c.dim(`(${fallback})`)}: `
      );
    } finally {
      rl.close();
    }

    const value = raw.trim() || fallback;
    try {
      if (validate) validate(value);
      // Replace prompt line with clean confirmed summary line
      stdout.write(`\x1b[1A\x1b[2K  ${c.sage('◆')} ${c.bold(label)} ${c.dim('·')} ${c.cyan(value)}\n`);
      return value;
    } catch (err) {
      console.log(`    ${c.red('!')} ${err.message}`);
    }
  }
}

function promptSelect(label, options, defaultValue) {
  let index = Math.max(0, options.findIndex((o) => o.value === defaultValue));
  const totalLines = options.length + 2; // header + options + hint

  return new Promise((resolve, reject) => {
    let rendered = false;

    const render = () => {
      if (rendered) {
        stdout.write(`\x1b[${totalLines}A\x1b[0J`);
      }
      rendered = true;
      stdout.write(`  ${c.amber('◇')} ${c.bold(label)}\n`);
      options.forEach((opt, i) => {
        const active = i === index;
        const pointer = active ? c.amber('❯') : ' ';
        const radio = active ? c.amber('●') : c.dim('○');
        const title = active ? c.bold(c.amber(opt.title)) : opt.title;
        const hint = c.dim(` — ${opt.hint}`);
        stdout.write(`    ${pointer} ${radio} ${title}${hint}\n`);
      });
      stdout.write(`    ${c.dim('Use ↑/↓ arrows (or 1-3) and Enter to confirm')}\n`);
    };

    const cleanup = () => {
      stdin.removeListener('keypress', onKeypress);
      if (stdin.isTTY) stdin.setRawMode(false);
      stdin.pause();
      stdout.write('\x1b[?25h');
    };

    const onKeypress = (_str, key) => {
      const action = handleMenuKey(index, key, options.length);
      if (action.cancel) {
        stdout.write(`\x1b[${totalLines}A\x1b[0J`);
        cleanup();
        const err = new Error('Cancelled');
        err.name = 'AbortError';
        reject(err);
        return;
      }
      if (action.submit) {
        const chosen = options[index];
        stdout.write(`\x1b[${totalLines}A\x1b[0J`);
        stdout.write(
          `  ${c.sage('◆')} ${c.bold(label)} ${c.dim('·')} ${c.cyan(chosen.title)} ${c.dim(`(${chosen.value})`)}\n`
        );
        cleanup();
        resolve(chosen.value);
        return;
      }
      if (action.index !== index) {
        index = action.index;
        render();
      }
    };

    readline.emitKeypressEvents(stdin);
    if (stdin.isTTY) stdin.setRawMode(true);
    stdin.resume();
    stdout.write('\x1b[?25l');
    render();
    stdin.on('keypress', onKeypress);
  });
}

function leafName(targetDir) {
  const parts = targetDir.replace(/[/\\]+$/, '').split(/[/\\]/).filter(Boolean);
  return parts[parts.length - 1] || DEFAULT_PROJECT_NAME;
}

export { THEME_OPTIONS, THEMES };
