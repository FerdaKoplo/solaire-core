import { chmod, cp, mkdir, readdir, writeFile } from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { renderTemplates } from './templates.js';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const WRAPPER_DIR = path.resolve(__dirname, '../templates/wrapper');

/**
 * Scaffold a new Tea Party project into targetDir.
 * Throws if targetDir exists and is non-empty unless force is true.
 */
export async function scaffoldProject(config) {
  const absDir = path.resolve(config.targetDir);
  await ensureTargetDir(absDir, config.force);

  const files = renderTemplates(config);
  for (const [relPath, content] of Object.entries(files)) {
    const dest = path.join(absDir, relPath);
    await mkdir(path.dirname(dest), { recursive: true });
    await writeFile(dest, content, 'utf8');
  }

  await cp(WRAPPER_DIR, absDir, { recursive: true });
  await chmod(path.join(absDir, 'gradlew'), 0o755);

  return {
    targetDir: absDir,
    files: Object.keys(files),
  };
}

async function ensureTargetDir(absDir, force) {
  await mkdir(absDir, { recursive: true });
  const entries = await readdir(absDir);
  const meaningful = entries.filter((name) => name !== '.DS_Store' && name !== '.git');
  if (meaningful.length > 0 && !force) {
    throw new Error(
      `Target directory "${absDir}" is not empty. Choose another directory or pass --force.`
    );
  }
}
