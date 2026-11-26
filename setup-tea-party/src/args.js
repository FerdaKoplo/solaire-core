/** Valid UI styling themes supported by solaire-core Classnames. */
export const THEMES = ['tea', 'tailwind', 'css'];

export const DEFAULT_PROJECT_NAME = 'my-tea-party';
export const DEFAULT_THEME = 'tea';
export const DEFAULT_SOLAIRE_VERSION = '1.4.0';

const JAVA_KEYWORDS = new Set([
  'abstract', 'assert', 'boolean', 'break', 'byte', 'case', 'catch', 'char',
  'class', 'const', 'continue', 'default', 'do', 'double', 'else', 'enum',
  'extends', 'final', 'finally', 'float', 'for', 'goto', 'if', 'implements',
  'import', 'instanceof', 'int', 'interface', 'long', 'native', 'new',
  'package', 'private', 'protected', 'public', 'return', 'short', 'static',
  'strictfp', 'super', 'switch', 'synchronized', 'this', 'throw', 'throws',
  'transient', 'try', 'void', 'volatile', 'while', '_',
]);

const PACKAGE_SEGMENT_RE = /^[a-z_][a-z0-9_]*$/;

/**
 * Parse CLI arguments into an options object.
 * Throws an Error on invalid flags or missing option values.
 */
export function parseArgs(argv) {
  const opts = {
    targetDir: null,
    packageName: null,
    theme: null,
    solaireVersion: DEFAULT_SOLAIRE_VERSION,
    yes: false,
    force: false,
    help: false,
    version: false,
  };

  for (let i = 0; i < argv.length; i++) {
    const arg = argv[i];
    if (arg === '-h' || arg === '--help') {
      opts.help = true;
    } else if (arg === '-v' || arg === '--version') {
      opts.version = true;
    } else if (arg === '-y' || arg === '--yes') {
      opts.yes = true;
    } else if (arg === '-f' || arg === '--force') {
      opts.force = true;
    } else if (arg === '-p' || arg === '--package') {
      opts.packageName = requireValue(argv, ++i, arg);
    } else if (arg.startsWith('--package=')) {
      opts.packageName = arg.slice('--package='.length);
    } else if (arg === '-t' || arg === '--theme') {
      opts.theme = requireValue(argv, ++i, arg);
    } else if (arg.startsWith('--theme=')) {
      opts.theme = arg.slice('--theme='.length);
    } else if (arg === '--solaire-version') {
      opts.solaireVersion = requireValue(argv, ++i, arg);
    } else if (arg.startsWith('--solaire-version=')) {
      opts.solaireVersion = arg.slice('--solaire-version='.length);
    } else if (arg.startsWith('-')) {
      throw new Error(`Unknown option: ${arg}`);
    } else if (opts.targetDir === null) {
      opts.targetDir = arg;
    } else {
      throw new Error(`Unexpected extra argument: ${arg}`);
    }
  }

  if (opts.theme !== null && !THEMES.includes(opts.theme)) {
    throw new Error(`Invalid theme "${opts.theme}". Expected one of: ${THEMES.join(', ')}`);
  }
  if (opts.packageName !== null) {
    validatePackageName(opts.packageName);
  }
  return opts;
}

function requireValue(argv, index, flag) {
  if (index >= argv.length || argv[index].startsWith('-')) {
    throw new Error(`Missing value for ${flag}`);
  }
  return argv[index];
}

/** Validate a Java package name (e.g. "com.example.myapp"). */
export function validatePackageName(pkg) {
  if (!pkg || typeof pkg !== 'string') {
    throw new Error('Java package name cannot be empty.');
  }
  const segments = pkg.split('.');
  for (const seg of segments) {
    if (!PACKAGE_SEGMENT_RE.test(seg) || JAVA_KEYWORDS.has(seg)) {
      throw new Error(
        `Invalid Java package segment "${seg}" in "${pkg}". Use lowercase identifiers (e.g. com.example.teaparty).`
      );
    }
  }
  return pkg;
}

/** Derive a valid Java base package name from a project directory name. */
export function toDefaultPackage(projectName) {
  const cleaned = String(projectName || '')
    .toLowerCase()
    .replace(/[^a-z0-9]/g, '');
  let leaf = cleaned || 'teaparty';
  if (/^[0-9]/.test(leaf) || JAVA_KEYWORDS.has(leaf)) {
    leaf = `app${leaf}`;
  }
  return `com.example.${leaf}`;
}

/** Derive a human-readable app title from a project directory name. */
export function toAppTitle(projectName) {
  const words = String(projectName || 'Tea Party')
    .trim()
    .split(/[-_\s]+/)
    .filter(Boolean);
  if (words.length === 0) return 'Tea Party';
  return words.map((w) => w.charAt(0).toUpperCase() + w.slice(1)).join(' ');
}
