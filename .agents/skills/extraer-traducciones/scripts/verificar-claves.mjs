#!/usr/bin/env node
/*
 * verificar-claves.mjs
 * Compara las traducciones de español e inglés y busca claves usadas con t("...")
 * que no existen. Sin dependencias: solo Node 18+.
 *
 * Uso (desde la raíz del repo):
 *   node .agents/skills/extraer-traducciones/scripts/verificar-claves.mjs
 *   node ... --dir src/components/Costos   (solo revisar el uso de claves en esa carpeta)
 */
import fs from "node:fs";
import path from "node:path";

const args = process.argv.slice(2);
const getArg = (n, d) => {
  const i = args.indexOf(n);
  return i >= 0 && args[i + 1] ? args[i + 1] : d;
};
const ROOT = process.cwd();
const I18N = path.join(ROOT, "src/i18n.js");
const SCAN_DIR = path.resolve(ROOT, getArg("--dir", "src"));

const i18nSrc = fs.readFileSync(I18N, "utf8");

// import esCostos from "./locales/es/costos.json";
const imports = new Map();
for (const m of i18nSrc.matchAll(/import\s+(\w+)\s+from\s+["'](\.\/locales\/(es|en)\/[^"']+\.json)["']/g)) {
  imports.set(m[1], { file: path.join(ROOT, "src", m[2].slice(2)), lang: m[3] });
}

// resources: { es: { translation: { costos: esCostos, ... } }, en: {...} }
function domainsFor(lang) {
  const block = i18nSrc.match(new RegExp(`\\b${lang}\\s*:\\s*\\{\\s*translation\\s*:\\s*\\{([\\s\\S]*?)\\}`));
  const map = new Map();
  if (!block) return map;
  for (const m of block[1].matchAll(/(\w+)\s*:\s*(\w+)/g)) {
    const imp = imports.get(m[2]);
    if (imp) map.set(m[1], imp.file);
  }
  return map;
}

const flatten = (obj, prefix = "", out = {}) => {
  for (const [k, v] of Object.entries(obj)) {
    const key = prefix ? `${prefix}.${k}` : k;
    if (v && typeof v === "object" && !Array.isArray(v)) flatten(v, key, out);
    else out[key] = v;
  }
  return out;
};

const load = (lang) => {
  const keys = {};
  for (const [domain, file] of domainsFor(lang)) {
    const data = JSON.parse(fs.readFileSync(file, "utf8"));
    Object.assign(keys, flatten(data, domain));
  }
  return keys;
};

const es = load("es");
const en = load("en");
const rel = (f) => path.relative(ROOT, f).split(path.sep).join("/");

const soloEs = Object.keys(es).filter((k) => !(k in en)).sort();
const soloEn = Object.keys(en).filter((k) => !(k in es)).sort();
const vacias = Object.entries({ ...es }).filter(([, v]) => v === "").map(([k]) => `es: ${k}`)
  .concat(Object.entries(en).filter(([, v]) => v === "").map(([k]) => `en: ${k}`));

// Archivos de locales que existen pero no están registrados en i18n.js
const registrados = new Set([...imports.values()].map((x) => x.file));
const sinRegistrar = [];
for (const lang of ["es", "en"]) {
  const dir = path.join(ROOT, "src/locales", lang);
  if (!fs.existsSync(dir)) continue;
  for (const f of fs.readdirSync(dir)) if (f.endsWith(".json") && !registrados.has(path.join(dir, f))) sinRegistrar.push(rel(path.join(dir, f)));
}

// Claves usadas en código: t("a.b.c") o t('a.b.c') o titleKey="a.b.c" / headerKey: "a.b.c"
function walk(dir, out = []) {
  for (const e of fs.readdirSync(dir, { withFileTypes: true })) {
    const p = path.join(dir, e.name);
    if (e.isDirectory()) { if (e.name !== "__tests__" && e.name !== "locales") walk(p, out); }
    else if (/\.(jsx?|mjs)$/.test(e.name) && !/\.(spec|test)\./.test(e.name)) out.push(p);
  }
  return out;
}
const usadas = new Map();
const KEY_RE = [/\bt\(\s*["'`]([a-zA-Z][\w-]*(?:\.[\w-]+)+)["'`]/g, /\b(?:titleKey|subtitleKey|headerKey|labelKey)\s*[=:]\s*["']([a-zA-Z][\w-]*(?:\.[\w-]+)+)["']/g];
for (const f of walk(SCAN_DIR)) {
  const code = fs.readFileSync(f, "utf8");
  for (const re of KEY_RE) for (const m of code.matchAll(re)) {
    if (!usadas.has(m[1])) usadas.set(m[1], new Set());
    usadas.get(m[1]).add(rel(f));
  }
}
// Una clave usada que apunta a un objeto (ej. un grupo de mensajes) no se reporta.
const esPrefijo = (k, dict) => Object.keys(dict).some((x) => x.startsWith(k + "."));
const faltanEs = [...usadas.keys()].filter((k) => !(k in es) && !esPrefijo(k, es)).sort();
const faltanEn = [...usadas.keys()].filter((k) => !(k in en) && !esPrefijo(k, en)).sort();

const out = (title, list, fmt = (x) => x) => {
  console.log(`${title} (${list.length})`);
  list.slice(0, 60).forEach((x) => console.log(`  - ${fmt(x)}`));
  if (list.length > 60) console.log(`  … y ${list.length - 60} más`);
  console.log("");
};
console.log(`Claves: es=${Object.keys(es).length} · en=${Object.keys(en).length} · usadas en ${rel(SCAN_DIR)}=${usadas.size}\n`);
out("Solo en español (falta en inglés)", soloEs);
out("Solo en inglés (falta en español)", soloEn);
out("Valores vacíos", vacias);
out("Archivos de locales sin registrar en src/i18n.js", sinRegistrar);
out("Usadas en el código y ausentes en español", faltanEs, (k) => `${k}  ← ${[...usadas.get(k)].join(", ")}`);
out("Usadas en el código y ausentes en inglés", faltanEn, (k) => `${k}  ← ${[...usadas.get(k)].join(", ")}`);
process.exitCode = soloEs.length || soloEn.length || faltanEs.length || faltanEn.length || sinRegistrar.length ? 1 : 0;
