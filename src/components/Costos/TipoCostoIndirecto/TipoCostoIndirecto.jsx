/*=============================================================================
 Nombre del archivo : TipoCostoIndirecto.jsx
 Descripcion        : Módulo de Gestión de Tipos de Costos Indirectos (HU-044).
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-10-16 | 0.4.0   | Jeisson Sanchez      | Creación del contenedor de listado paginado y filtros para HU-044.2. |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/
import React, { useEffect, useMemo, useState } from "react";
import { Box, Chip, Stack } from "@mui/material";
import { useTranslation } from "react-i18next";
import axios from "../../axiosConfig";
import SectionHeader from "../../common/SectionHeader.jsx";
import GridActionBar from "../../common/GridActionBar.jsx";
import AppDataGrid from "../../common/AppDataGrid.jsx";
import MessageSnackBar from "../../MessageSnackBar.jsx";
import FiltroTipoCostoIndirecto from "./FiltroTipoCostoIndirecto.jsx";
import { filtrosVacios, toParams, parseServerError } from "./tipoCostoIndirectoValidation";

export default function TipoCostoIndirecto() {
  const { t } = useTranslation();
  const [selectedRow, setSelectedRow] = useState(null);
  const [filtersOpen, setFiltersOpen] = useState(false);
  const [filtros, setFiltros] = useState(filtrosVacios);
  const [paginationModel, setPaginationModel] = useState({ page: 0, pageSize: 10 });
  const [rows, setRows] = useState([]);
  const [rowCount, setRowCount] = useState(0);
  const [loading, setLoading] = useState(false);
  const [refreshKey] = useState(0);
  const [message, setMessage] = useState({ open: false, severity: "success", text: "" });

  const queryParams = useMemo(() => toParams(filtros), [filtros]);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);

    axios
      .get("/v1/tipos-costos-indirectos", {
        params: {
          page: paginationModel.page,
          size: paginationModel.pageSize,
          sortBy: "id,asc",
          ...queryParams,
        },
      })
      .then(({ data }) => {
        if (cancelled) return;
        const items = Array.isArray(data?.data) ? data.data : [];
        const total = data?.header?.totalElements ?? items.length;
        setRows(items);
        setRowCount(total);
      })
      .catch((error) => {
        if (cancelled) return;
        setRows([]);
        setRowCount(0);
        const { detail } = parseServerError(error);
        setMessage({
          open: true,
          severity: "error",
          text: detail || t("costos.tipoCostoIndirecto.messages.listError", "No se pudo cargar el listado de tipos de costos indirectos."),
        });
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, [paginationModel, queryParams, refreshKey, t]);

  const applyFilters = (nuevosFiltros) => {
    setFiltros(nuevosFiltros);
    setPaginationModel((prev) => ({ ...prev, page: 0 }));
    setFiltersOpen(false);
  };

  const handleClearFilters = () => {
    applyFilters(filtrosVacios);
  };

  const columns = useMemo(
    () => [
      {
        field: "nombre",
        headerKey: "costos.tipoCostoIndirecto.columns.nombre",
        headerName: "Nombre",
        flex: 1.2,
        minWidth: 160,
      },
      {
        field: "descripcion",
        headerKey: "costos.tipoCostoIndirecto.columns.descripcion",
        headerName: "Descripción",
        flex: 2,
        minWidth: 220,
        valueGetter: (params) => params.value || "-",
      },
      {
        field: "empresaNombre",
        headerKey: "costos.tipoCostoIndirecto.columns.empresa",
        headerName: "Empresa",
        flex: 1,
        minWidth: 140,
        valueGetter: (params) => params.value || "-",
      },
      {
        field: "estadoNombre",
        headerKey: "costos.tipoCostoIndirecto.columns.estado",
        headerName: "Estado",
        statusChip: true,
        width: 120,
      },
    ],
    []
  );

  const localeText = useMemo(
    () => ({
      noRowsLabel: t("costos.tipoCostoIndirecto.grid.empty", "No hay tipos de costos indirectos para mostrar."),
    }),
    [t]
  );

  // Chips de filtros activos
  const activeChips = useMemo(() => {
    const list = [];
    if (filtros.nombre && filtros.nombre.trim()) {
      list.push({
        key: "nombre",
        label: `${t("costos.tipoCostoIndirecto.filters.nombre", "Nombre")}: ${filtros.nombre.trim()}`,
        onDelete: () => applyFilters({ ...filtros, nombre: "" }),
      });
    }
    if (filtros.estadoId) {
      const estadoTexto =
        String(filtros.estadoId) === "1"
          ? t("common.labels.active", "Activo")
          : t("common.labels.inactive", "Inactivo");
      list.push({
        key: "estadoId",
        label: `${t("costos.tipoCostoIndirecto.filters.estado", "Estado")}: ${estadoTexto}`,
        onDelete: () => applyFilters({ ...filtros, estadoId: "" }),
      });
    }
    return list;
  }, [filtros, t]);

  const hasActiveFilters = Object.keys(queryParams).length > 0;

  return (
    <Box p={2}>
      <SectionHeader titleKey="costos.tipoCostoIndirecto.title" />

      <MessageSnackBar message={message} setMessage={setMessage} />

      <GridActionBar
        onAdd={() => {}}
        onUpdate={() => {}}
        onDelete={() => {}}
        canAdd={true}
        canUpdate={Boolean(selectedRow)}
        canDelete={Boolean(selectedRow)}
        showDelete={true}
        onFilters={() => setFiltersOpen(true)}
        onClearFilters={handleClearFilters}
        hasActiveFilters={hasActiveFilters}
      />

      {activeChips.length > 0 && (
        <Stack direction="row" flexWrap="wrap" useFlexGap spacing={1} sx={{ mb: 2 }}>
          {activeChips.map((chip) => (
            <Chip
              key={chip.key}
              size="small"
              label={chip.label}
              onDelete={chip.onDelete}
              color="primary"
              variant="outlined"
            />
          ))}
        </Stack>
      )}

      <FiltroTipoCostoIndirecto
        open={filtersOpen}
        filtros={filtros}
        onApply={applyFilters}
        onClose={() => setFiltersOpen(false)}
      />

      <AppDataGrid
        columns={columns}
        rows={rows}
        loading={loading}
        rowCount={rowCount}
        paginationModel={paginationModel}
        setPaginationModel={setPaginationModel}
        pageSizeOptions={[5, 10, 20, 50]}
        localeText={localeText}
        containerSx={{ borderRadius: 4 }}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
      />
    </Box>
  );
}
