---
name: revisar-cambios
description: Revisa los cambios de una rama o de un PR del frontend contra la guía técnica del frontend antes de abrir o aprobar el PR. Usar al pedir revisar cambios, revisar un PR, validar antes de hacer commit o preguntar si algo cumple las reglas del proyecto.
---

# Revisar cambios

Esta skill solo lee y reporta. No modifica archivos salvo que la persona lo pida después de ver el reporte.

Lee primero la guía técnica del frontend (`guia_reglas_frontend_para_desarrolladores.md`): es la fuente de las reglas. El checklist final de la guía (sección 16) es la base de esta revisión.

## 1. Chequeos automáticos

Desde la raíz del repo:

```bash
node .agents/skills/revisar-cambios/scripts/revisar-cambios.mjs
```

Por defecto compara el árbol de trabajo (incluidos cambios sin commit y archivos nuevos) contra `origin/develop`. Opciones:

- `--base origin/develop_costos` si la rama sale de costos.
- `--head origin/<rama>` para revisar una rama o PR sin cambiarse a ella (hacer `git fetch` antes).
- `--json` para salida estructurada.

El script reporta en tres niveles:

- `error`: hay que corregirlo antes del PR (archivos con solo cambios de saltos de línea, `docs/` modificado, `.env`, `axios` importado directo, URLs o IPs en el código, tildes dañadas, archivo nuevo sin encabezado).
- `aviso`: probablemente hay que corregirlo (sin fila nueva en el control de cambios, `style={{}}`, colores fijos, `console.log`, supresiones de ESLint, cambios de dependencias, traducción en un solo idioma, código sin pruebas).
- `revisar`: posibles textos sin `t()`. Son heurísticos; confirmar cada uno leyendo el archivo.

## 2. Revisión de criterio

El script no entiende el código. Después de correrlo, leer el diff (`git diff <base>...` o el del PR) y revisar:

1. Componentes: ¿usa `SectionHeader`, `GridActionBar`, `AppDataGrid`, `CrudFilterModal`, `SearchableSelect` donde aplican, o crea variantes nuevas sin necesidad? Comparar con los módulos de referencia de la guía: `almacen/Almacen.jsx`, `EmpresaRol/EmpresaRol.jsx`, `Usuario/Usuario.jsx`. Botones adicionales van por `extraActions` o `rightActions`, no en una barra paralela.
2. Textos: todo texto visible pasa por `t()` y la clave existe en `src/locales/es` y `src/locales/en`.
3. Experiencia de usuario: eliminar o cambiar estado pide confirmación; los errores que ve el usuario son entendibles (no `err.message` de Axios).
4. Backend: usa los campos que entrega el contrato real, lee bien la paginación (`data.page` en los contratos V2) y no repite peticiones (efectos que se disparan dos veces, catálogos que cargan con el modal cerrado, recargas innecesarias).
5. Formularios: sigue la sección 7.5 de la guía (estado `formData` / `errors` / `saving`, `validate()`, validaciones por tipo de campo reutilizando las de `Usuario`, `buildPayload()`, cerrar solo después de guardar bien, `reloadData()`).
6. Alcance: el PR hace una sola cosa. Si mezcla refactor, estilos y funcionalidad, sugerir dividirlo.
7. Reestructuración: no adelanta cambios del plan RNF-003 (rutas, carpetas `features/`) dentro de un PR de funcionalidad, ni toca `App.jsx` o `Navigator2.jsx` más allá de registrar un módulo.
8. Pruebas: si cambia comportamiento, hay prueba nueva o ajustada (`src/components/__tests__/` o `tests/e2e/`).

## 3. Formato del reporte

Responder en español, corto y concreto:

```
Resumen: <listo para PR | requiere cambios> · <n> errores, <n> avisos

Corregir antes del PR
- <archivo>:<línea> · <qué pasa> · <cómo arreglarlo>

Sugerencias
- ...

Revisado sin hallazgos
- <reglas que se revisaron y están bien>
```

No inventar números de línea: si no se sabe la línea exacta, dar el archivo y el fragmento. Si el script no pudo correr (no hay Node o falla `git merge-base`), decirlo y hacer solo la revisión de criterio.
