/**
 * @description Constantes y enumeraciones para módulo Kardex
 */

// Roles de usuario
export const ROLES = {
    ADMINISTRADOR_SISTEMA: 1,
    ADMINISTRADOR_EMPRESA: 2,
};

// Tipos de movimiento
export const TIPO_MOV_ENTRADA_COMPRA = 2;

// Estados
export const ESTADOS = {
    ACTIVO: 1,
    INACTIVO: 0,
};

// Valores por defecto de filtro
export const DEFAULT_FILTERS = {
    fechaDesde: "",
    fechaHasta: "",
    tipoMovimientoId: "",
    estadoId: "",
};

// Tamaño de página
export const PAGE_SIZE = 10;
export const MAX_RECORDS = 1000;

// Mensajes
export const MESSAGES = {
    ERROR_LOAD_KARDEX: "Error al cargar kardexes",
    ERROR_LOAD_CATALOGS: "Error al cargar catálogos",
    SUCCESS_CREATE: "Kardex creado correctamente.",
    SUCCESS_UPDATE: "Kardex actualizado correctamente.",
    SUCCESS_DELETE: "Kardex eliminado correctamente.",
    ERROR_DELETE: "Error al eliminar el Kardex.",
};
