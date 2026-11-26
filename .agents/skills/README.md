# Skills del frontend

Instrucciones para tareas repetitivas que cualquier herramienta de IA compatible con [Agent Skills](https://agentskills.io) puede usar (Copilot, Claude, Gemini, Codex u otras). Cada carpeta tiene un `SKILL.md` y, si hace falta, scripts en Node sin dependencias.

Las reglas generales están en la guía técnica del frontend (`guia_reglas_frontend_para_desarrolladores.md`); las skills las aplican a una tarea concreta.

| Skill | Para qué | Script |
| --- | --- | --- |
| `revisar-cambios` | Revisar una rama o PR contra las convenciones antes de abrirlo o aprobarlo | `revisar-cambios.mjs` |
| `detectar-codigo-muerto` | Encontrar archivos sin uso y preparar su retiro | `detectar-codigo-muerto.mjs` |
| `extraer-traducciones` | Pasar textos fijos de un módulo a i18n y verificar claves | `verificar-claves.mjs` |
| `pruebas-desde-rf` | Convertir criterios Given/When/Then de un RF en pruebas | |
| `describir-pr` | Redactar la descripción del PR y la fila del control de cambios | |

## Usarlas

- Con un agente: pedir la tarea normalmente ("revisa mis cambios antes del PR") o nombrar la skill (`/revisar-cambios`). El agente decide cuándo cargarla según su `description`.
- Sin IA: los scripts funcionan solos desde la raíz del repo, por ejemplo `node .agents/skills/revisar-cambios/scripts/revisar-cambios.mjs`.

Copilot lee esta carpeta (`.agents/skills`). Si alguna herramienta del equipo busca las skills en otra ruta, se configura o se documenta aquí.

## Mantenerlas

- Entran y cambian por PR, como el código.
- Si un PR cambia una convención, la estructura o un componente de `common/`, ese PR actualiza la guía y las skills afectadas.
- Antes de agregar una skill externa: leer su `SKILL.md` y sus scripts, preferir fuentes oficiales y revisar la licencia.
