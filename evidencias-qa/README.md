# Evidencias Funcionales QA — Sprint 4 y Sprint 5

Capturas de pantalla y logs de red generados con **Playwright + Chrome DevTools Protocol** ejecutando la aplicación
contra el **backend integrado** `https://dev.api.inmero.co/inventario` con sesión de Administrador del Sistema
(`u20211196841@usco.edu.co`).

El panel derecho de cada captura es el tráfico de red **real** de la sesión (método, URL completa, query, body
literal enviado y status HTTP), con validación automática del body: `OK - DTO tipado (sin condicion / EMPRESA_ID / SQL)`.

---

## T-214 / HU-039.2 — Previsualización del reporte Kardex (PR #130)

| Archivo | Evidencia |
|---|---|
| `t214_01_network_carga_inicial_preload_unico.png` | Carga inicial: solo `GET /api/v2/menu` y **una sola** `GET /api/v2/report/kardex/filtros`. Sin requests ajenos (ni `/v1/pedido` ni `/v1/items/estado_categoria/0`). |
| `t214_02_network_busqueda_dto_tipado.png` | `POST /api/v2/report/kardex/resumen?page=0&size=10` con body `{"fechaInicio":"2026-01-01","fechaFin":"2026-10-19"}` — DTO tipado, sin SQL / `condicion` / `EMPRESA_ID`. |
| `t214_03_grilla_columnas_completas.png` | Tabla de previsualización con las columnas Fecha/Hora, Sede, Almacén, Producción, Tipo de Movimiento, Producto, Presentación, Cantidad y Saldo. |
| `t214_04_network_cambio_tamano_pagina.png` | Cambio de filas por página → `POST /api/v2/report/kardex/resumen?page=0&size=5`. |
| `t214_05_network_paginacion_pagina2.png` | Página 2 → `POST /api/v2/report/kardex/resumen?page=1&size=5` (200 OK). Paginación server-side demostrada. |
| `t214_network_1_carga_inicial.txt` | Log textual: método, URL, query, body y validación automática de cada request. |
| `t214_network_2_busqueda.txt` | Log textual de la búsqueda. |
| `t214_network_3_cambio_tamano.txt` | Log textual del cambio de tamaño de página. |
| `t214_network_4_paginacion.txt` | Log textual de la navegación a página 2. |

## T-216 / HU-039.3 — Generar y descargar el reporte Kardex (PR #131)

| Archivo | Evidencia |
|---|---|
| `t216_01_modal_exportar_ui.png` | Modal "Generar Reporte Kardex" con selección de PDF y Excel. |
| `t216_02_network_export_pdf.png` | `POST /api/v2/report/kardex` (PDF) — método, URL, body tipado y status 200. |
| `t216_03_documento_pdf_generado.png` | Visor "Vista previa del Reporte" con el documento generado. |
| `t216_04_network_export_excel.png` | `POST /api/v2/report/kardex/exportar?formato=XLSX` — query visible, body tipado, status 200. |
| `t216_05_estado_progreso_generando.png` | Estado de progreso: "Generando documento, por favor espere..." con `LinearProgress` y botón **GENERAR REPORTE deshabilitado** (prevención de solicitudes duplicadas). |
| `t216_06_error_controlado.png` | Error controlado: "No se pudo generar el reporte." sin trazas técnicas, con reintento disponible. |
| `t216_07_reintento_exitoso.png` | Reintento exitoso tras el error. |
| `t216_reporte_kardex.pdf` | **Binario real del PDF generado por el backend** (262.862 bytes, cabecera `%PDF-`, 54 páginas) con los filtros aplicados. |
| `t216_reporte_kardex_descarga.xlsx` | **Archivo `.xlsx` real descargado** (135.022 bytes, cabecera `PK`) desde `/exportar?formato=XLSX`. |
| `t216_network_1_export_pdf.txt` | Log textual de la exportación PDF. |
| `t216_network_2_export_excel.txt` | Log textual de la exportación Excel. |

## T-229 / HU-041.1 — Generar y descargar el Reporte de Orden de Compra (PR #128)

| Archivo | Evidencia |
|---|---|
| `t229_01_network_precarga_filtros.png` | Network de la carga inicial con la precarga de filtros V2 (`GET /api/v2/report/orden-compra/filtros`). |
| `t229_02_network_busqueda_dto_tipado.png` | `POST /api/v2/report/orden-compra/resumen?page=0&size=10` con body tipado. |
| `t229_03_grilla_ordenes.png` | Grilla de órdenes con los resultados del backend V2. |
| `t229_04_modal_exportar_ui.png` | Modal de exportación con selección de PDF y Excel. |
| `t229_05_network_export_pdf.png` | `POST /api/v2/report/orden-compra/exportar?formato=PDF` con body tipado y status 200. |
| `t229_06_network_export_excel.png` | `POST /api/v2/report/orden-compra/exportar?formato=XLSX` con query visible, body tipado y status 200. |
| `t229_07_documento_pdf_orden_compra.png` | Visor del reporte PDF generado. |
| `t229_reporte_orden_compra.pdf` | **Binario real del PDF generado por el backend** (77.447 bytes, cabecera `%PDF-`). |
| `t229_reporte_orden_compra.xlsx` | **Archivo `.xlsx` real descargado** (41.354 bytes, cabecera `PK`). |
| `t229_network_1_carga_inicial.txt` | Log textual de la carga inicial. |
| `t229_network_2_busqueda.txt` | Log textual de la búsqueda. |
| `t229_network_3_export_pdf.txt` | Log textual de la exportación PDF. |
| `t229_network_4_export_excel.txt` | Log textual de la exportación Excel. |

## T-231 / HU-041.2 — Resumen de totales de Orden de Compra (PR #129)

| Archivo | Evidencia |
|---|---|
| `t231_01_network_busqueda_resumen.png` | `POST /api/v2/report/orden-compra/resumen?page=0&size=10` con body tipado. |
| `t231_02_grilla_totales.png` | Grilla con columnas Total Unidades y Valor Total. |
| `t231_03_network_paginacion.png` | Cambio de filas por página → `?page=0&size=5`. |
| `t231_04_modal_detalle_orden.png` | Detalle de la orden en modal flotante (sin desplazar la grilla). |
| `t231_network_1_busqueda.txt` | Log textual de la búsqueda. |
| `t231_network_2_paginacion.txt` | Log textual de la paginación. |

---

## Correcciones de código aplicadas tras la revalidación de QA

| Hallazgo QA | Corrección | Rama |
|---|---|---|
| Paginación server-side no conectada a `AppDataGrid` | Se usa la prop canónica `setPaginationModel` (habilita `paginationMode="server"`) y se normaliza el modelo `{ page, pageSize }`. | T-214 / T-216 / T-229 / T-231 |
| **Total de resultados siempre en 0 (bug real)** | El contrato V2 entrega la paginación en `data.page` (`PaginacionDTO`), no en la raíz. Se corrigió la lectura; antes `rowCount` caía a `mapped.length` y el botón "página siguiente" quedaba deshabilitado. | T-214 / T-216 / T-229 / T-231 |
| Conflicto activo contra `test` | Merge de `origin/test` resuelto (`src/i18n.js`). PRs en `mergeable: clean`. | T-214 |
| Requests ajenos al módulo | `useUbicacionFilters` ya no dispara `/v1/pedido` ni `/v1/items/estado_categoria/0` salvo que el consumidor lo pida con los flags `cargarPedidos` / `cargarCategoriasEstadoPedido`. | T-214 / T-229 / T-231 |
| Preload duplicado de `/filtros` | Se eliminó la segunda llamada a `fetchCatalogs()`; el preload vive únicamente en el hook. | T-214 |
| Clave i18n `kardex.messages.resultsCount` ausente | Agregada en `es` y `en`. | T-214 |
| Pruebas sin cobertura de paginación | Pruebas que disparan página 1 y cambio de tamaño verificando la 2ª y 3ª petición a `/resumen`. | T-214 / T-231 |
