import { useState, useMemo } from "react";
import { filterByDateRange } from "../utils/dateUtils";
import { DEFAULT_FILTERS } from "../constants/kardexConstants";

/**
 * @description Hook para gestionar filtros de kardex
 * @param {array} kardexesRaw Datos sin filtrar
 * @param {object} paginationModel { page, size }
 * @returns {object} { filters, setFilters, filteredKardexes, filtrationInfo }
 */
export const useKardexFilters = (kardexesRaw = [], paginationModel = {}) => {
    const [filters, setFilters] = useState(DEFAULT_FILTERS);

    const filteredKardexes = useMemo(() => {
        let filtered = kardexesRaw;

        // Filtro por rango de fechas
        filtered = filterByDateRange(filtered, filters.fechaDesde, filters.fechaHasta);

        // Filtro por tipo de movimiento
        if (filters.tipoMovimientoId) {
            const tipoId = Number(filters.tipoMovimientoId);
            filtered = filtered.filter((k) => k.tipoMovimientoId === tipoId);
        }

        // Filtro por estado
        if (filters.estadoId !== "") {
            const estId = Number(filters.estadoId);
            filtered = filtered.filter((k) => k.estadoId === estId);
        }

        return filtered;
    }, [kardexesRaw, filters]);

    // Aplicar paginación
    const { page = 0, size = 10 } = paginationModel;
    const startIdx = page * size;
    const paginatedRows = filteredKardexes.slice(startIdx, startIdx + size);

    const cleanFilters = () => {
        setFilters(DEFAULT_FILTERS);
    };

    const hasActiveFilters = Object.values(filters).some((v) => v !== "");

    return {
        filters,
        setFilters,
        cleanFilters,
        filteredKardexes,
        paginatedRows,
        totalFiltered: filteredKardexes.length,
        hasActiveFilters,
    };
};
