# Release Notes — v0.3.2

**Fecha:** 2026-04-20
**Rama:** `release/0.3.2`
**Commit base:** `1b8ea4f3` — _Delete KardexListDto.java_
**Alcance:** 294 commits, 341 archivos modificados (+21 717 / −1 816 líneas)

---

## Nuevas funcionalidades

### Módulo Kardex (RF-025)

- Registro de movimientos de inventario con validación de responsables y presentaciones.
- Listado de movimientos con filtros dinámicos y paginación.
- Actualización de movimientos maestros de Kardex (HU-025.4).
- Vista administrativa (`KardexAdminView`) con filtrado por empresa.
- Soporte para traslados entre almacenes.
- Borrado lógico y metadatos de auditoría en Kardex.
- Cierre de inventario mensual con cálculo de stock vía función de base de datos.
- Reportes: producción por Kardex, presentaciones de producto, vencimiento, proveedor, bloque y sede.

### Seguridad y Autenticación

- `DynamicRolePermissionService`: resolución dinámica de autoridades por rol en tiempo de ejecución.
- `RolPermisoDualAuthResolver`: resolución dinámica de `empresaId` según el rol del usuario autenticado.
- Manejadores personalizados para acceso denegado y token JWT expirado.
- Cambio de contraseña inicial con validación y notificación por email.
- Registro de usuarios internos de la empresa con activación de rol.
- Filtrado de asignación de permisos por rol de usuario.
- Propagación del contexto de seguridad a hilos secundarios (`MODE_INHERITABLETHREADLOCAL`).

### Multi-Tenancy

- Aislamiento completo de datos por empresa usando Hibernate 6.
- Integración de `tenantEmpresaId` en entidades `UsuarioRol` y `EmpresaRol`.
- Configuración de `@TenantId` en JPA para resolución automática de tenant.

### Gestión de Módulos y Menú

- CRUD completo para `Modulo`, `SubSistema`, `TipoAplicacion` y `TipoModulo`.
- Endpoint de menú estructurado por subsistema y rol.
- Asignación y configuración de obligatoriedad de módulos por empresa (HU-035.4).
- Servicio de métricas: conteo de registros por entidad asociada a la empresa.

### Roles y Permisos

- CRUD para `EmpresaRol` accesible por admin sistema y admin empresa.
- Gestión de `UsuarioRol` con endpoints por empresa y toggle bidireccional activo/inactivo.
- Inactivación manual de usuario-rol con cronjob de limpieza de asignaciones preferidas.
- Sincronización de `modulo_empresa` en asignación y reemplazo de permisos.

### Otros módulos

- CRUD de `EvaluacionItem` con validaciones de negocio.
- CRUD de `PedidoCotizacion` con validaciones.
- Endpoint de stock con lectura paginada.
- CRUD de `EstadoCategoria`.

---

## Correcciones

| Área | Descripción |
|---|---|
| `rol-permiso` | Propagar `empresaId` objetivo en flujos por módulo |
| `rol-permiso` | Filtrar adminEmpresa en asignación y menú |
| `rol-permiso` | Sincronizar `modulo_empresa` en asignación y reemplazo |
| `empresa-rol` | Mapear `empresaId` en response del sistema |
| `usuario-rol` | Toggle bidireccional activo ↔ inactivo |
| `menu` | Evitar problema N+1 en asignación de módulos |
| `audit` | Consolidar configuración y verificar autenticación antes de auditar |
| Auth | Resolver correctamente el rol del usuario que asigna roles |
| Reportes | Corregir condición JOIN en reporte de vencimiento de productos |
| General | Agregar `server.forward-headers-strategy` para compatibilidad con proxies inversos |

---

## Mejoras de rendimiento

- JDBC batching habilitado (`batch_size=50`) con `reWriteBatchedInserts` activado.
- Actualización de Spring Boot a **3.5.12** y SpringDoc a **2.8.6**.
- Inserción masiva optimizada en `RolPermisoService` (elimina N+1).
- Lazy loading corregido en la relación `EstadoCategoria`.
- Modo de arranque JPA en `deferred` para compatibilidad con multi-tenant.

---

## Dependencias actualizadas

| Dependencia | Versión anterior | Nueva versión |
|---|---|---|
| Spring Boot | — | 3.5.12 |
| SpringDoc OpenAPI | — | 2.8.6 |

---

## Breaking changes

### `feat(kardex)!` — Traslados de almacén
- `KardexRequestDTO` y `ArticuloRequestDTO` migrados a `record` de Java; constructores anteriores incompatibles.
- Nuevas reglas de validación obligatorias en ítems de Kardex.

### `refactor(auth)!` — Migración de UsuarioEstado
- `UsuarioEstado` es ahora una entidad JPA, no un enum.
- `CustomUserDetails` reemplaza el retorno de `User` en `MyUserDetailsService`.

### `refactor(api)!` — Tipos de datos monetarios y de fecha
- Campos monetarios migrados de `Double`/`Float` a `BigDecimal`.
- Campos de fecha migrados a `LocalDate` e `Instant` (eliminado uso de `java.util.Date`).

---

## Notas para QA

- Verificar flujos de asignación de roles con perfiles `ADMIN_SISTEMA` y `ADMIN_EMPRESA`.
- Validar traslados de almacén en Kardex con ítems desgregados y no desgregados.
- Confirmar que el menú se genera correctamente por subsistema y rol tras los cambios de multi-tenant.
- Revisar reportes de vencimiento y producción con datos reales antes del merge a `main`.
