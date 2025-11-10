// src/components/Produccion/GridProduccion.jsx
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

const LS_KEY = "gridProduccion:columnVisibility:v1";

/* ---------- Toolbar personalizada ---------- */
function ProduccionToolbar({ onResetColumns }) {
  return (
    <GridToolbarContainer sx={{ p: 1, gap: 1, justifyContent: "space-between" }}>
      <div>
        <GridToolbarColumnsButton />
        <GridToolbarFilterButton />
        <GridToolbarDensitySelector />
      </div>
      <GridToolbarQuickFilter debounceMs={300} />
      <button
        type="button"
        onClick={onResetColumns}
        style={{
          border: "1px solid rgba(0,0,0,.2)",
          background: "transparent",
          padding: "4px 8px",
          borderRadius: 6,
          cursor: "pointer",
        }}
        title="Restablecer columnas"
      >
        Restablecer columnas
      </button>
    </GridToolbarContainer>
  );
}

export default function GridProduccion({
  // Datos
  producciones = [],
  loading = false,

  // Selección
  selectedRow = null,
  setSelectedRow,

  // --- Server-side (estilo A: MUI) ---
  paginationModel,        // { page, pageSize } o { page, size }
  setPaginationModel,     // (model) => void
  rowCount,               // total

  // --- Server-side (estilo B: legacy) ---
  page = 0,
  rowsPerPage = 5,
  totalElements = 0,
  onPageChange,           // (event, nextPage) => void
  onRowsPerPageChange,    // (nextSize) => void
}) {
  /* ---------- Helpers ---------- */
  const safeDate = (v) => {
    if (!v) return "";
    const d = new Date(v);
    return Number.isNaN(d.getTime())
      ? String(v).substring(0, 10)
      : d.toISOString().substring(0, 10);
  };

  /* ---------- Columnas (hideable para selector) ---------- */
  const columns = useMemo(() => ([
    { field: "id", headerName: "ID", width: 90, hideable: true },
    {
      field: "fechaInicio",
      headerName: "Fecha Inicio",
      flex: 1,
      minWidth: 150,
      valueGetter: (p) => safeDate(p?.row?.fechaInicio),
      hideable: true,
    },
    {
      field: "fechaFinal",
      headerName: "Fecha Final",
      flex: 1,
      minWidth: 150,
      valueGetter: (p) => safeDate(p?.row?.fechaFinal),
      hideable: true,
    },
    { field: "nombre", headerName: "Nombre", flex: 1, minWidth: 180, hideable: true },
    {
      field: "tipoProduccionNombre",
      headerName: "Tipo Producción",
      flex: 1,
      minWidth: 170,
      valueGetter: (p) =>
        p?.row?.tipoProduccionNombre ??
        p?.row?.tipoProduccion?.nombre ??
        p?.row?.tipoProduccion?.name ??
        String(p?.row?.tipoProduccionId ?? ""),
      hideable: true,
    },
    { field: "descripcion", headerName: "Descripción", flex: 1.4, minWidth: 220, hideable: true },
    {
      field: "espacioNombre",
      headerName: "Espacio",
      flex: 1,
      minWidth: 150,
      valueGetter: (p) =>
        p?.row?.espacioNombre ??
        p?.row?.espacio?.nombre ??
        p?.row?.espacio?.name ??
        String(p?.row?.espacioId ?? ""),
      hideable: true,
    },
    {
      field: "subSeccionNombre",
      headerName: "Subsección",
      flex: 1,
      minWidth: 170,
      valueGetter: (p) =>
        p?.row?.subSeccionNombre ??
        p?.row?.subseccionNombre ??
        p?.row?.subSeccion?.nombre ??
        p?.row?.subSeccion?.name ??
        String(p?.row?.subSeccionId ?? p?.row?.subseccionId ?? ""),
      hideable: true,
    },
    {
      field: "estadoNombre",
      headerName: "Estado",
      flex: 0.8,
      minWidth: 120,
      valueGetter: (p) =>
        p?.row?.estadoNombre ??
        p?.row?.estado?.nombre ??
        p?.row?.estado?.name ??
        (String(p?.row?.estadoId) === "1" ? "Activo" : "Inactivo"),
      hideable: true,
    },
  ]), []);

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

  /* ---------- ¿Server con estilo A o B? ---------- */
  const hasStyleA = Boolean(paginationModel && setPaginationModel && typeof rowCount === "number");
  const hasStyleB = Boolean(typeof totalElements === "number" && (onPageChange || onRowsPerPageChange));

  return (
    <div style={{ width: "100%", height: 500 }}>
      <DataGrid
        rows={Array.isArray(producciones) ? producciones : []}
        columns={columns}
        getRowId={(row) => row.id}
        loading={loading}
        disableRowSelectionOnClick
        disableColumnMenu
        /* -------- selección -------- */
        onRowClick={(params) => setSelectedRow?.(params.row)}
        rowSelectionModel={selectedRow?.id ? [selectedRow.id] : []}
        /* -------- columnas + toolbar -------- */
        columnVisibilityModel={columnVisibilityModel}
        onColumnVisibilityModelChange={handleVisibilityChange}
        slots={{ toolbar: ProduccionToolbar }}
        slotProps={{ toolbar: { onResetColumns: handleResetColumns } }}

        /* ---- Server-side estilo A (MUI) ---- */
        {...(hasStyleA
          ? {
              paginationMode: "server",
              rowCount,
              paginationModel: {
                page: paginationModel.page ?? 0,
                pageSize: paginationModel.pageSize ?? paginationModel.size ?? 10,
              },
              onPaginationModelChange: (model) => {
                const next = {
                  page: model.page ?? 0,
                  size: model.pageSize ?? model.size ?? 10,
                };
                setPaginationModel?.(next);
              },
              pageSizeOptions: [5, 10, 15, 20, 50],
            }
          : {})}

        /* ---- Server-side estilo B (legacy) ---- */
        {...(!hasStyleA && hasStyleB
          ? {
              paginationMode: "server",
              rowCount: totalElements ?? 0,
              paginationModel: { page, pageSize: rowsPerPage },
              onPaginationModelChange: (model) => {
                const nextPage = model?.page ?? 0;
                const nextSize = model?.pageSize ?? rowsPerPage ?? 10;
                if (nextSize !== rowsPerPage) {
                  onRowsPerPageChange?.(nextSize);
                } else if (nextPage !== page) {
                  onPageChange?.(null, nextPage);
                }
              },
              pageSizeOptions: [5, 10, 15, 20, 50],
            }
          : {})}

        /* ---- Fallback cliente ---- */
        {...(!hasStyleA && !hasStyleB
          ? {
              paginationMode: "client",
              pageSizeOptions: [5, 10, 15, 20, 50],
              initialState: {
                pagination: { paginationModel: { page: 0, pageSize: 5 } },
              },
            }
          : {})}
      />
    </div>
  );
}

GridProduccion.propTypes = {
  producciones: PropTypes.array,
  loading: PropTypes.bool,

  selectedRow: PropTypes.object,
  setSelectedRow: PropTypes.func,

  // Estilo A (MUI)
  paginationModel: PropTypes.shape({
    page: PropTypes.number,
    pageSize: PropTypes.number,
    size: PropTypes.number,
  }),
  setPaginationModel: PropTypes.func,
  rowCount: PropTypes.number,

  // Estilo B (legacy)
  page: PropTypes.number,
  rowsPerPage: PropTypes.number,
  totalElements: PropTypes.number,
  onPageChange: PropTypes.func,
  onRowsPerPageChange: PropTypes.func,
};
