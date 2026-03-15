import React from "react";
import {
    GridToolbarContainer,
    GridToolbarColumnsButton,
    GridToolbarFilterButton,
    GridToolbarDensitySelector,
    GridToolbarQuickFilter,
} from "@mui/x-data-grid";
import { Button, TextField, MenuItem, Box } from "@mui/material";
import RestartAltIcon from "@mui/icons-material/RestartAlt";

/**
 * @description Toolbar personalizada para GridKardex con controles de filtro
 */
export function KardexToolbar({
    onResetColumns,
    filters,
    setFilters,
    tiposMovimiento = [],
}) {
    return (
        <GridToolbarContainer
            sx={{
                p: 1,
                gap: 1,
                justifyContent: "space-between",
                flexWrap: "wrap",
            }}
        >
            <Box sx={{ display: "flex", gap: 2, alignItems: "center", flexWrap: "wrap" }}>
                <GridToolbarColumnsButton />
                <GridToolbarFilterButton />
                <GridToolbarDensitySelector />

                {/* Filtro por fecha desde */}
                <TextField
                    size="small"
                    label="Fecha desde"
                    type="date"
                    InputLabelProps={{ shrink: true }}
                    value={filters.fechaDesde}
                    onChange={(e) =>
                        setFilters((prev) => ({
                            ...prev,
                            fechaDesde: e.target.value,
                        }))
                    }
                    sx={{ width: 150 }}
                />

                {/* Filtro por fecha hasta */}
                <TextField
                    size="small"
                    label="Fecha hasta"
                    type="date"
                    InputLabelProps={{ shrink: true }}
                    value={filters.fechaHasta}
                    onChange={(e) =>
                        setFilters((prev) => ({
                            ...prev,
                            fechaHasta: e.target.value,
                        }))
                    }
                    sx={{ width: 150 }}
                />

                {/* Filtro por tipo de movimiento */}
                <TextField
                    size="small"
                    select
                    label="Tipo movimiento"
                    value={filters.tipoMovimientoId}
                    onChange={(e) =>
                        setFilters((prev) => ({
                            ...prev,
                            tipoMovimientoId: e.target.value,
                        }))
                    }
                    sx={{ width: 160 }}
                >
                    <MenuItem value="">Todos</MenuItem>
                    {tiposMovimiento.map((t) => (
                        <MenuItem key={t.id} value={t.id}>
                            {t.name || t.nombre}
                        </MenuItem>
                    ))}
                </TextField>

                {/* Filtro por estado */}
                <TextField
                    size="small"
                    select
                    label="Estado"
                    value={filters.estadoId}
                    onChange={(e) =>
                        setFilters((prev) => ({
                            ...prev,
                            estadoId: e.target.value,
                        }))
                    }
                    sx={{ width: 130 }}
                >
                    <MenuItem value="">Todos</MenuItem>
                    <MenuItem value="1">Activo</MenuItem>
                    <MenuItem value="0">Inactivo</MenuItem>
                </TextField>

                {/* Botón limpiar filtros */}
                <Button
                    size="small"
                    variant="outlined"
                    onClick={() =>
                        setFilters({
                            fechaDesde: "",
                            fechaHasta: "",
                            tipoMovimientoId: "",
                            estadoId: "",
                        })
                    }
                >
                    Limpiar
                </Button>
            </Box>

            <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
                <GridToolbarQuickFilter debounceMs={300} />
                <Button
                    variant="outlined"
                    size="small"
                    startIcon={<RestartAltIcon />}
                    onClick={onResetColumns}
                >
                    Restablecer columnas
                </Button>
            </Box>
        </GridToolbarContainer>
    );
}
