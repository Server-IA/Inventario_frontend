#!/usr/bin/env node
/*
 * detectar-codigo-muerto.mjs
 * Lista los archivos de src/ a los que no llega ningún import desde los puntos
 * de entrada de la app. Sin dependencias: solo Node 18+.
 *
 * Uso (desde la raíz del repo):
 *   node .agents/skills/detectar-codigo-muerto/scripts/detectar-codigo-muerto.mjs
 *   node ... --entry src/main.jsx --json
 */
import fs from "node:fs";
import path from "node:path";

const args = process.argv.slice(2);
const getArg = (name, def) => {
  const i = args.indexOf(name);
  return i >= 0 && args[i + 1] ? args[i + 1] : def;
};
const ROOT = process.cwd();
const SRC = path.join(ROOT, "src");
const ENTRIES = getArg("--entry", "src/main.jsx").split(",").map((e) => path.resolve(ROOT, e.trim()));
const AS_JSON = args.includes("--json");

const CODE_EXT = [".js", ".jsx", ".mjs", ".ts", ".tsx"];
const ALL_EXT = [...CODE_EXT, ".json", ".css"];
const isTest = (f) =>
  /[\\/]__tests__[\\/]/.test(f) || /\.(spec|test)\.[jt]sx?$/.test(f) || /test-setup\.[jt]s$/.test(f);

function walk(dir, out = []) {
  for (const e of fs.readdirSync(dir, { withFileTypes: true })) {
    const p = path.join(dir, e.name);
    if (e.isDirectory()) walk(p, out);
    else if (ALL_EXT.includes(path.extname(e.name))) out.push(p);
  }
  return out;
}

function stripComments(code) {
  return code
    .replace(/\/\*[\s\S]*?\*\//g, "")
    .split("\n")
    .filter((l) => !l.trim().startsWith("//"))
    .join("\n");
}

const IMPORT_RE = [
  /\bimport\s+(?:[^'"`;]*?\s+from\s+)?["']([^"']+)["']/g,
  /\bexport\s+[^'"`;]*?\s+from\s+["']([^"']+)["']/g,
  /\bimport\s*\(\s*["']([^"']+)["']\s*\)/g,
  /\brequire\s*\(\s*["']([^"']+)["']\s*\)/g,
];

function specifiers(file) {
  const code = stripComments(fs.readFileSync(file, "utf8"));
  const found = new Set();
  for (const re of IMPORT_RE) for (const m of code.matchAll(re)) found.add(m[1]);
  return [...found];
}

function resolveSpec(fromFile, spec) {
  let base;
  if (spec.startsWith("@/")) base = path.join(SRC, spec.slice(2));
  else if (spec.startsWith(".")) base = path.resolve(path.dirname(fromFile), spec);
  else return null; // paquete npm o recurso de public/ (/images/...)
  const candidates = [base, ...ALL_EXT.map((x) => base + x), ...CODE_EXT.map((x) => path.join(base, "index" + x))];
  return candidates.find((c) => fs.existsSync(c) && fs.statSync(c).isFile()) || null;
}

const allFiles = walk(SRC);
const appFiles = allFiles.filter((f) => !isTest(f));
const testFiles = allFiles.filter(isTest);

function reach(starts) {
  const seen = new Set();
  const stack = [...starts];
  while (stack.length) {
    const f = stack.pop();
    if (seen.has(f) || !fs.existsSync(f)) continue;
    seen.add(f);
    if (!CODE_EXT.includes(path.extname(f))) continue;
    for (const s of specifiers(f)) {
      const r = resolveSpec(f, s);
      if (r && !seen.has(r)) stack.push(r);
    }
  }
  return seen;
}

for (const e of ENTRIES) {
  if (!fs.existsSync(e)) {
    console.error(`No existe el punto de entrada: ${path.relative(ROOT, e)}`);
    process.exit(2);
  }
}

const fromApp = reach(ENTRIES);
const fromTests = reach(testFiles);
const rel = (f) => path.relative(ROOT, f).split(path.sep).join("/");

const dead = appFiles.filter((f) => !fromApp.has(f));
const onlyTests = dead.filter((f) => fromTests.has(f)).map(rel).sort();
const orphan = dead.filter((f) => !fromTests.has(f)).map(rel).sort();
const brokenTests = [];
for (const t of testFiles) {
  for (const s of specifiers(t)) {
    if ((s.startsWith(".") || s.startsWith("@/")) && !resolveSpec(t, s)) brokenTests.push(`${rel(t)} -> ${s}`);
  }
}

const result = {
  entradas: ENTRIES.map(rel),
  archivosAnalizados: appFiles.length,
  alcanzables: [...fromApp].filter((f) => !isTest(f)).length,
  sinUso: orphan,
  soloUsadosPorPruebas: onlyTests,
  pruebasConImportsRotos: brokenTests.sort(),
};

if (AS_JSON) {
  console.log(JSON.stringify(result, null, 2));
} else {
  console.log(`Entradas: ${result.entradas.join(", ")}`);
  console.log(`Archivos de app analizados: ${result.archivosAnalizados} · alcanzables: ${result.alcanzables}\n`);
  const block = (title, list) => {
    console.log(`${title} (${list.length})`);
    list.forEach((x) => console.log(`  - ${x}`));
    console.log("");
  };
  block("Sin uso: nada los importa", orphan);
  block("Solo los importan pruebas", onlyTests);
  block("Pruebas que importan archivos inexistentes", result.pruebasConImportsRotos);
  console.log("Esto es una lista de candidatos. Revisar cada uno antes de borrar (ver SKILL.md).");
}
