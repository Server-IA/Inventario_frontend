#!/usr/bin/env node
/*
 * revisar-cambios.mjs
 * Chequeos mecánicos sobre los cambios de una rama frente a su base, según la guía técnica del frontend.
 * Sin dependencias: solo Node 18+ y git.
 *
 * Uso (desde la raíz del repo):
 *   node .agents/skills/revisar-cambios/scripts/revisar-cambios.mjs
 *   node ... --base origin/develop_costos      (otra rama base)
 *   node ... --head origin/mi-rama             (comparar una rama remota sin cambiarse a ella)
 *   node ... --json
 */
import { execFileSync } from "node:child_process";
import fs from "node:fs";

const args = process.argv.slice(2);
const getArg = (n, d) => {
  const i = args.indexOf(n);
  return i >= 0 && args[i + 1] ? args[i + 1] : d;
};
const BASE = getArg("--base", "origin/develop");
const HEAD = getArg("--head", null); // null = árbol de trabajo actual (incluye cambios sin commit)
const AS_JSON = args.includes("--json");

const git = (...a) => execFileSync("git", a, { encoding: "utf8", maxBuffer: 64 * 1024 * 1024 });

let mergeBase;
try {
  mergeBase = git("merge-base", BASE, HEAD || "HEAD").trim();
} catch {
  console.error(`No se pudo calcular la base con ${BASE}. ¿Existe la rama? Prueba con git fetch.`);
  process.exit(2);
}
const range = HEAD ? [mergeBase, HEAD] : [mergeBase];

const parseNumstat = (txt) =>
  txt
    .trim()
    .split("\n")
    .filter(Boolean)
    .map((l) => {
      const [a, d, ...f] = l.split("\t");
      return { add: a, del: d, file: f.join("\t") };
    });

const stat = parseNumstat(git("diff", "--numstat", ...range));
// En modo árbol de trabajo, los archivos nuevos sin git add también cuentan.
const untracked = HEAD
  ? []
  : git("ls-files", "--others", "--exclude-standard").trim().split("\n").filter(Boolean);
const deleted = new Set(git("diff", "--name-only", "--diff-filter=D", ...range).trim().split("\n").filter(Boolean));
const statNoCr = new Map(parseNumstat(git("diff", "--numstat", "--ignore-cr-at-eol", ...range)).map((x) => [x.file, x]));

const hallazgos = [];
const add = (nivel, archivo, regla, detalle) => hallazgos.push({ nivel, archivo, regla, detalle });

const onlyEol = [];
const real = [...untracked];
for (const s of stat) {
  const n = statNoCr.get(s.file);
  if (!n || (n.add === "0" && n.del === "0")) onlyEol.push(s.file);
  else real.push(s.file);
}
if (onlyEol.length) add("error", `${onlyEol.length} archivo(s)`, "solo-saltos-de-linea", `Solo cambian finales de línea (CRLF/LF): ${onlyEol.slice(0, 8).join(", ")}${onlyEol.length > 8 ? "…" : ""}. Descartarlos antes del commit.`);

for (const f of real) {
  if (/^docs\//.test(f)) add("error", f, "docs-generado", "docs/ es JSDoc generado; no se versiona a mano.");
  if (/(^|\/)\.env(\.|$)/.test(f) && !/example/.test(f)) add("error", f, "secretos", "Archivo .env en el cambio. Nunca subir variables de entorno.");
  if (f === "package.json" || f === "package-lock.json") add("aviso", f, "dependencias", "Cambian dependencias: justificarlo en el PR.");
}

const codeFiles = real.filter((f) => /^src\/.*\.(jsx?|mjs)$/.test(f));
const isTest = (f) => /__tests__|\.(spec|test)\./.test(f);
const today = new Date().toISOString().slice(0, 10);

for (const f of codeFiles) {
  if (deleted.has(f)) continue; // un archivo borrado no necesita encabezado ni revisión de contenido
  const isUntracked = untracked.includes(f);
  const diff = isUntracked ? "" : git("diff", "--ignore-cr-at-eol", "-U0", ...range, "--", f);
  const added = isUntracked
    ? fs.readFileSync(f, "utf8").split("\n")
    : diff.split("\n").filter((l) => l.startsWith("+") && !l.startsWith("+++")).map((l) => l.slice(1));
  const isNew = isUntracked || /^new file mode/m.test(diff);

  if (!isTest(f)) {
    const hasHeaderRow = added.some((l) => /^\s*\|\s*\d{4}-\d{2}-\d{2}\s*\|/.test(l));
    const headerInBlock = isNew ? added.some((l) => /CONTROL DE CAMBIOS/.test(l)) : null;
    if (isNew && !headerInBlock) add("error", f, "encabezado", "Archivo nuevo sin encabezado de control de cambios.");
    else if (!hasHeaderRow) add("aviso", f, "encabezado", "No se agregó fila al control de cambios del encabezado.");
  }

  added.forEach((l) => {
    const t = l.trim();
    const esClienteHttp = /axiosConfig\.js$/.test(f);
    if (!isTest(f) && !esClienteHttp && /from\s+["']axios["']/.test(t)) add("error", f, "http", "Importa axios directo. Usar axiosConfig.");
    if (!isTest(f) && /SiteProps\.url|https?:\/\/(localhost|\d{1,3}\.\d{1,3}\.)/.test(t)) add("error", f, "http", `URL o IP escrita en el código: ${t.slice(0, 90)}`);
    if (/style=\{\{/.test(t)) add("aviso", f, "estilos", `style en línea; usar sx: ${t.slice(0, 90)}`);
    if (/#[0-9a-fA-F]{3,8}\b|\bgrey\[\d+\]|rgba?\(/.test(t) && !/^\s*(\*|\/\/)/.test(t)) add("aviso", f, "estilos", `Color fijo; usar tokens del tema: ${t.slice(0, 90)}`);
    if (/Ã[¡-¿]|Ã±|Ã‘|Â¿|Â¡/.test(t)) add("error", f, "codificacion", `Tildes dañadas (mojibake): ${t.slice(0, 90)}`);
    if (/console\.log\(/.test(t)) add("aviso", f, "residuos", `console.log agregado: ${t.slice(0, 90)}`);
    if (/eslint-disable/.test(t)) add("aviso", f, "lint", `Supresión de ESLint agregada: ${t.slice(0, 90)}`);
    if (/^\s*\|\s*\d{4}-\d{2}-\d{2}\s*\|.*\b(OpenAI|ChatGPT|Copilot|Claude|Gemini|IA)\b/i.test(t)) add("error", f, "encabezado", `Autor del control de cambios es una herramienta de IA: ${t.slice(0, 90)}`);
    if (/\.jsx$/.test(f) && !isTest(f)) {
      const txt = t.match(/>\s*([A-Za-zÁÉÍÓÚÑáéíóúñ¿¡][^<>{}]{2,})\s*</);
      const attr = t.match(/\b(label|placeholder|title|helperText|aria-label)=["']([^"']{2,})["']/);
      if (txt) add("revisar", f, "i18n", `Posible texto sin t(): "${txt[1].trim().slice(0, 60)}"`);
      if (attr) add("revisar", f, "i18n", `Posible atributo sin t(): ${attr[1]}="${attr[2].slice(0, 60)}"`);
    }
  });
}

const changedComponents = codeFiles.filter((f) => !isTest(f) && !deleted.has(f));
const changedTests = real.filter((f) => isTest(f) || /^tests\/e2e\//.test(f));
if (changedComponents.length && !changedTests.length) add("aviso", "(rama)", "pruebas", "Cambia código de src/ pero ninguna prueba. Confirmar si el cambio necesita prueba.");

const touchedLocales = real.filter((f) => /^src\/locales\/(es|en)\//.test(f));
const esL = touchedLocales.filter((f) => f.includes("/es/")).map((f) => f.split("/").pop());
const enL = touchedLocales.filter((f) => f.includes("/en/")).map((f) => f.split("/").pop());
for (const n of esL) if (!enL.includes(n)) add("aviso", `src/locales/en/${n}`, "i18n", "Cambió el archivo en español pero no el de inglés.");
for (const n of enL) if (!esL.includes(n)) add("aviso", `src/locales/es/${n}`, "i18n", "Cambió el archivo en inglés pero no el de español.");

const orden = { error: 0, aviso: 1, revisar: 2 };
hallazgos.sort((a, b) => orden[a.nivel] - orden[b.nivel] || a.archivo.localeCompare(b.archivo));

const resumen = {
  base: BASE,
  head: HEAD || "árbol de trabajo",
  archivosConCambiosReales: real.length,
  archivosSoloSaltosDeLinea: onlyEol.length,
  errores: hallazgos.filter((h) => h.nivel === "error").length,
  avisos: hallazgos.filter((h) => h.nivel === "aviso").length,
  aRevisar: hallazgos.filter((h) => h.nivel === "revisar").length,
};

if (AS_JSON) console.log(JSON.stringify({ resumen, hallazgos }, null, 2));
else {
  console.log(`Base: ${BASE} · Head: ${resumen.head} · fecha ${today}`);
  console.log(`Cambios reales: ${resumen.archivosConCambiosReales} archivo(s) · solo saltos de línea: ${resumen.archivosSoloSaltosDeLinea}`);
  console.log(`Errores: ${resumen.errores} · Avisos: ${resumen.avisos} · A revisar: ${resumen.aRevisar}\n`);
  for (const h of hallazgos) console.log(`[${h.nivel.toUpperCase()}] ${h.archivo} · ${h.regla}\n    ${h.detalle}`);
  if (!hallazgos.length) console.log("Sin hallazgos mecánicos. Falta la revisión de criterio (ver SKILL.md).");
}
process.exitCode = resumen.errores ? 1 : 0;
