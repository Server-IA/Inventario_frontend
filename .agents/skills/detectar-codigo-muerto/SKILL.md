---
name: detectar-codigo-muerto
description: Encuentra archivos del frontend que ningún import alcanza desde la app (código muerto) y prepara su retiro seguro. Usar al pedir limpiar código, buscar archivos sin uso o huérfanos, avanzar la Fase 02 de la reestructuración, o antes de mover carpetas.
---

# Detectar código muerto

Lee primero la guía técnica del frontend (`guia_reglas_frontend_para_desarrolladores.md`), en especial la sección 14 (no reintroducir módulos legacy eliminados).

## 1. Correr el análisis

Desde la raíz del repo:

```bash
node .agents/skills/detectar-codigo-muerto/scripts/detectar-codigo-muerto.mjs
```

Opciones: `--json` para salida en JSON y `--entry src/main.jsx,src/otro.jsx` si hay más de un punto de entrada.

El script recorre los imports estáticos y dinámicos desde `src/main.jsx` (incluido el alias `@/` si existe) y devuelve tres listas:

- Sin uso: ningún archivo de la app ni de pruebas los importa.
- Solo los importan pruebas: el archivo vive solo porque una prueba lo usa. Si se borra, se borra también su prueba.
- Pruebas con imports rotos: pruebas que apuntan a archivos que ya no existen.

## 2. Revisar cada candidato antes de borrar

El script trabaja con imports y no ve todo. Por cada archivo de la lista:

1. Buscar su nombre en todo el repo (`git grep -n "<NombreArchivo>"`), incluidos `index.html`, `vite.config.js` y `tests/e2e/`. Un archivo cargado por nombre en texto no aparece como import.
2. Revisar si está en `documentacion/proyecto/reestructuracion inventario.md` (repo `documentacion_inventario`). Si el plan lo marca como decisión pendiente (por ejemplo `criterio_evaluacion/`), no se borra: se reporta.
3. Revisar el historial (`git log --oneline -- <archivo>`). Si se tocó en las últimas semanas, preguntar a quien lo hizo; puede ser trabajo a medio integrar.

## 3. Lo que el script NO detecta

- Módulos importados en el registro de `components` de `Navigator2.jsx` cuyo id ya no envía el menú del backend. Para el script están vivos porque se importan. Para confirmarlos hay que comparar las claves de `components` con los ids del menú (`/v1/menu` o `/v2/menu`) o preguntar al equipo de backend.
- Exportaciones sin uso dentro de un archivo que sí se usa.
- Claves de traducción sin uso en `src/locales`.
- Imágenes de `public/` sin referencia.

## 4. Proponer el retiro

- Entregar primero la lista agrupada (carpeta o dominio) con el motivo de cada archivo y lo que quedó en duda. No borrar sin confirmación de la persona.
- Al borrar: rama `fix/<referenciaTaiga>-<hu>-<descripcion>` (o la que indique el equipo) creada desde `origin/develop` actualizado, `git rm` de los archivos (con su prueba si estaba en "solo los importan pruebas"), y quitar claves huérfanas que dejen en `Navigator2.jsx` o `App.jsx`.
- Verificar: `npm run build`, `npm test` y volver a correr el script. La lista debe quedar vacía o solo con lo que se decidió conservar.
- Un PR por grupo pequeño de archivos, sin mezclar con cambios de funcionalidad. Commit sugerido: `refactor(<dominio>): retirar <grupo> sin uso`.
- No hacer `push` ni abrir el PR sin que la persona lo pida (guía, sección 11.4).
