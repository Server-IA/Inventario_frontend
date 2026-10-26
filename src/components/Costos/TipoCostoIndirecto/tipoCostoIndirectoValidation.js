/*=============================================================================
 Nombre del archivo : tipoCostoIndirectoValidation.js
 Descripcion        : Funciones de validación, mapeo de filtros y parseo de errores
                      para el módulo de Tipos de Costos Indirectos.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-10-16 | 0.4.0   | Jeisson Sanchez      | Creación de utilidades de validación y DTOs para HU-044. |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/

export const filtrosVacios = {
  nombre: "",
  estadoId: "",
};

export const emptyForm = {
  nombre: "",
  descripcion: "",
};

/**
 * Convierte el estado local de filtros en parámetros query limpios para GET /v1/tipos-costos-indirectos
 */
export const toParams = (filtros = {}) => {
  const params = {};
  if (filtros.nombre && filtros.nombre.trim()) {
    params.nombre = filtros.nombre.trim();
  }
  if (filtros.estadoId !== "" && filtros.estadoId !== null && filtros.estadoId !== undefined) {
    params.estadoId = Number(filtros.estadoId);
  }
  return params;
};

/**
 * Valida síncronamente los campos del formulario
 */
export const validateTipoCostoIndirecto = (form = {}) => {
  const errors = {};
  const nombre = (form.nombre || "").trim();

  if (!nombre) {
    errors.nombre = "nombreRequired";
  } else if (nombre.length > 100) {
    errors.nombre = "nombreMaxLength";
  }

  if (form.descripcion && form.descripcion.length > 2048) {
    errors.descripcion = "descripcionMaxLength";
  }

  return errors;
};

/**
 * Construye el payload para registro o actualización
 */
export const buildPayload = (form = {}) => ({
  nombre: (form.nombre || "").trim(),
  descripcion: form.descripcion ? form.descripcion.trim() : "",
});

/**
 * Devuelve la clave i18n del mensaje genérico asociado a un estado HTTP conocido.
 */
export const getServerErrorMessageKey = (status) => {
  if (status === 403) return "forbidden";
  if (status === 404) return "notFound";
  if (status === 409) return "conflict";
  return null;
};

/**
 * Parsea respuestas de error HTTP (400, 403, 404, 409) del backend de Spring Boot.
 * Los textos visibles se resuelven en el componente consumidor para respetar i18n.
 */
export const parseServerError = (error) => {
  const data = error?.response?.data;
  const status = error?.response?.status;
  const fieldErrors = {};
  const detail = data?.detail || data?.message || data?.mensaje || "";

  if (data?.invalid_params && Array.isArray(data.invalid_params)) {
    data.invalid_params.forEach((item) => {
      if (item.name) {
        fieldErrors[item.name] = item.reason || item.message || "invalidValue";
      }
    });
  }

  return {
    fieldErrors,
    detail,
    status,
    messageKey: getServerErrorMessageKey(status),
  };
};
