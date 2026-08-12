/**
 * @description Formateos y transformaciones para kardex
 */

/**
 * @description Convierte datetime a string formateado para display
 * @param {string|Date} val Valor de fecha
 * @returns {string} Fecha formateada o ""
 */
export const safeDateTime = (val) =>
    val ? new Date(val).toLocaleString("es-ES") : "";

/**
 * @description Extrae nombres de objetos anidados con fallback
 * @param {object} item Objeto con propiedades nombre/name
 * @param {string} fallback Valor por defecto
 * @returns {string}
 */
export const extractName = (item, fallback = "") => {
    if (!item) return fallback;
    return item?.nombreComercial || item?.nombre || item?.name || fallback;
};

/**
 * @description Crea mapa lookup de ID -> nombre para array de objetos
 * @param {array} array Array de objetos
 * @param {function} nameExtractor Función para extraer nombre
 * @returns {object} Mapa { id: nombre }
 */
export const createLookupMap = (array = [], nameExtractor = extractName) => {
    const map = {};
    for (const item of array) {
        if (item?.id) {
            map[String(item.id)] = nameExtractor(item, `#${item.id}`);
        }
    }
    return map;
};

/**
 * @description Convierte estado numérico a texto
 * @param {1|0|"1"|"0"|boolean} estado ID de estado
 * @returns {string} "Activo" o "Inactivo"
 */
export const formatEstado = (estado) => {
    if (estado === 1 || estado === "1") return "Activo";
    if ([0, "0", 2, "2"].includes(estado)) return "Inactivo";
    return String(estado ?? "");
};

/**
 * @description Array normalizado de items (trata diferentes estructuras API)
 * @param {*} data Respuesta API
 * @returns {array}
 */
export const toArray = (d) =>
    Array.isArray(d)
        ? d
        : d?.content ??
        d?.items ??
        d?.data ??
        d?.results ??
        [];

/**
 * @description Extrae información de paginación de respuesta API
 * @param {object} res Respuesta de axios
 * @returns {object} { rows, total }
 */
export const normalizePageResponse = (res) => {
    const data = res?.data ?? {};
    const rows = toArray(data);
    const total =
        data?.totalElements ??
        data?.page?.totalElements ??
        data?.total ??
        rows.length;
    return { rows, total };
};

export const resolveKardexId = (row) =>
    row?.id ??
    row?.kardexId ??
    row?.karId ??
    row?.requestedId ??
    null;

export const resolveArticuloKardexId = (row) =>
    row?.id ??
    row?.articuloKardexId ??
    row?.kardexItemId ??
    null;
