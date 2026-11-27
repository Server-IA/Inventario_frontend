---
name: pruebas-desde-rf
description: Convierte los criterios de aceptación (Given/When/Then) de un RF o una HU del proyecto en pruebas del frontend con Vitest o Playwright, siguiendo los nombres y patrones del repo. Usar al pedir pruebas para un RF, una HU, una tarea o un issue, o al preguntar qué falta probar de un requerimiento.
---

# Pruebas desde un RF

Lee primero la guía técnica del frontend (`guia_reglas_frontend_para_desarrolladores.md`), secciones 7.5 (validaciones por tipo de campo y checklist de formularios) y 12.

## 1. Obtener los criterios

Los RF están en el repo `documentacion_inventario`, carpeta `documentacion/requerimientos/` (`RF-XXX - <Nombre>.docx`). Cada HU trae un bloque "Criterios de aceptación (Given / When / Then)".

Para leer el .docx, usar la herramienta disponible: `pandoc "<archivo>.docx" -t plain`, o `npx -y mammoth "<archivo>.docx" --output-format=markdown`. Si ninguna está disponible, pedir a la persona que pegue la sección de la HU.

Hacer una tabla con: HU, escenario, Given, When, Then.

## 2. Traducir cada criterio a comportamiento de interfaz

Muchos criterios están escritos para el backend ("Then 201 Created", "Then 404 Not Found"). En el frontend, cada uno se convierte en lo que ve el usuario:

| Criterio del RF | Qué se prueba en el frontend |
| --- | --- |
| 201 / 200 OK | Se cierra el formulario, aparece el mensaje de éxito y la grilla se recarga |
| 400 / validación | El campo muestra el error y no se envía la petición |
| 404 / 403 | Aparece un mensaje entendible, no el error técnico |
| Listado con filtros | Se envían los parámetros correctos y la grilla muestra lo que devuelve el backend |

Si un criterio no tiene efecto visible en el frontend, anotarlo como "solo backend" y no inventar una prueba.

## 3. Elegir el tipo de prueba

- Unitaria (Vitest + Testing Library): validaciones, armado de parámetros, mensajes, estados de botones, manejo de respuestas simuladas. Es la opción por defecto.
- E2E (Playwright): flujos completos contra el backend de desarrollo (crear, editar, listar con filtros). Solo para los caminos principales de la HU.

## 4. Escribir las pruebas siguiendo el repo

Leer antes el componente que se va a probar para usar sus textos, roles y nombres reales. No inventar selectores.

Unitarias, en `src/components/__tests__/`, nombre `Task<id><Descripcion>.spec.jsx` o `Issue<id><Descripcion>.spec.jsx`. Patrón usado en el repo (ver `Task262RegistrarTipoCostoIndirecto.spec.jsx`):

- `vi.mock("../axiosConfig", ...)` con `get`, `post`, `put`, `patch`, `delete` simulados.
- `vi.mock("react-i18next", ...)` con un `t` que devuelve el respaldo o la clave.
- Render dentro de `ThemeProvider` con `createTheme`.
- `describe("Task #<id> - HU-XXX.Y <título>")` y casos numerados en español.

E2E, en `tests/e2e/`, nombre `<dominio>.rf-XXX.spec.js` (casos positivos) y `<dominio>.rf-XXX.validation.spec.js` (validaciones). Usar los helpers de `tests/e2e/helpers/e2e.shared.utils.js` en vez de reescribirlos: `loginAsCompanyAdmin`, `openModuleScreen`, `clickActionButton`, `getActiveDialog`, `fillDialogField`, `clickDialogButton`, `expectSnackMessage`, `findGridCellInColumnAcrossPages`, entre otros. Ver `producto.rf-024.spec.js` como ejemplo. Los datos de prueba llevan un prefijo reconocible (`E2E <Entidad> ...`). Nunca escribir credenciales: salen de las variables `E2E_*`.

## 5. Entregar

- Las pruebas escritas.
- Una tabla de trazabilidad: criterio del RF → prueba que lo cubre (o "solo backend" / "pendiente" con el motivo).
- Correr `npm test` (y `npm run test:e2e -- <archivo>` si hay `.env` con las variables E2E). Reportar el resultado real; si no se pudo correr, decirlo.
- Si el criterio es de un formulario, cruzar con el checklist de formularios de la guía (abre en crear y editar, precarga, limpia errores, no carga catálogos con el modal cerrado, bloquea el envío mientras guarda, refresca la grilla).
