---
name: describir-pr
description: Redacta la descripción de un pull request del frontend y la fila para el control de cambios del proyecto a partir de los commits y el diff de la rama. Usar al pedir la descripción de un PR, el resumen de cambios de una rama, notas para el revisor o el registro en Control_de_Cambios.xlsx.
---

# Describir un PR

Lee primero la guía técnica del frontend (`guia_reglas_frontend_para_desarrolladores.md`), sección 11 (ramas, commits, push y pull request).

## 1. Reunir la información

Desde la raíz del repo, con la base que corresponda (`origin/develop` o `origin/develop_costos`):

```bash
git log --oneline origin/develop..HEAD
git diff --stat origin/develop...HEAD
git diff origin/develop...HEAD
```

Sacar las referencias del nombre de la rama y de los commits. Según la guía, la rama sigue el formato `tipo/referenciaTaiga-historiaDeUsuario-descripcion` (por ejemplo `fix/321-hu-037-usuarios-filtro-estado`: referencia Taiga 321, HU-037). Algunos commits traen además `(Task #262 / HU-044.1)` o `(Issue #324)`.

Si existe la skill `revisar-cambios`, correrla y usar su resultado en la sección "Cómo se probó" o "Pendientes".

## 2. Descripción del PR

Escribir en español, sin relleno, con esta estructura:

```markdown
## Qué cambia
<1 a 3 frases: qué puede hacer ahora el usuario o qué se corrigió>

## Referencias
- Taiga #NNN · HU-XXX · RF-XXX

## Cambios
- <archivo o módulo>: <qué cambió y por qué>

## Cómo se probó
- [ ] npm run build
- [ ] npm test (<n> pruebas, archivos nuevos: ...)
- [ ] E2E: <archivo> (o "no aplica" con el motivo)
- [ ] Revisión manual: <pasos concretos: abrir módulo, crear, editar, filtrar...>

## Capturas
<antes / después si hay cambios visuales; si faltan, dejar "Pendiente">

## Notas para el revisor
<decisiones tomadas, dudas, lo que queda fuera del PR>
```

Reglas: no inventar pruebas ni resultados que no se corrieron (dejar la casilla sin marcar); no listar archivos que solo cambian saltos de línea; si el PR mezcla temas, decirlo en las notas.

## 3. Título, reviewers y publicación

- Título del PR en el mismo formato que los commits: `fix(sede): ajustar cascada geográfica en filtros`.
- Reviewers: QA y el líder del proyecto (guía, sección 11.5).
- La skill solo redacta. No hacer `push` ni crear el PR sin que la persona lo pida explícitamente (guía, sección 11.4).

## 4. Fila para Control_de_Cambios.xlsx

El archivo está en el repo `documentacion_inventario`, carpeta `entrega/`. La primera hoja usa estas columnas:

| Versión | Fecha | Descripción del cambio | Autor | Módulo/Servicio impactado | Tipo de cambio | Descripción | Comentarios adicionales |
| --- | --- | --- | --- | --- | --- | --- | --- |

Proponer la fila con: la versión `0.4.0` (guía, sección 10), la fecha en formato AAAA-MM-DD, un título corto del cambio, el autor según la regla de la guía (nunca una herramienta de IA), el módulo, el tipo (`feature`, `fix`, `refactor`...) y una descripción de dos o tres líneas. Entregarla como tabla para copiar; no editar el .xlsx salvo que la persona lo pida.
