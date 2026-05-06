import React, { useState } from "react";
import MessageSnackBar from "../MessageSnackBar";
import GridKardex from "./GridKardex";
import ReKardex from "../RKardex/Rkardex";
import {
  Box,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  MenuItem,
  Button,
} from "@mui/material";
import { useKardexAuth } from "./hooks/useKardexAuth";
import { useKardexData } from "./hooks/useKardexData";
import { useKardexFilters } from "./hooks/useKardexFilters";
import { KardexFormsContainer } from "./KardexFormsContainer";
import SectionHeader from "../common/SectionHeader";
import { DEFAULT_FILTERS } from "./constants/kardexConstants";

export default function Kardex() {
  const { isAdmin } = useKardexAuth();
  const { kardexesRaw, catalogs, reloadData, loading } = useKardexData();

  const [kardexPage, setKardexPage] = useState({ page: 0, size: 10 });
  const { filters, setFilters, paginatedRows, totalFiltered } = useKardexFilters(
    kardexesRaw,
    kardexPage,
    catalogs.tiposMovimiento
  );

  const [selectedRow, setSelectedRow] = useState(null);
  const [searchDialogOpen, setSearchDialogOpen] = useState(false);
  const [filtersOpen, setFiltersOpen] = useState(false);
  const [message, setMessage] = useState({ open: false, severity: "success", text: "" });

  const hasActiveFilters = Boolean(
    filters.fechaDesde || filters.fechaHasta || filters.tipoMovimientoId || filters.estadoId
  );

  const handleClearFilters = () => {
    setFilters(DEFAULT_FILTERS);
    setKardexPage((prev) => ({ ...prev, page: 0 }));
  };

  return (
    <Box sx={{ p: 2 }}>
      <SectionHeader title="Gestión de Kardex" />

      <KardexFormsContainer
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
        reloadData={reloadData}
        setMessage={setMessage}
        onOpenReportes={() => setSearchDialogOpen(true)}
        onOpenFilters={() => setFiltersOpen(true)}
        onClearFilters={handleClearFilters}
        hasActiveFilters={hasActiveFilters}
      />

      <GridKardex
        kardexes={paginatedRows}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
        loading={loading}
        rowCount={totalFiltered}
        paginationModel={kardexPage}
        setPaginationModel={setKardexPage}
        isAdmin={isAdmin}
      />

      <Dialog open={filtersOpen} onClose={() => setFiltersOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Filtros de Kardex</DialogTitle>
        <DialogContent sx={{ pt: 3.5 }}>
          <Box sx={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 2, mt: 1 }}>
            <TextField
              size="small"
              label="Fecha desde"
              type="date"
              InputLabelProps={{ shrink: true }}
              value={filters.fechaDesde}
              onChange={(e) => setFilters((prev) => ({ ...prev, fechaDesde: e.target.value }))}
            />
            <TextField
              size="small"
              label="Fecha hasta"
              type="date"
              InputLabelProps={{ shrink: true }}
              value={filters.fechaHasta}
              onChange={(e) => setFilters((prev) => ({ ...prev, fechaHasta: e.target.value }))}
            />
            <TextField
              size="small"
              select
              label="Tipo movimiento"
              value={filters.tipoMovimientoId}
              onChange={(e) => setFilters((prev) => ({ ...prev, tipoMovimientoId: e.target.value }))}
            >
              <MenuItem value="">Todos</MenuItem>
              {(catalogs.tiposMovimiento || []).map((t) => (
                <MenuItem key={t.id} value={t.id}>
                  {t.name || t.nombre}
                </MenuItem>
              ))}
            </TextField>
            <TextField
              size="small"
              select
              label="Estado"
              value={filters.estadoId}
              onChange={(e) => setFilters((prev) => ({ ...prev, estadoId: e.target.value }))}
            >
              <MenuItem value="">Todos</MenuItem>
              <MenuItem value="1">Activo</MenuItem>
              <MenuItem value="0">Inactivo</MenuItem>
            </TextField>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClearFilters}>Limpiar</Button>
          <Button
            variant="contained"
            onClick={() => {
              setKardexPage((prev) => ({ ...prev, page: 0 }));
              setFiltersOpen(false);
            }}
          >
            Aplicar
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog open={searchDialogOpen} onClose={() => setSearchDialogOpen(false)} fullWidth maxWidth="lg">
        <ReKardex setOpen={setSearchDialogOpen} />
      </Dialog>

      <MessageSnackBar message={message} setMessage={setMessage} />
    </Box>
  );
}
