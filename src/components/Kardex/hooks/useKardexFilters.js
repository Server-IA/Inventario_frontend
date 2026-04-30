import { useState, useMemo } from "react";
import { filterByDateRange } from "../utils/dateUtils";
import { DEFAULT_FILTERS } from "../constants/kardexConstants";

const normalize = (v) =>
    String(v ?? "")
        .trim()
        .toLowerCase()
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "");

export const useKardexFilters = (kardexesRaw = [], paginationModel = {}, tiposMovimiento = []) => {
    const [filters, setFilters] = useState(DEFAULT_FILTERS);

    const tipoById = useMemo(() => {
        const map = {};
        for (const t of tiposMovimiento || []) {
            map[String(t?.id)] = t?.name ?? t?.nombre ?? t?.descripcion ?? "";
        }
        return map;
    }, [tiposMovimiento]);

    const filteredKardexes = useMemo(() => {
        let filtered = Array.isArray(kardexesRaw) ? kardexesRaw : [];

        filtered = filterByDateRange(filtered, filters.fechaDesde, filters.fechaHasta);

        if (filters.tipoMovimientoId) {
            const wantedName = normalize(tipoById[String(filters.tipoMovimientoId)]);
            filtered = filtered.filter((k) => {
                if (k?.tipoMovimientoId != null) {
                    return String(k.tipoMovimientoId) === String(filters.tipoMovimientoId);
                }
                const rowName = normalize(k?.nombreTipoMovimiento ?? k?.tipoMovimiento ?? "");
                return wantedName ? rowName === wantedName : false;
            });
        }

        if (filters.estadoId !== "") {
            const estId = Number(filters.estadoId);
            filtered = filtered.filter((k) => {
                if (k?.estadoId != null) return Number(k.estadoId) === estId;
                const estado = normalize(k?.nombreEstado ?? "");
                if (estId === 1) return estado.includes("activo");
                if (estId === 0) return estado.includes("inactivo");
                return false;
            });
        }

        return filtered;
    }, [kardexesRaw, filters, tipoById]);

    const { page = 0, size = 10 } = paginationModel;
    const startIdx = page * size;
    const paginatedRows = filteredKardexes.slice(startIdx, startIdx + size);

    return {
        filters,
        setFilters,
        filteredKardexes,
        paginatedRows,
        totalFiltered: filteredKardexes.length,
    };
};
