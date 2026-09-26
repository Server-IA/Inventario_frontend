/*=============================================================================
 Nombre del archivo : costoIndirectoValidation.js
 Descripcion        : Validación, payload y lectura de errores del registro de
                      costo indirecto (funciones puras, sin dependencias de UI).
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-09-25 | 0.4.0   | Arekkazu             | Creación del archivo para   |
 |            |         |                      | la HU-045.1.                |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/
/**
 * @module costoIndirectoValidation
 * @description Reglas de cliente que replican las del backend para
 * `POST /v1/costos-indirectos` y utilidades para armar el body y leer errores.
 */

/** Niveles de ubicación en orden jerárquico. */
export const NIVELES = ["sede", "bloque", "espacio", "almacen"];

export const emptyForm = {
  tipo: null,
  sede: null,
  bloque: null,
  espacio: null,
  almacen: null,
  fechaInicio: "",
  fechaFin: "",
  valor: "",
};

/**
 * Valida el formulario y devuelve códigos de error por campo (claves de
 * `costos.costoIndirecto.form.validation.*`). Objeto vacío = válido.
 * @param {Object} values Estado del formulario.
 * @returns {Object<string,string>}
 */
export const validateCostoIndirecto = (values) => {
  const errors = {};
  const { tipo, sede, fechaInicio, fechaFin, valor } = values;

  if (!tipo) errors.tipo = "tipoRequired";
  if (!sede) errors.sede = "sedeRequired";
  if (!fechaInicio) errors.fechaInicio = "fechaInicioRequired";
  if (!fechaFin) errors.fechaFin = "fechaFinRequired";
  // Fechas ISO (yyyy-MM-dd): la comparación de texto equivale a la de fechas.
  else if (fechaInicio && fechaFin < fechaInicio) errors.fechaFin = "fechaFinBeforeStart";

  if (valor === "" || valor == null) errors.valor = "valorRequired";
  else if (!(Number(valor) > 0)) errors.valor = "valorPositive";

  return errors;
};

/**
 * Arma el body del POST. Los niveles opcionales vacíos no se envían.
 * @param {Object} values Estado del formulario ya validado.
 * @returns {Object}
 */
export const buildPayload = ({ tipo, sede, bloque, espacio, almacen, fechaInicio, fechaFin, valor }) => ({
  tipoCostoIndirectoId: tipo.id,
  sedeId: sede.id,
  ...(bloque && { bloqueId: bloque.id }),
  ...(espacio && { espacioId: espacio.id }),
  ...(almacen && { almacenId: almacen.id }),
  fechaInicio,
  fechaFin,
  valor: Number(valor),
});

const SERVER_FIELD = { tipoCostoIndirectoId: "tipo", sedeId: "sede" };

/**
 * Lee un error RFC 9457 del backend: mapa `errors` por campo y `detail`.
 * @param {Object} error Error de axios.
 * @returns {{fieldErrors: Object<string,string>, detail: (string|null)}}
 */
export const parseServerError = (error) => {
  const data = error?.response?.data ?? {};
  const fieldErrors = {};
  Object.entries(data.errors ?? {}).forEach(([campo, mensaje]) => {
    fieldErrors[SERVER_FIELD[campo] ?? campo] = mensaje;
  });
  return { fieldErrors, detail: data.detail ?? null };
};
