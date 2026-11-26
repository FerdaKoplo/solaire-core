#!/usr/bin/env node

import { run } from '../src/cli.js';

try {
  const code = await run(process.argv.slice(2));
  process.exitCode = code;
} catch (err) {
  if (err?.name === 'AbortError') {
    console.error('\nAborted.');
    process.exitCode = 130;
  } else {
    console.error(`Error: ${err?.message ?? String(err)}`);
    process.exitCode = 1;
  }
}
