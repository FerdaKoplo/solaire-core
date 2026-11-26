import { env, stdout } from 'node:process';

const useColor = Boolean(
  !env.NO_COLOR && (env.FORCE_COLOR || stdout.isTTY)
);

const wrap = (open, close) => (text) =>
  useColor ? `\x1b[${open}m${text}\x1b[${close}m` : String(text);

export const c = {
  bold: wrap('1', '22'),
  dim: wrap('2', '22'),
  amber: wrap('33', '39'),
  sage: wrap('32', '39'),
  cyan: wrap('36', '39'),
  red: wrap('31', '39'),
  bgAmber: wrap('43;30', '49;39'),
  inverse: wrap('7', '27'),
};

export function printBanner(version) {
  console.log('');
  console.log(
    `  ${c.bgAmber(' TEA PARTY ')} ${c.bold('setup-tea-party')} ${c.dim(`v${version}`)}`
  );
  console.log(
    `  ${c.dim('Scaffold a solaire-core (TeaVM + Vite) web app')}`
  );
  console.log('');
}
