# Graph Report - /home/hermes/hermes-workspace/coagronet_frontend  (2026-08-11)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 926 nodes · 2086 edges · 124 communities (62 shown, 62 thin omitted)
- Extraction: 95% EXTRACTED · 5% INFERRED · 0% AMBIGUOUS · INFERRED: 101 edges (avg confidence: 0.76)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `31b4836f`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- axiosConfig.js
- LocalizacionGeografica.jsx
- Inicio.jsx
- e2e.shared.utils.js
- Almacen.jsx
- FormKardex.jsx
- FormRegistroEmpresa.jsx
- kardexConstants.js
- useUbicacionFilters.js
- empresa-rol-system.rf-036.spec.js
- App.jsx
- Navigator2.jsx
- MessageSnackBar.jsx
- Pedido.jsx
- modulos.rf-035.spec.js
- devDependencies
- Presentacionproducto.jsx
- scripts
- modulos.rf-035.validation.spec.js
- empresa-rol-admin.rf-036.spec.js
- dependencies
- UsuarioRoles.jsx
- requireEnv
- Chart1.jsx
- Produccion.jsx
- Producto.jsx
- docgen.mjs
- package.json
- Verify.jsx
- IngredientePP.jsx
- Proveedor.jsx
- Evaluacion_item.jsx
- Proceso.jsx
- Tipo_inventario.jsx
- resolveFromMailhog
- add-jsdoc-header.mjs
- Ocupacion.jsx
- Grid1.jsx
- Grupo.jsx
- ingrediente.jsx
- Marca.jsx
- Tipobloque.jsx
- TipoEspacio.jsx
- Tipo_evaluacion.jsx
- TipoMovimiento.jsx
- TipoSede.jsx
- TipoIdentificacion.jsx
- fix-jsx-comments.mjs
- clean-jsdoc-theme
- @babel/parser
- @babel/preset-react
- cors
- date-fns
- docdash
- dotenv
- embla-carousel-autoplay
- embla-carousel-react
- @emotion/react
- @emotion/styled
- @eslint/js
- eslint-plugin-react
- eslint-plugin-react-refresh
- fast-glob
- @fontsource/roboto
- formik
- fs-extra
- globby
- http-proxy-middleware
- i18next
- i18next-browser-languagedetector
- jsdoc-babel
- jspdf
- jspdf-autotable
- @mui/icons-material
- @mui/lab
- @mui/styled-engine-sc
- @mui/styles
- @mui/system
- @mui/x-charts
- @mui/x-data-grid
- @mui/x-date-pickers
- prop-types
- react
- react-date-range
- react-dom
- react-hook-form
- react-i18next
- react-responsive-carousel
- react-router-dom
- react-slick
- serve
- slick-carousel
- styled-components
- validator
- yup
- @types/react
- vite
- @vitejs/plugin-react
- sanitize-jsx-comments.mjs

## God Nodes (most connected - your core abstractions)
1. `instance` - 109 edges
2. `MessageSnackBar()` - 51 edges
3. `validateCamposBase()` - 48 edges
4. `StackButtons()` - 27 edges
5. `makeLoaders()` - 17 edges
6. `scripts` - 16 edges
7. `AppDataGrid()` - 15 edges
8. `getActiveDialog()` - 14 edges
9. `FormKardex()` - 13 edges
10. `useUbicacionFilters()` - 13 edges

## Surprising Connections (you probably didn't know these)
- `Presentacionproducto()` --indirect_call--> `u()`  [INFERRED]
  src/components/Presentacionproducto/Presentacionproducto.jsx → docs/scripts/prettify/prettify.js
- `KardexArticulosSection()` --calls--> `resolveKardexId()`  [EXTRACTED]
  src/components/Kardex/KardexArticulosSection.jsx → src/components/Kardex/utils/kardexFormatters.js
- `useKardexData()` --calls--> `toArray()`  [EXTRACTED]
  src/components/Kardex/hooks/useKardexData.js → src/components/Kardex/utils/kardexFormatters.js
- `Navbar()` --calls--> `useThemeToggle()`  [EXTRACTED]
  src/components/Navbar.jsx → src/components/dashboard/ThemeToggleProvider.jsx
- `RE_ordenCompra()` --calls--> `useUbicacionFilters()`  [EXTRACTED]
  src/components/RE_oc/re_oc.jsx → src/components/useUbicacionFilters.js

## Import Cycles
- None detected.

## Communities (124 total, 62 thin omitted)

### Community 0 - "axiosConfig.js"
Cohesion: 0.06
Nodes (48): instance, FormBloque(), BaseFormCampos(), FormCriterioEvaluacion(), GridCriterioEvaluacion(), FormEspacio(), FormGrupo(), FormIngrediente() (+40 more)

### Community 1 - "LocalizacionGeografica.jsx"
Cohesion: 0.06
Nodes (38): CategoriaEstado(), FormCategoriaEstado(), GridCategoriaEstado(), AppDataGrid(), loadStoredVisibilityModel(), GridActionBar(), SectionHeader(), EmpresaRol() (+30 more)

### Community 2 - "Inicio.jsx"
Cohesion: 0.08
Nodes (32): App(), axiosV2, ChangeLogoDialog(), ChangePasswordDialog(), decodeJwt(), go(), AppBarComponent(), decodeJwt() (+24 more)

### Community 3 - "e2e.shared.utils.js"
Cohesion: 0.06
Nodes (34): ADMIN_EMAIL, ADMIN_PASSWORD, authHeaders(), AUTOREGISTER_PASSWORD, BACKEND_URI, COMPANY_ADMIN_EMAIL, COMPANY_ADMIN_PASSWORD, COMPANY_CONTEXT_NAME (+26 more)

### Community 4 - "Almacen.jsx"
Cohesion: 0.10
Nodes (23): Almacen(), FormAlmacen(), Bloque(), BloqueToolbar(), GridBloque(), CrudFilterModal(), makeLoaders(), uniqById() (+15 more)

### Community 5 - "FormKardex.jsx"
Cohesion: 0.11
Nodes (26): FormArticuloKardex(), mapHeaderAndItemsToKardexPayload(), toArray(), DEFAULT_ARTICLE_FILTERS, extractApiMessage(), FormKardex(), getPresentacionId(), isPresentacionDevolutiva() (+18 more)

### Community 6 - "FormRegistroEmpresa.jsx"
Cohesion: 0.10
Nodes (19): Copyright(), Drawer, SiteProps, Empresa(), FormEmpresa(), GridEmpresa(), FormPersona(), GridPersona() (+11 more)

### Community 7 - "kardexConstants.js"
Cohesion: 0.13
Nodes (16): DEFAULT_FILTERS, ESTADOS, MAX_RECORDS, MESSAGES, PAGE_SIZE, ROLES, TIPO_MOV_ENTRADA_COMPRA, useKardexAuth() (+8 more)

### Community 8 - "useUbicacionFilters.js"
Cohesion: 0.13
Nodes (20): FormArticuloOrdenCompra(), isSqlSuspicious(), schema, GridArticuloOrdenCompra(), Toolbar(), CustomToolbar(), GridOrdenCompra(), OrdenCompra() (+12 more)

### Community 9 - "empresa-rol-system.rf-036.spec.js"
Cohesion: 0.14
Nodes (21): getGridRowsSnapshot(), openSubsistemaAndModulo(), parseRowId(), pickSafeRowByHighestId(), waitForPermissionsTreeReady(), checkFirstDialogCheckbox(), clickActionButton(), clickDialogButton() (+13 more)

### Community 10 - "App.jsx"
Cohesion: 0.15
Nodes (10): moduleMap, Contenido(), Navigator2(), GridPresentacion(), Presentacion(), ChangePasswordInitial(), GridTipoProduccion(), TipoProduccion() (+2 more)

### Community 11 - "Navigator2.jsx"
Cohesion: 0.15
Nodes (11): bottomToSidebar, components, icons, moduleImages, sidebarToBottom, MediaCard(), GridMovimineto(), Movimineto() (+3 more)

### Community 12 - "MessageSnackBar.jsx"
Cohesion: 0.25
Nodes (10): MessageSnackBar(), VistaPreviaPDFOrdenCompra(), RE_kardexPedido(), RE_productoVencimiento(), RE_kardexPedido(), asArray(), UbicacionProductoVencimientoFilters(), asArray() (+2 more)

### Community 13 - "Pedido.jsx"
Cohesion: 0.18
Nodes (11): FormArticuloPedido(), FormPedido(), ArticuloToolbar(), GridArticuloPedido(), GridPedido(), PedidoToolbar(), Pedido(), VistaPreviaPDFPedido() (+3 more)

### Community 14 - "modulos.rf-035.spec.js"
Cohesion: 0.20
Nodes (16): countFrontendGridPages(), ensureGridColumnVisible(), expectGridCheckboxIndicatorInColumn(), expectGridIconIndicatorInColumn(), findGridCellInColumnAcrossPages(), getGridColumnIndex(), goToGridFirstPage(), goToNextGridPage() (+8 more)

### Community 15 - "devDependencies"
Cohesion: 0.12
Nodes (17): @babel/core, @babel/preset-env, eslint-plugin-react-hooks, globals, jsdoc, devDependencies, @babel/core, @babel/preset-env (+9 more)

### Community 16 - "Presentacionproducto.jsx"
Cohesion: 0.21
Nodes (14): B(), C(), D(), E(), k(), L(), M(), u() (+6 more)

### Community 17 - "scripts"
Cohesion: 0.12
Nodes (16): scripts, autodoc, build, dev, docs, lint, mailhog:logs, mailhog:start (+8 more)

### Community 18 - "modulos.rf-035.validation.spec.js"
Cohesion: 0.18
Nodes (12): RFC-9457, clickCreateModuloButton(), clickDialogSelectFirstOption(), clickDialogSelectOption(), fillDialogField(), fillDialogFieldByName(), fillModuloForm(), NOADMIN_EMAIL (+4 more)

### Community 19 - "empresa-rol-admin.rf-036.spec.js"
Cohesion: 0.26
Nodes (6): ensureIdSortDescending(), getGridRowsSnapshot(), openSubsistemaAndModulo(), parseRowId(), pickSafeRowByHighestId(), waitForPermissionsTreeReady()

### Community 20 - "dependencies"
Cohesion: 0.18
Nodes (11): axios, glob, jwt-decode, @mui/material, dependencies, axios, glob, jwt-decode (+3 more)

### Community 21 - "UsuarioRoles.jsx"
Cohesion: 0.29
Nodes (8): FormUsuarioRoles(), toDateTimeLocal(), CustomToolbar(), GridUsuarioRoles(), buildMap(), emptyRow, extractItems(), UsuarioRoles()

### Community 22 - "requireEnv"
Cohesion: 0.25
Nodes (10): authenticateByApi(), loginApiGetToken(), loginAsAdmin(), loginAsAdminGetToken(), loginAsNoAdmin(), loginAsSystemAdmin(), loginByApi(), registerByApi() (+2 more)

### Community 23 - "Chart1.jsx"
Cohesion: 0.28
Nodes (3): Chart(), data, Deposits()

### Community 24 - "Produccion.jsx"
Cohesion: 0.39
Nodes (6): FormProduccion(), GridProduccion(), ProduccionToolbar(), Produccion(), toList(), toMap()

### Community 25 - "Producto.jsx"
Cohesion: 0.36
Nodes (5): emptyForm, FormProducto(), GridProducto(), ProductoToolbar(), Producto()

### Community 26 - "docgen.mjs"
Cohesion: 0.48
Nodes (6): autoFixSourceCode(), cleanOldDocComments(), createFallbackComponent(), parser, processFile(), run()

### Community 27 - "package.json"
Cohesion: 0.29
Nodes (6): homepage, name, private, proxy, type, version

### Community 28 - "Verify.jsx"
Cohesion: 0.43
Nodes (5): BACKEND_BASE, verifyAxios, verifyEmail(), persistTokens(), Verify()

### Community 29 - "IngredientePP.jsx"
Cohesion: 0.43
Nodes (4): FormIngredientePresentacionP(), GridIngredientePresentacionP(), IngredientePPToolbar(), IngredientePresentacionProducto()

### Community 30 - "Proveedor.jsx"
Cohesion: 0.43
Nodes (4): FormProveedor(), GridProveedor(), ProveedorToolbar(), Proveedor()

### Community 31 - "Evaluacion_item.jsx"
Cohesion: 0.47
Nodes (3): EvaluacionItem(), FormEvaluacionItem(), GridEvaluacionItem()

### Community 32 - "Proceso.jsx"
Cohesion: 0.60
Nodes (4): GridProceso(), Proceso(), toList(), toMap()

### Community 33 - "Tipo_inventario.jsx"
Cohesion: 0.47
Nodes (3): FormTipoInventario(), GridTipoInventario(), TipoInventario()

### Community 34 - "resolveFromMailhog"
Cohesion: 0.53
Nodes (6): extractTokenFromVerificationLink(), extractVerificationLink(), resolveFromMailhog(), resolveFromProvider(), resolveVerificationLink(), resolveVerificationToken()

### Community 35 - "add-jsdoc-header.mjs"
Cohesion: 0.40
Nodes (3): COMPONENTS_DIR, __dirname, __filename

### Community 36 - "Ocupacion.jsx"
Cohesion: 0.50
Nodes (3): FormOcupacion(), GridOcupacion(), Ocupacion()

## Knowledge Gaps
- **143 isolated node(s):** `__filename`, `__dirname`, `COMPONENTS_DIR`, `parser`, `name` (+138 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **62 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `dependencies` connect `dependencies` to `package.json`, `@babel/parser`, `cors`, `date-fns`, `embla-carousel-autoplay`, `embla-carousel-react`, `@emotion/react`, `@emotion/styled`, `fast-glob`, `@fontsource/roboto`, `formik`, `http-proxy-middleware`, `i18next`, `i18next-browser-languagedetector`, `jspdf`, `jspdf-autotable`, `@mui/icons-material`, `@mui/lab`, `@mui/styled-engine-sc`, `@mui/styles`, `@mui/system`, `@mui/x-charts`, `@mui/x-data-grid`, `@mui/x-date-pickers`, `prop-types`, `react`, `react-date-range`, `react-dom`, `react-hook-form`, `react-i18next`, `react-responsive-carousel`, `react-router-dom`, `react-slick`, `serve`, `slick-carousel`, `styled-components`, `validator`, `yup`?**
  _High betweenness centrality (0.134) - this node is a cross-community bridge._
- **Why does `instance` connect `axiosConfig.js` to `LocalizacionGeografica.jsx`, `Inicio.jsx`, `Almacen.jsx`, `FormKardex.jsx`, `FormRegistroEmpresa.jsx`, `kardexConstants.js`, `useUbicacionFilters.js`, `App.jsx`, `Navigator2.jsx`, `MessageSnackBar.jsx`, `Pedido.jsx`, `Presentacionproducto.jsx`, `UsuarioRoles.jsx`, `Produccion.jsx`, `Producto.jsx`, `IngredientePP.jsx`, `Proveedor.jsx`, `Proceso.jsx`, `Tipo_inventario.jsx`, `Ocupacion.jsx`, `Grupo.jsx`, `ingrediente.jsx`, `Marca.jsx`, `Tipobloque.jsx`, `TipoEspacio.jsx`, `Tipo_evaluacion.jsx`, `TipoMovimiento.jsx`, `TipoSede.jsx`, `TipoIdentificacion.jsx`?**
  _High betweenness centrality (0.081) - this node is a cross-community bridge._
- **Why does `devDependencies` connect `devDependencies` to `eslint-plugin-react-refresh`, `@types/react`, `vite`, `fs-extra`, `globby`, `@vitejs/plugin-react`, `jsdoc-babel`, `clean-jsdoc-theme`, `@babel/preset-react`, `docdash`, `dotenv`, `package.json`, `@eslint/js`, `eslint-plugin-react`?**
  _High betweenness centrality (0.069) - this node is a cross-community bridge._
- **What connects `__filename`, `__dirname`, `COMPONENTS_DIR` to the rest of the system?**
  _143 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `axiosConfig.js` be split into smaller, more focused modules?**
  _Cohesion score 0.05792620078334364 - nodes in this community are weakly interconnected._
- **Should `LocalizacionGeografica.jsx` be split into smaller, more focused modules?**
  _Cohesion score 0.055178652193577565 - nodes in this community are weakly interconnected._
- **Should `Inicio.jsx` be split into smaller, more focused modules?**
  _Cohesion score 0.07712765957446809 - nodes in this community are weakly interconnected._