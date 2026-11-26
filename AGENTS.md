# AGENTS.md · Inventario USCO (frontend)

Instrucciones para asistentes de IA que trabajen en este repositorio.

## Antes de cualquier cambio

Leer `guia_reglas_frontend_para_desarrolladores.md` en la raíz del repo. Es la fuente principal de reglas: stack, comandos, patrón CRUD, componentes compartidos, formularios y validaciones, i18n, capa HTTP, encabezados, Git y checklist final. Si este archivo y la guía no coinciden, manda la guía.

## Complementos a la guía

- Navegación actual: no hay rutas. `App.jsx` guarda el módulo activo en un estado y `components/dashboard/Navigator2.jsx` lo cambia según el menú que llega del backend. Un módulo nuevo se registra en el objeto `components` de `Navigator2.jsx` con la clave que manda el menú. No adelantar la migración a rutas (RNF-003) dentro de un cambio de funcionalidad.
- Pruebas: unitarias en `src/components/__tests__/` (`Task<id><Descripcion>.spec.jsx`); E2E en `tests/e2e/` (`<dominio>.rf-XXX.spec.js` y `.validation.spec.js`) usando los helpers de `tests/e2e/helpers/e2e.shared.utils.js`. Credenciales solo por variables `E2E_*`.
- `docs/` es documentación JSDoc generada: no se edita ni se versiona a mano.
- Codificación: guardar en UTF-8. Hay archivos con tildes dañadas (`MÃ³dulo`); no copiar esos textos.
- Guías externas de plataforma web (como Modern Web Guidance) se usan para accesibilidad, rendimiento, seguridad y CSS; los componentes de interfaz siguen siendo MUI y `common/`. Objetivo de compatibilidad de navegadores: Baseline Widely available **(por confirmar)**.

## Skills

En `.agents/skills/` hay instrucciones para tareas repetitivas. Usarlas cuando la tarea coincida:

- `revisar-cambios`: antes de abrir o aprobar un PR.
- `detectar-codigo-muerto`: al limpiar código o antes de mover carpetas.
- `extraer-traducciones`: al internacionalizar un módulo o revisar claves.
- `pruebas-desde-rf`: al escribir pruebas a partir de un RF o una HU.
- `describir-pr`: al redactar la descripción de un PR.
