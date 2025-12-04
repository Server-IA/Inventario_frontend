// src/components/Presentacionproducto/GridPresentacionproducto.jsx
import React, { useEffect, useMemo, useState } from "react";
import PropTypes from "prop-types";
import {
  DataGrid,
  GridToolbarContainer,
  GridToolbarColumnsButton,
  GridToolbarFilterButton,
  GridToolbarDensitySelector,
  GridToolbarQuickFilter,
} from "@mui/x-data-grid";
import { Box, Chip, Button } from "@mui/material";
import RestartAltIcon from "@mui/icons-material/RestartAlt";

const LS_KEY = "gridPresentacionProducto:columnVisibility:v1";

/* ---------- Toolbar personalizada ---------- */
function PPToolbar({ onResetColumns }) {
  return (
    <GridToolbarContainer sx={{ p: 1, gap: 1, justifyContent: "space-between" }}>
      <div>
        <GridToolbarColumnsButton />
        <GridToolbarFilterButton />
        <GridToolbarDensitySelector />
      </div>
      <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
        <GridToolbarQuickFilter debounceMs={300} />
        <Button
          variant="outlined"
          size="small"
          startIcon={<RestartAltIcon />}
          onClick={onResetColumns}
        >
          Restablecer columnas
        </Button>
      </div>
    </GridToolbarContainer>
  );
}

export default function GridPresentacionproducto({
  // nombres nuevos (preferidos)
  rows,
  onPaginationModelChange,

  // compat con nombres antiguos
  Presentacionproductoes,
  setPaginationModel,

  // selección
  selectedRow,
  setSelectedRow,

  // estado de carga
  loading = false,

  // control opcional (server-side)
  paginationModel,            // { page, pageSize } o { page, size }
  sortModel,
  setSortModel,
  filterModel,
  setFilterModel,
  rowCount = 0,
}) {
  /* ---------- Datos/handlers seguros ---------- */
  const safeRows = Array.isArray(rows)
    ? rows
    : Array.isArray(Presentacionproductoes)
    ? Presentacionproductoes
    : [];

  const handlePaginationChange =
    onPaginationModelChange ??
    (setPaginationModel
      ? (model) => {
          const next = { page: model.page ?? 0, size: model.pageSize ?? model.size ?? 10 };
          setPaginationModel(next);
        }
      : undefined);

  /* ---------- ¿Server o cliente? ---------- */
  const serverPagination = Boolean(paginationModel && handlePaginationChange && typeof rowCount === "number");

  /* ---------- Columnas (todas hideable) ---------- */
  const columns = useMemo(
    () => [
      { field: "id", headerName: "ID", width: 90, type: "number", hideable: true },
      {
        field: "productoNombre",
        headerName: "Producto",
        flex: 1,
        minWidth: 160,
        hideable: true,
        valueGetter: (p) =>
          p?.row?.productoNombre ??
          p?.row?.producto?.nombre ??
          p?.row?.producto?.name ??
          String(p?.row?.productoId ?? ""),
      },
      {
        field: "nombre",
        headerName: "Nombre de la Presentación",
        flex: 1.2,
        minWidth: 200,
        hideable: true,
      },
      {
        field: "unidadNombre",
        headerName: "Unidad",
        flex: 1,
        minWidth: 120,
        hideable: true,
        valueGetter: (p) =>
          p?.row?.unidadNombre ??
          p?.row?.unidad?.nombre ??
          p?.row?.unidad?.name ??
          String(p?.row?.unidadId ?? ""),
      },
      { field: "descripcion", headerName: "Descripción", flex: 1.4, minWidth: 220, hideable: true },
      { field: "cantidad", headerName: "Cantidad Presentacion", type: "number", width: 180, hideable: true },
      {
        field: "marcaNombre",
        headerName: "Marca",
        flex: 1,
        minWidth: 140,
        hideable: true,
        valueGetter: (p) =>
          p?.row?.marcaNombre ??
          p?.row?.marca?.nombre ??
          p?.row?.marca?.name ??
          String(p?.row?.marcaId ?? ""),
      },
      {
        field: "presentacionNombre",
        headerName: "Tipo de Presentación",
        flex: 1.1,
        minWidth: 180,
        hideable: true,
        valueGetter: (p) =>
          p?.row?.presentacionNombre ??
          p?.row?.presentacion?.nombre ??
          p?.row?.presentacion?.name ??
          String(p?.row?.presentacionId ?? ""),
      },
      {
        field: "estadoId",
        headerName: "Estado",
        width: 140,
        hideable: true,
        renderCell: (p) => {
          const activo =
            p?.row?.estado?.nombre === "Activo" ||
            p?.row?.estado?.name === "Activo" ||
            String(p?.row?.estadoId) === "1";
          return (
            <Chip
              size="small"
              label={activo ? "Activo" : "Inactivo"}
              color={activo ? "success" : "default"}
            />
          );
        },
        sortComparator: (v1, v2, cellParams1, cellParams2) =>
          Number(String(cellParams1?.row?.estadoId) === "1") -
          Number(String(cellParams2?.row?.estadoId) === "1"),
      },
    ],
    []
  );

  /* ---------- Persistencia de visibilidad ---------- */
  const [columnVisibilityModel, setColumnVisibilityModel] = useState({});

  useEffect(() => {
    try {
      const saved = JSON.parse(localStorage.getItem(LS_KEY) || "{}");
      if (saved && typeof saved === "object") setColumnVisibilityModel(saved);
    } catch { /* noop */ }
  }, []);

  const handleVisibilityChange = (model) => {
    setColumnVisibilityModel(model);
    try {
      localStorage.setItem(LS_KEY, JSON.stringify(model));
    } catch { /* noop */ }
  };

  const handleResetColumns = () => {
    localStorage.removeItem(LS_KEY);
    setColumnVisibilityModel({});
  };

  return (
    <Box sx={{ width: "100%" }}>
      <DataGrid
        rows={safeRows}
        columns={columns}
        getRowId={(r) => r.id}
        loading={loading}

        // Selección controlada
        onRowClick={(params) => setSelectedRow?.(params.row)}
        rowSelectionModel={selectedRow?.id ? [selectedRow.id] : []}
        disableRowSelectionOnClick

        // Toolbar
        slots={{ toolbar: PPToolbar }}
        slotProps={{ toolbar: { onResetColumns: handleResetColumns } }}

        // Columnas visibles
        columnVisibilityModel={columnVisibilityModel}
        onColumnVisibilityModelChange={handleVisibilityChange}

        // Paginación
        paginationMode={serverPagination ? "server" : "client"}
        {...(serverPagination
          ? {
              rowCount,
              paginationModel: {
                page: paginationModel.page ?? 0,
                pageSize: paginationModel.pageSize ?? paginationModel.size ?? 10,
              },
              onPaginationModelChange: handlePaginationChange,
              pageSizeOptions: [5, 10, 15, 20, 50],
            }
          : {
              pageSizeOptions: [5, 10, 15, 20, 50],
              initialState: { pagination: { paginationModel: { page: 0, pageSize: 5 } } },
            })}

        // Sorting / Filtering: server si hay setters, si no, client
        sortingMode={setSortModel ? "server" : "client"}
        sortModel={sortModel}
        onSortModelChange={setSortModel}
        filterMode={setFilterModel ? "server" : "client"}
        filterModel={filterModel}
        onFilterModelChange={setFilterModel}
        autoHeight
      />
    </Box>
  );
}

GridPresentacionproducto.propTypes = {
  // nuevos
  rows: PropTypes.array,
  onPaginationModelChange: PropTypes.func,

  // compat antiguos
  Presentacionproductoes: PropTypes.array,
  setPaginationModel: PropTypes.func,

  selectedRow: PropTypes.object,
  setSelectedRow: PropTypes.func,
  loading: PropTypes.bool,
  paginationModel: PropTypes.shape({
    page: PropTypes.number,
    pageSize: PropTypes.number,
    size: PropTypes.number
  }),
  sortModel: PropTypes.array,
  setSortModel: PropTypes.func,
  filterModel: PropTypes.object,
  setFilterModel: PropTypes.func,
  rowCount: PropTypes.number,
};
