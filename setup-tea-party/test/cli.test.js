import assert from 'node:assert/strict';
import { execFile } from 'node:child_process';
import { mkdtemp, readFile, rm, stat, writeFile } from 'node:fs/promises';
import os from 'node:os';
import path from 'node:path';
import { afterEach, describe, it } from 'node:test';
import { fileURLToPath } from 'node:url';
import { promisify } from 'node:util';

import { parseArgs, toAppTitle, toDefaultPackage, validatePackageName } from '../src/args.js';
import { handleMenuKey } from '../src/prompts.js';
import { scaffoldProject } from '../src/scaffold.js';

const execFileAsync = promisify(execFile);
const __dirname = path.dirname(fileURLToPath(import.meta.url));
const BIN_PATH = path.resolve(__dirname, '../bin/setup-tea-party.js');

const tempDirs = [];

async function makeTempDir() {
  const dir = await mkdtemp(path.join(os.tmpdir(), 'setup-tea-party-test-'));
  tempDirs.push(dir);
  return dir;
}

afterEach(async () => {
  while (tempDirs.length > 0) {
    await rm(tempDirs.pop(), { recursive: true, force: true });
  }
});

describe('args parsing and naming helpers', () => {
  it('parses flags and positional target', () => {
    const opts = parseArgs(['my-app', '-p', 'io.example.app', '-t', 'tailwind', '-y']);
    assert.equal(opts.targetDir, 'my-app');
    assert.equal(opts.packageName, 'io.example.app');
    assert.equal(opts.theme, 'tailwind');
    assert.equal(opts.yes, true);
  });

  it('rejects invalid theme and invalid package', () => {
    assert.throws(() => parseArgs(['--theme', 'neon']), /Invalid theme/);
    assert.throws(() => parseArgs(['--package', 'com.Example.App']), /Invalid Java package/);
    assert.throws(() => validatePackageName('com.class.app'), /Invalid Java package/);
    assert.throws(() => parseArgs(['--bogus']), /Unknown option/);
  });

  it('derives valid default package and title', () => {
    assert.equal(toDefaultPackage('my-tea-party'), 'com.example.myteaparty');
    assert.equal(toDefaultPackage('123-app'), 'com.example.app123app');
    assert.equal(toDefaultPackage('class'), 'com.example.appclass');
    assert.equal(toAppTitle('my-tea_party'), 'My Tea Party');
  });
});

describe('scaffoldProject', () => {
  it('scaffolds tea theme project with Gradle wrapper and Tailwind config', async () => {
    const tmp = await makeTempDir();
    const targetDir = path.join(tmp, 'garden-ui');

    await scaffoldProject({
      targetDir,
      projectName: 'garden-ui',
      packageName: 'io.github.test.garden',
      theme: 'tea',
      solaireVersion: '1.4.0',
      force: false,
    });

    const buildGradle = await readFile(path.join(targetDir, 'build.gradle'), 'utf8');
    assert.match(buildGradle, /io\.github\.ferdakoplo:solaire-core:1\.4\.0/);
    assert.match(buildGradle, /mainClass = 'io\.github\.test\.garden\.Main'/);
    assert.match(buildGradle, /JSModuleType\.ES2015/);

    const mainJava = await readFile(
      path.join(targetDir, 'src/main/java/io/github/test/garden/Main.java'),
      'utf8'
    );
    assert.match(mainJava, /Classnames\.use\(Classnames\.tea\(\)\)/);

    const styleCss = await readFile(path.join(targetDir, 'src/main/web/style.css'), 'utf8');
    assert.match(styleCss, /@import "tailwindcss";/);
    assert.match(styleCss, /@source "\.\.\/\.\.\/\.\.\/build\/generated\/teavm\/js\/classes\.js";/);

    const gradlewStat = await stat(path.join(targetDir, 'gradlew'));
    assert.notEqual(gradlewStat.mode & 0o111, 0);
  });

  it('scaffolds css theme without Tailwind dependencies', async () => {
    const tmp = await makeTempDir();
    const targetDir = path.join(tmp, 'plain-app');

    await scaffoldProject({
      targetDir,
      projectName: 'plain-app',
      packageName: 'com.example.plain',
      theme: 'css',
      solaireVersion: '1.4.0',
      force: false,
    });

    const pkg = JSON.parse(await readFile(path.join(targetDir, 'package.json'), 'utf8'));
    assert.ok(pkg.dependencies.vite);
    assert.equal(pkg.dependencies['@tailwindcss/vite'], undefined);
    assert.equal(pkg.devDependencies, undefined);

    const styleCss = await readFile(path.join(targetDir, 'src/main/web/style.css'), 'utf8');
    assert.match(styleCss, /\.app-shell/);
  });

  it('refuses non-empty directory without --force', async () => {
    const tmp = await makeTempDir();
    await writeFile(path.join(tmp, 'existing.txt'), 'keep me', 'utf8');

    await assert.rejects(
      () =>
        scaffoldProject({
          targetDir: tmp,
          projectName: 'existing',
          packageName: 'com.example.existing',
          theme: 'tea',
          solaireVersion: '1.4.0',
          force: false,
        }),
      /not empty/
    );
  });
});

describe('CLI entrypoint', () => {
  it('runs end-to-end with --yes', async () => {
    const tmp = await makeTempDir();
    const target = path.join(tmp, 'tea-demo');

    const { stdout } = await execFileAsync(process.execPath, [BIN_PATH, target, '--yes']);
    assert.match(stdout, /Tea Party ready!/);
    assert.match(stdout, /npm run tea-party/);

    const settings = await readFile(path.join(target, 'settings.gradle'), 'utf8');
    assert.match(settings, /rootProject\.name = 'tea-demo'/);
    const buildGradle = await readFile(path.join(target, 'build.gradle'), 'utf8');
    assert.match(buildGradle, /io\.github\.ferdakoplo:solaire-core:1\.4\.0/);
  });
});

describe('handleMenuKey', () => {
  it('moves up/down with wrap-around and handles vim/number/submit/cancel keys', () => {
    assert.deepEqual(handleMenuKey(0, { name: 'up' }, 3), { index: 2, submit: false, cancel: false });
    assert.deepEqual(handleMenuKey(2, { name: 'down' }, 3), { index: 0, submit: false, cancel: false });
    assert.deepEqual(handleMenuKey(1, { name: 'k' }, 3), { index: 0, submit: false, cancel: false });
    assert.deepEqual(handleMenuKey(1, { name: 'j' }, 3), { index: 2, submit: false, cancel: false });
    assert.deepEqual(handleMenuKey(0, { sequence: '3' }, 3), { index: 2, submit: false, cancel: false });
    assert.deepEqual(handleMenuKey(1, { name: 'return' }, 3), { index: 1, submit: true, cancel: false });
    assert.deepEqual(handleMenuKey(1, { ctrl: true, name: 'c' }, 3), { index: 1, submit: false, cancel: true });
    assert.deepEqual(handleMenuKey(1, { name: 'escape' }, 3), { index: 1, submit: false, cancel: true });
  });
});
