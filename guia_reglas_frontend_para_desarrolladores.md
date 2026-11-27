# Guia tecnica del frontend para desarrolladores e IAs

Fecha de corte: 2026-11-25
Proyecto: `InventarioInmero_frontend`

## Objetivo

Este documento sirve como contexto tecnico operativo para cualquier desarrollador o IA que necesite comprender, ejecutar y modificar el frontend sin romper convenciones ya establecidas.

No describe solo reglas generales; tambien resume la forma real en la que esta organizado el proyecto, como se levanta, como se conectan los componentes reutilizables y como deben integrarse las traducciones, la capa API y la documentacion de codigo.

## 1. Resumen rapido del proyecto

- Stack principal: React 18 + Vite + Material UI.
- Internacionalizacion: `i18next` + `react-i18next`.
- Cliente HTTP principal: `src/components/axiosConfig.js`.
- Entrada principal de i18n: `src/i18n.js`.
- Bootstrap principal: `src/App.jsx`.
- Modulos y reutilizables compartidos: `src/components/common`.
- Idiomas activos: `es` y `en`.
- Base path de la app: `/inventario`.

## 2. Estructura relevante para trabajar

Rutas y carpetas que una IA debe revisar primero:

- `src/App.jsx`
  Punto de entrada funcional del frontend.
- `src/i18n.js`
  Registro central de archivos de traduccion y resolucion de idioma.
- `src/components/axiosConfig.js`
  Cliente Axios base con `Authorization` y `Accept-Language`.
- `src/components/common/SectionHeader.jsx`
  Encabezado reutilizable para modulos.
- `src/components/common/GridActionBar.jsx`
  Barra de acciones CRUD reutilizable.
- `src/components/common/AppDataGrid.jsx`
  Grilla reutilizable con formato, i18n y soporte server-side.
- `src/components/common/CrudFilterModal.jsx`
  Modal de filtros declarativo.
- `src/locales/es/*.json`
  Traducciones en espanol por dominio.
- `src/locales/en/*.json`
  Traducciones en ingles por dominio.

Arquitectura objetivo de carpetas:

- `src/app`
- `src/routes`
- `src/layouts`
- `src/features`
- `src/components`
- `src/lib`
- `src/theme`
- `src/locales`
- `src/assets`

Aunque el proyecto todavia convive con estructura historica dentro de `src/components`, toda modificacion nueva debe intentar acercarse a esta organizacion y no aumentar deuda innecesaria.

## 3. Comandos de arranque y trabajo diario

Ejecutar desde:

`d:\PASANTIAS\InventarioInmero_frontend`

### 3.1 Instalacion

```bash
npm install
```

### 3.2 Desarrollo local

```bash
npm run dev
```

Notas:

- El proyecto usa Vite.
- La aplicacion se sirve bajo `/inventario`.
- Existe un problema historico intermitente de `invalid hook call` en `vite dev`.
- Para validaciones funcionales estables, muchas veces conviene usar `build + preview`.

### 3.3 Build de validacion

```bash
npm run build
```

### 3.4 Preview estable del build

```bash
npm run preview
```

### 3.5 Lint

```bash
npm run lint
```

### 3.6 Pruebas unitarias

```bash
npm run test
```

### 3.7 Pruebas end-to-end

```bash
npm run test:e2e
```

Variantes disponibles:

```bash
npm run test:e2e:ui
npm run test:e2e:headed
npm run test:e2e:debug
```

## 4. Variables y configuracion clave

### 4.1 Backend

El cliente API principal se crea en `src/components/axiosConfig.js` y usa:

```js
baseURL: import.meta.env.VITE_BACKEND_URI + "/api"
```

Por tanto, cualquier entorno debe definir al menos:

```env
VITE_BACKEND_URI=https://...
```

### 4.2 Idioma

`src/i18n.js` resuelve el idioma con este orden:

1. parametro recibido;
2. `localStorage.preferredLanguage`;
3. `localStorage.i18nextLng`;
4. `navigator.language`.

Idiomas soportados:

- `es`
- `en`

### 4.3 Autenticacion

La app espera en `localStorage` valores como:

- `token`
- `token_expiration`
- `preferredLanguage`
- `sidebarOpen`

No se debe rediseñar esta capa por iniciativa propia; la migracion a cookies HttpOnly esta fuera del alcance actual.

## 5. Flujo recomendado para una IA antes de tocar codigo

Antes de implementar cambios, revisar en este orden:

1. `package.json`
   para comandos y dependencias.
2. `src/App.jsx`
   para bootstrap, layout general y carga de i18n.
3. `src/i18n.js`
   para namespaces y archivos de traduccion registrados.
4. `src/components/axiosConfig.js`
   para contrato base de llamadas HTTP.
5. El modulo objetivo actual.
6. Los componentes compartidos en `src/components/common`.
7. Los archivos `src/locales/es/*.json` y `src/locales/en/*.json` del dominio afectado.

Si el cambio es UI CRUD, buscar primero ejemplos en modulos ya migrados al patron compartido como:

- `src/components/almacen/Almacen.jsx`
- `src/components/EmpresaRol/EmpresaRol.jsx`
- `src/components/Usuario/Usuario.jsx`

## 6. Patron estandar para un modulo CRUD

Cuando un modulo lo permita, debe seguir esta estructura:

1. `SectionHeader`
2. `GridActionBar`
3. `AppDataGrid`
4. `CrudFilterModal`
5. formulario modal propio
6. `MessageSnackBar` u otro feedback equivalente

### 6.1 Esqueleto recomendado

```jsx
<Box sx={{ p: 2 }}>
  <SectionHeader titleKey="modulo.title" />

  <GridActionBar
    onAdd={handleCreate}
    onUpdate={handleEdit}
    onDelete={handleDelete}
    canUpdate={Boolean(selectedRow)}
    canDelete={Boolean(selectedRow)}
    extraActions={extraActions}
    onFilters={() => setOpenFilters(true)}
    onClearFilters={handleFiltersClear}
    hasActiveFilters={hasActiveFilters}
    rightActions={rightActions}
  />

  <AppDataGrid
    rows={rows}
    columns={columns}
    selectedRow={selectedRow}
    setSelectedRow={setSelectedRow}
    containerSx={{ borderRadius: 4 }}
  />

  <CrudFilterModal
    open={openFilters}
    onClose={() => setOpenFilters(false)}
    titleKey="modulo.filters.title"
    fields={fields}
    values={filters}
    onChange={handleFiltersChange}
    onClear={handleFiltersClear}
    onApply={handleFiltersApply}
  />

  <FormModulo ... />
</Box>
```

### 6.2 Reglas de implementacion

- No crear una nueva grilla si `AppDataGrid` cubre el caso.
- No crear una nueva barra de acciones si `GridActionBar` cubre el caso.
- No crear un modal de filtros ad hoc si `CrudFilterModal` cubre el caso.
- No duplicar encabezados si `SectionHeader` cubre el caso.
- Si falta una capacidad reusable, extender el componente compartido antes de crear otra variante aislada.

## 7. Como usar los componentes reutilizables

### 7.1 `SectionHeader`

Archivo:

`src/components/common/SectionHeader.jsx`

Uso esperado:

- para titulos y subtitulos de modulo;
- con `titleKey` y `subtitleKey` cuando el texto sea visible al usuario;
- con `rightNode` si se necesita una accion auxiliar arriba de la grilla.

Evitar:

- titulos hardcoded cuando ya existe i18n;
- estilos inline innecesarios para replicar el mismo encabezado.

### 7.2 `GridActionBar`

Archivo:

`src/components/common/GridActionBar.jsx`

Uso esperado:

- acciones CRUD principales;
- boton de filtros;
- limpiar filtros;
- acciones secundarias a la derecha.

Props mas usadas:

- `onAdd`
- `onUpdate`
- `onDelete`
- `canUpdate`
- `canDelete`
- `extraActions`
- `onFilters`
- `onClearFilters`
- `hasActiveFilters`
- `rightActions`

Regla:

- la habilitacion de editar y eliminar debe depender del estado real de `selectedRow`.
- si el modulo requiere botones adicionales, deben integrarse primero mediante `extraActions` o `rightActions` antes de crear una barra paralela.
- `extraActions` se usa para botones adicionales del bloque principal CRUD.
- `rightActions` se usa para acciones secundarias alineadas con filtros y utilidades del lado derecho.

Ejemplo de botones extra:

```jsx
const extraActions = (
  <Button onClick={handleExport}>
    {t("common.actions.export")}
  </Button>
);

const rightActions = (
  <Button onClick={handleOpenSummary}>
    {t("modulo.actions.summary")}
  </Button>
);

<GridActionBar
  onAdd={handleCreate}
  onUpdate={handleEdit}
  onDelete={handleDelete}
  canUpdate={Boolean(selectedRow)}
  canDelete={Boolean(selectedRow)}
  extraActions={extraActions}
  onFilters={() => setOpenFilters(true)}
  onClearFilters={handleFiltersClear}
  hasActiveFilters={hasActiveFilters}
  rightActions={rightActions}
/>
```

Reglas de uso para botones adicionales:

- deben reutilizar los estilos heredados del propio `GridActionBar`;
- deben seguir i18n igual que el resto de acciones;
- no deben duplicar acciones ya existentes en `onAdd`, `onUpdate`, `onDelete` u `onFilters`;
- si una necesidad de botones extra se repite entre modulos, conviene documentarla y evaluar si debe convertirse en una extension comun del componente.

### 7.3 `AppDataGrid`

Archivo:

`src/components/common/AppDataGrid.jsx`

Capacidades importantes:

- i18n interno de toolbar y labels;
- columnas declarativas por `type`;
- soporte para `status`, `boolean`, `number`, `date`, `datetime`;
- soporte server-side con `paginationModel`, `setPaginationModel` y `rowCount`;
- persistencia opcional de visibilidad de columnas.

Patron recomendado de columnas:

```jsx
const columns = [
  { field: "id", headerKey: "modulo.columns.id", type: "number", width: 80 },
  { field: "nombre", headerKey: "modulo.columns.name", type: "text", flex: 1 },
  { field: "estadoId", headerKey: "modulo.columns.status", type: "status", width: 120 },
];
```

Reglas:

- usar `headerKey` en vez de `headerName` cuando sea texto visible;
- usar `type` correcto para que el componente formatee bien;
- si hay paginacion backend, no reinventar la logica por fuera del grid.

### 7.4 `CrudFilterModal`

Archivo:

`src/components/common/CrudFilterModal.jsx`

Patron:

- declarativo;
- recibe `fields`;
- cada campo puede traer `labelKey`, `placeholderKey`, `getOptions`, `disabled`, `clearChildren`, `fullRow`.

Ejemplo de `fields`:

```js
const fields = [
  {
    name: "paisId",
    labelKey: "modulo.filters.country",
    getOptions: getPaises,
    clearChildren: ["deptoId", "municipioId"],
  },
  {
    name: "deptoId",
    labelKey: "modulo.filters.department",
    getOptions: getDepartamentos,
    disabled: (values) => !values.paisId,
    clearChildren: ["municipioId"],
  },
];
```

Reglas:

- no disparar cargas de opciones cuando `open === false`;
- limpiar hijos dependientes al cambiar el padre;
- ignorar respuestas obsoletas al cerrar el modal;
- memoizar loaders y configuraciones si su recreacion produce llamadas redundantes.

### 7.5 Patrones y estandar de formularios

Los formularios del proyecto no deben resolverse como implementaciones aisladas. Deben seguir un patron consistente para creacion, edicion, validacion y feedback al usuario.

#### Estructura recomendada

Cuando el formulario sea modal, la base esperada es:

```jsx
<Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
  <DialogTitle>
    {formMode === "edit" ? t("modulo.form.editTitle") : t("modulo.form.createTitle")}
  </DialogTitle>
  <DialogContent>
    {/* campos */}
  </DialogContent>
  <DialogActions>
    <Button onClick={handleClose}>
      {t("common.actions.cancel")}
    </Button>
    <Button variant="contained" onClick={handleSubmit} disabled={saving}>
      {saving ? t("common.actions.saving") : t("common.actions.save")}
    </Button>
  </DialogActions>
</Dialog>
```

#### Props frecuentes esperadas

En formularios CRUD modales, el contrato mas comun del proyecto es:

- `open`
- `setOpen` o `onClose`
- `formMode`
- `selectedRow`
- `reloadData`
- `setMessage`
- `authHeaders` cuando el modulo aun los pasa explicitamente

Regla:

- si el modulo ya tiene un patron de props definido, debe respetarse en lugar de inventar uno nuevo;
- si se crea un formulario nuevo, debe mantenerse lo mas alineado posible con este contrato.

#### Estado interno

Patron recomendado:

- definir `initialData`;
- usar `formData` como estado principal;
- usar `errors` para errores de validacion por campo;
- usar `saving` cuando el submit sea asincrono;
- resetear errores y datos cuando el modal se abre en modo create;
- precargar los datos cuando el modal se abre en modo edit.

Ejemplo de estados comunes:

```jsx
const [formData, setFormData] = useState(initialData);
const [errors, setErrors] = useState({});
const [saving, setSaving] = useState(false);
```

#### Carga en modo edicion

Reglas:

- la carga inicial de datos de edicion debe ejecutarse solo cuando `open === true`;
- si el formulario necesita catalogos, no deben cargarse cuando el modal esta cerrado;
- si el formulario depende de filtros o contexto externo, debe precargar el valor solo si aplica;
- al cerrar el modal, debe limpiarse el estado transitorio que pueda contaminar la siguiente apertura.

#### Validaciones

Patron esperado:

- validar antes del submit;
- reflejar errores por campo con `error` y `helperText`;
- resolver mensajes visibles desde i18n;
- centralizar la validacion en una funcion `validate()` o en helpers del modulo;
- si el backend tiene validaciones conocidas que generan mensajes confusos, anticiparlas en frontend.

Ejemplo:

```jsx
const validate = () => {
  const newErrors = {};

  if (!formData.nombre?.trim()) {
    newErrors.nombre = t("modulo.form.validation.nameRequired");
  }

  setErrors(newErrors);
  return Object.keys(newErrors).length === 0;
};
```

#### Validaciones por tipo de campo

Ademas de la estructura general de `validate()`, los formularios deben seguir reglas base por tipo de dato. Estas reglas toman como referencia lo ya trabajado y endurecido en `src/components/Usuario/FormUsuario.jsx`.

##### Correos electronicos

Reglas:

- si el correo es obligatorio, no permitir valor vacio;
- validar formato de email antes de enviar;
- usar `type="email"` cuando el control sea `TextField`;
- diferenciar si el campo es correo principal, personal o corporativo solo a nivel de traduccion y negocio, no relajando la validacion tecnica.

Patron comun:

```jsx
if (!emailPersonal) {
  nextErrors.emailPersonal = t("modulo.form.validation.emailRequired");
} else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(emailPersonal)) {
  nextErrors.emailPersonal = t("modulo.form.validation.invalidEmail");
}
```

##### Identificacion o documento

Reglas:

- validar siempre junto con `tipoIdentificacionId`;
- si el tipo de documento exige numerico, aceptar solo digitos;
- si el tipo permite alfanumerico, permitir letras, numeros y guion;
- controlar longitud minima y maxima;
- normalizar espacios antes de validar;
- no asumir que todos los documentos comparten el mismo patron.

Patron derivado de `Usuario`:

- documentos numericos: `^\d{5,20}$`
- documentos alfanumericos: `^[A-Za-z0-9-]{5,20}$`

Recomendacion:

- resolver la regla a partir del nombre o tipo del documento en un helper como `resolveIdentificationRule`;
- si un modulo nuevo usa identificacion, reutilizar esta logica en lugar de inventar una validacion distinta.

##### Fechas

Reglas:

- las fechas obligatorias deben validarse antes del submit;
- cuando exista fecha inicial y fecha final, la final no puede ser menor que la inicial;
- si el campo depende de otro, reportar un mensaje especifico y no uno generico;
- para rangos, validar orden cronologico;
- evitar enviar fechas vacias cuando el backend exige valor.

Ejemplos de validaciones esperadas:

- fecha inicio obligatoria;
- fecha fin requiere fecha inicio previa;
- fecha fin debe ser mayor o igual a fecha inicio.

##### Direcciones

Reglas:

- si se valida direccion, debe incluir estructura minima util, no solo texto libre;
- normalizar espacios repetidos antes de validar;
- no aceptar solo numeros ni solo simbolos;
- controlar longitud maxima;
- permitir caracteres frecuentes de direccion como `#`, `.`, `,`, `-`, `/` y `°`.

Patron ya usado en `Usuario`:

```jsx
const ADDRESS_REGEX =
  /^(?=.*[A-Za-zÁÉÍÓÚÜÑáéíóúüñ])(?=.*\d)[A-Za-zÁÉÍÓÚÜÑáéíóúüñ0-9\s#.,\-°/]+$/;
```

Recomendacion:

- si el campo `direccion` es obligatorio, validarlo contra una expresion reutilizable del modulo o una constante compartida;
- si no es obligatorio, al menos validar formato cuando venga informado.

##### Celulares y telefonos

Reglas:

- diferenciar telefono libre de celular estructurado segun el caso de uso;
- para celular colombiano, el patron actual usado en `Usuario` es de 10 digitos;
- bloquear caracteres no numericos cuando el campo exige solo numeros;
- aplicar `maxLength` consistente desde UI ademas de validar en submit.

Patron actual de referencia:

```jsx
if (celular && !/^\d{10}$/.test(celular)) {
  nextErrors.celular = t("modulo.form.validation.invalidPhone");
}
```

##### Nombres, apellidos y textos cortos

Reglas:

- los campos requeridos deben validarse con `trim()`;
- no aceptar solo espacios;
- cuando aplique, bloquear caracteres peligrosos o no esperados;
- si hay restriccion de longitud, reflejarla tambien en el control visual.

##### Selects y catalogos obligatorios

Reglas:

- no asumir que `0`, `null` o `""` son valores validos;
- convertir ids a numero cuando el contrato del backend lo requiera;
- si el catalogo depende de otro campo, limpiar el valor hijo al cambiar el padre;
- validar selects obligatorios aunque visualmente tengan placeholder.

##### Archivos

Cuando el formulario permita adjuntos, logos o imagenes:

- validar tamano maximo;
- validar tipos MIME permitidos;
- limpiar previews u objetos temporales al cerrar el modal;
- mostrar un mensaje claro cuando el archivo no cumpla el formato.

Esto ya aparece, por ejemplo, en `FormEmpresa.jsx`.

##### Reglas extra recomendadas

- evitar regex duplicadas en varios formularios si la validacion ya existe en otro modulo activo;
- si una validacion compleja ya fue endurecida por QA en `Usuario`, conviene tomarla como referencia base para modulos nuevos;
- cuando un campo tenga una regla de negocio sensible, dejarla documentada en traducciones o helpers, no enterrada dentro de JSX;
- si un backend devuelve errores frecuentes y previsibles, anticiparlos con validacion preventiva en frontend.

#### Construccion del payload

Recomendacion:

- crear una funcion `buildPayload()` cuando el formulario necesite normalizar datos antes de enviar;
- convertir ids numericos de selects antes de enviar al backend;
- hacer `trim()` en textos cuando aplique;
- no enviar al backend campos de UI que no pertenecen al contrato de la API.

#### Feedback y guardado

Reglas:

- al guardar, deshabilitar el boton principal con `saving`;
- mostrar mensajes de exito o error via `setMessage`;
- cerrar el formulario solo despues de una respuesta exitosa;
- llamar `reloadData()` despues de guardar correctamente;
- en errores de API, priorizar `response.data.message` o equivalente antes de usar un fallback generico.

#### i18n en formularios

Todo formulario debe mover a traducciones:

- titulos de crear y editar;
- labels de campos;
- placeholders;
- helper texts;
- mensajes de validacion;
- mensajes de exito y error;
- textos de botones.

No permitido:

- `label="Nombre"`
- `helperText="Campo requerido"`
- `alert("Error guardando")`

Debe preferirse:

```jsx
label={t("modulo.form.fields.name")}
helperText={errors.nombre}
```

#### Layout y usabilidad

Reglas visuales para formularios:

- preferir `fullWidth`;
- usar `maxWidth` consistente en `Dialog`;
- en mobile, mover campos a nuevas filas antes de saturar una linea;
- agrupar campos relacionados si el formulario es largo;
- no mezclar demasiada logica visual en helpers externos solo para estilos;
- si el campo requiere busqueda local, evaluar `Autocomplete` o `SearchableSelect` segun el patron del modulo.

#### Campos dependientes y catalogos

Si el formulario usa selects encadenados o catalogos:

- limpiar hijos cuando cambia el campo padre;
- no dejar seleccionado un valor inconsistente;
- no recargar catalogos completos si solo hace falta una cascada;
- reutilizar loaders y endpoints autorizados del modulo.

#### Checklist rapido de formularios

Antes de cerrar un cambio en un formulario, validar:

- abre en create y edit correctamente;
- precarga datos en edit;
- limpia errores al reabrir;
- no carga catalogos con el modal cerrado;
- usa i18n en todos los textos visibles;
- muestra errores por campo;
- bloquea el submit mientras guarda;
- refresca la grilla o listado al guardar.

## 8. Guia tecnica de internacionalizacion

### 8.1 Donde se configura

Archivo central:

`src/i18n.js`

Alli se importan y registran todos los JSON por idioma y por dominio.

### 8.2 Como agregar un texto nuevo

Pasos obligatorios:

1. identificar el dominio correcto:
   - `common.json`
   - `usuario.json`
   - `almacen.json`
   - `empresa.json`
   - `empresaRol.json`
   - `kardex.json`
   - etc.
2. agregar la nueva clave en:
   - `src/locales/es/...`
   - `src/locales/en/...`
3. usar `useTranslation()` en el componente.
4. resolver el texto con `t("dominio.ruta.clave")`.

Ejemplo:

```jsx
import { useTranslation } from "react-i18next";

const { t } = useTranslation();

<Button>{t("common.actions.save")}</Button>
```

### 8.3 Reglas de i18n

- no dejar textos visibles hardcoded en JSX;
- no dejar mensajes de validacion fijos en componentes;
- no dejar nombres de estado visibles quemados;
- cuando una logica depende de textos visibles o matchers de estado, llevarla tambien a traducciones si aplica;
- mantener sincronizados `es` y `en`.

### 8.4 Cuando modificar `src/i18n.js`

Solo modificarlo si:

- se agrega un nuevo archivo JSON de traduccion;
- se incorpora un nuevo dominio funcional que necesita registrarse;
- cambia la configuracion general de idiomas soportados.

Si solo agregas nuevas claves a un dominio ya existente, no hace falta tocar `src/i18n.js`.

## 9. Capa API y llamadas HTTP

### 9.1 Cliente recomendado

Usar:

`src/components/axiosConfig.js`

No crear clientes axios paralelos sin justificacion fuerte.

### 9.2 Comportamiento del cliente actual

El cliente:

- usa `VITE_BACKEND_URI + "/api"` como base;
- agrega `Accept-Language`;
- agrega `Authorization: Bearer <token>` si hay token;
- permite `skipAuth` para llamadas sin autenticacion.

### 9.3 Reglas para nuevas llamadas

- preferir siempre el cliente compartido;
- respetar el contrato real de backend;
- si backend ya entrega campos planos suficientes, no reconstruir relaciones localmente;
- anticipar validaciones confusas del backend con validaciones claras en frontend cuando mejore UX;
- evitar cargas masivas de catalogos cuando solo se necesita una cascada o consulta puntual.

## 10. Encabezados y documentacion en codigo

Cada archivo de codigo editado debe mantener encabezado con:

- nombre del archivo;
- descripcion;
- tabla de control de cambios.

Reglas obligatorias:

- autor registrado: `Cesar Medina`;
- version a mantener: `0.4.0` en encabezados del proyecto;
- no registrar `OpenAI` como autor;
- registrar cada cambio funcional o tecnico relevante en la tabla.

Ejemplo de estructura:

```txt
/*=============================================================================
 Nombre del archivo : MiComponente.jsx
 Descripcion        : Describe la responsabilidad principal del archivo.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-11-25 | 0.4.0   | Cesar Medina         | Ajuste de ejemplo.          |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/
```

## 11. Reglas de repositorio, ramas y Git

### 11.1 Rama base

- toda rama de trabajo debe partir desde `origin/develop`;
- antes de crear la rama, `develop` local debe quedar alineada exactamente con remoto;
- no se deben crear ramas nuevas desde estados locales desactualizados;
- evitar merges locales innecesarios al preparar una rama nueva.

### 11.2 Convencion de nombres de ramas

Formato obligatorio:

```txt
tipo/numeroTaiga-historiaDeUsuario-descripcion
```

Donde:

- `tipo` debe ser `feature` o `fix`;
- `numeroTaiga` corresponde al identificador o referencia en Taiga;
- `historiaDeUsuario` debe identificar la historia de usuario asociada;
- `descripcion` debe resumir el ajuste funcional o tecnico.

Estructura solicitada para el proyecto:

```txt
tipoDeAjuste(numero feature o fix)/referenciaTaiga-historiaDeUsuario-descripcion
```

Ejemplo:

```txt
feature/274-hu-038-sedes-categoria-actividad
fix/321-hu-037-usuarios-filtro-estado
```

Reglas:

- usar nombres cortos pero entendibles;
- separar segmentos con guion medio;
- no usar espacios;
- no crear nombres ambiguos como `feature/cambios-varios`.

### 11.3 Commits

- los commits deben ser claros, acotados y coherentes con el ajuste realizado;
- preferir commits atomicos;
- el mensaje debe explicar el cambio real y no ser generico;
- no mezclar limpieza, refactor y funcionalidad nueva en un mismo commit si pueden separarse;
- no incluir archivos temporales, pruebas descartadas o residuos de experimentacion.

Ejemplos aceptables:

```txt
fix(sede): ajustar cascada geográfica en filtros
refactor(usuario): retirar modulos legacy de roles
feat(kardex): bloquear pedidos inactivos en entrada
```

### 11.4 Push

- nunca hacer `push` al repositorio remoto hasta que el usuario lo pida explicitamente;
- se puede preparar la rama, los commits y el estado local, pero el `push` requiere autorizacion del usuario;
- tampoco debe crearse PR si antes no existe instruccion del usuario para hacerlo.

### 11.5 Pull request

Cuando se cree un PR:

- debe usar una rama basada correctamente en `origin/develop`;
- debe llevar titulo y descripcion claros;
- deben asignarse como reviewers:
  - QA;
  - el lider del proyecto.

### 11.6 Verificaciones utiles en Git

- si Git reporta cambios despues de un commit, revisar primero si son diferencias de finales de linea `LF`/`CRLF`;
- antes de cambiar de rama, confirmar que no existan cambios sin guardar que puedan contaminar otra tarea;
- si la rama local quedo mal creada, recrearla desde `origin/develop` en lugar de arrastrar historial incorrecto.

## 12. Validacion y pruebas antes de cerrar cambios

### 12.1 Flujo minimo

1. reiniciar frontend;
2. probar la vista afectada;
3. validar responsive basico;
4. validar que no haya peticiones redundantes;
5. correr `npm run build`;
6. si aplica, correr pruebas unitarias o e2e relacionadas.

### 12.2 Regla importante

Antes de una prueba manual, tumbar y volver a levantar la aplicacion.

Si `vite dev` falla o se comporta de forma inestable por el problema conocido de hooks/HMR, usar:

```bash
npm run build
npm run preview
```

## 13. Guia practica para crear o actualizar un modulo

### 13.1 Si el modulo ya existe

- revisar si ya usa `SectionHeader`, `GridActionBar`, `AppDataGrid` y `CrudFilterModal`;
- mantener la convencion existente;
- no mezclar refactor completo con el cambio funcional si no fue solicitado;
- mover solo lo necesario hacia el patron comun si el cambio lo requiere.

### 13.2 Si es un modulo nuevo

Crear o adaptar:

1. archivo principal del modulo;
2. formulario modal si aplica;
3. columnas para `AppDataGrid`;
4. claves de traduccion en `es` y `en`;
5. wiring de menu o navegacion si corresponde;
6. encabezados con control de cambios.

Checklist tecnico:

- usa `axiosConfig.js`;
- usa `useTranslation()`;
- no deja textos visibles hardcoded;
- usa componentes compartidos;
- registra nuevas traducciones en archivos correctos;
- evita llamadas innecesarias;
- valida create, edit y delete si aplican.

## 14. Casos en los que una IA debe tener especial cuidado

- No asumir que todos los modulos estan totalmente migrados al patron compartido.
- No introducir helpers nuevos solo para estilos si el estilo puede vivir en el componente.
- No tocar autenticacion o persistencia de tokens salvo que sea el requerimiento.
- No reintroducir modulos legacy eliminados.
- No crear nuevos textos hardcoded aunque el archivo viejo ya los tenga.
- No agregar o quitar rutas, modulos o loaders sin revisar primero `Navigator2.jsx`, `App.jsx` y los JSON de traduccion.

## 15. Recomendacion de implementacion visual

Todo cambio visual relevante deberia apoyarse en una guia de estilo y mockups en Figma. La referencia de Figma debe cubrir:

- paleta;
- tipografia;
- botones;
- campos;
- grillas;
- modales;
- espaciados;
- estados visuales.

La implementacion en codigo debe transformar esas decisiones de Figma en componentes reutilizables cuando el patron vaya a repetirse.

## 16. Checklist final para una IA antes de entregar cambios

- lei `package.json`, `App.jsx`, `i18n.js` y `axiosConfig.js`;
- use componentes compartidos donde correspondia;
- agregue traducciones en `es` y `en`;
- no deje textos hardcoded;
- actualice encabezados y tabla de cambios;
- valide build;
- revise que no se disparen requests redundantes;
- mantuve el alcance solicitado sin ampliar logica innecesaria.
