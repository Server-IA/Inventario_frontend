---
name: extraer-traducciones
description: Pasa los textos fijos de un módulo del frontend al sistema de traducciones (español e inglés) y verifica que las claves estén completas. Usar al pedir internacionalizar o traducir un módulo, quitar textos quemados, agregar claves i18n o revisar traducciones faltantes.
---

# Extraer traducciones

Lee primero la guía técnica del frontend (`guia_reglas_frontend_para_desarrolladores.md`), secciones 7.5 (i18n en formularios) y 8. Requerimiento relacionado: RNF-002.

## Cómo está armado i18n en este proyecto

- Un archivo JSON por dominio en `src/locales/es/` y `src/locales/en/` (por ejemplo `costos.json`).
- Cada archivo se importa en `src/i18n.js` y se registra bajo una clave de dominio dentro de `resources.es.translation` y `resources.en.translation`. Esa clave es el primer segmento de la ruta: `costos.json` registrado como `costos` se usa como `t("costos.tipoCostoIndirecto.title")`.
- Textos genéricos (Cancelar, Guardar, Activo, Inactivo...) ya existen en `common.json` (`common.actions.*`, `common.labels.*`). Reutilizarlos antes de crear claves nuevas.
- En los componentes se usa `const { t } = useTranslation();` y `t("dominio.ruta.clave")`, como indica la guía. Varios módulos pasan además un texto de respaldo (`t("clave", "Texto")`). Ojo: ese respaldo esconde las claves que no existen, porque en español se ve bien y en inglés aparece el texto en español. Si se usa, la clave tiene que existir igual en los dos idiomas.
- `SectionHeader` recibe la clave con `titleKey` y las columnas de `AppDataGrid` con `headerKey`.

Estructura de claves a seguir (tomada de `costos.json`):

```
<dominio>.<modulo>.title
<dominio>.<modulo>.columns.<campo>
<dominio>.<modulo>.grid.empty
<dominio>.<modulo>.filters.<campo>
<dominio>.<modulo>.form.<campo> / form.title / form.editTitle / form.validation.<regla>
<dominio>.<modulo>.messages.<evento>   (created, updated, deleted, listError...)
```

## Pasos

1. Leer el módulo completo (contenedor, formulario, grilla, filtros, validaciones) y listar cada texto visible: títulos, etiquetas, columnas, placeholders, helperText, opciones de select, mensajes de snackbar, mensajes de validación, textos de confirmación, `aria-label` y `title`.
2. Decidir el dominio: usar el archivo existente si el módulo ya tiene uno (en ese caso no se toca `src/i18n.js`); si no, crear `src/locales/es/<dominio>.json` y `src/locales/en/<dominio>.json` y registrarlos en `src/i18n.js` (import + entrada en `es.translation` y `en.translation`).
3. Agregar las claves en los dos idiomas con la misma estructura. El inglés debe ser una traducción real, no una copia del español.
4. Reemplazar cada texto por `t("clave")` (con respaldo si el archivo ya usa ese estilo). En columnas, usar `headerKey`.
5. Estados que hoy se comparan por texto visible ("Activo", "Inactivo"): no compararlos contra el texto traducido; comparar por id o valor del backend y traducir solo lo que se muestra.
6. Actualizar el encabezado de control de cambios de cada archivo tocado (guía, sección 10).

## Verificar

```bash
node .agents/skills/extraer-traducciones/scripts/verificar-claves.mjs
node .agents/skills/extraer-traducciones/scripts/verificar-claves.mjs --dir src/components/<Modulo>
```

El script compara las claves de español e inglés, detecta valores vacíos, archivos de locales sin registrar y claves usadas en el código que no existen en alguno de los idiomas. Al terminar no debe haber hallazgos del módulo trabajado.

Después: `npm test` (las pruebas suelen simular `t` y algunas buscan la clave o el texto de respaldo; si una prueba falla por un texto, ajustarla) y revisar la pantalla en los dos idiomas.

## Reglas

- No cambiar lógica, estilos ni comportamiento en el mismo cambio: solo textos.
- No traducir datos que vienen del backend (nombres de productos, empresas, etc.).
- Commit sugerido: `fix(<modulo>): internacionalizar textos de <modulo>`.
