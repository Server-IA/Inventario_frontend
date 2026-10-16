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
 * Parsea respuestas de error HTTP (400, 403, 404, 409) del backend de Spring Boot
 */
export const parseServerError = (error) => {
  const data = error?.response?.data;
  const status = error?.response?.status;

  const fieldErrors = {};
  let detail = data?.detail || data?.message || data?.mensaje || "";

  if (data?.invalid_params && Array.isArray(data.invalid_params)) {
    data.invalid_params.forEach((item) => {
      if (item.name) {
        fieldErrors[item.name] = item.reason || item.message || "Valor inválido";
      }
    });
  }

  if (status === 409) {
    if (!detail) {
      detail = "Ya existe un tipo de costo indirecto con ese nombre para la empresa o tiene costos asociados.";
    }
  } else if (status === 403) {
    detail = "No tienes permisos para realizar esta acción o el registro no pertenece a tu empresa.";
  } else if (status === 404) {
    detail = "El tipo de costo indirecto no fue encontrado.";
  }

  return { fieldErrors, detail, status };
};
