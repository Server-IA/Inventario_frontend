/**
 * @description Convierte datetime a string formateado para display
 * @param {string|Date} val Valor de fecha
 * @returns {string} Fecha formateada o ""
 */
export const safeDateTime = (val) =>
    val ? new Date(val).toLocaleString("es-ES") : "";

/**
 * @description Convierte string de fecha YYYY-MM-DD a Date
 * @param {string} dateStr Fecha en formato YYYY-MM-DD
 * @returns {Date}
 */
export const parseDate = (dateStr) => new Date(dateStr);

/**
 * @description Filtra kardexes por rango de fechas
 * @param {array} data Array de kardexes
 * @param {string} fechaDesde Fecha inicio (YYYY-MM-DD)
 * @param {string} fechaHasta Fecha fin (YYYY-MM-DD)
 * @returns {array} Kardexes filtrados
 */
export const filterByDateRange = (data, fechaDesde, fechaHasta) => {
    let filtered = data;

    if (fechaDesde) {
        const dateDesde = parseDate(fechaDesde);
        filtered = filtered.filter((k) => new Date(k.fechaHora) >= dateDesde);
    }

    if (fechaHasta) {
        const dateHasta = parseDate(fechaHasta);
        // Considerar toda la fecha hasta (incluir hasta el final del día)
        dateHasta.setHours(23, 59, 59, 999);
        filtered = filtered.filter((k) => new Date(k.fechaHora) <= dateHasta);
    }

    return filtered;
};
