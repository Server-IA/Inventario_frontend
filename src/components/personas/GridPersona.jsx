// src/components/Persona/GridPersona.jsx
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
import { Box } from "@mui/material";
import GridBase from "../dashboard/GridBase";
const LS_KEY = "gridPersona:columnVisibility:v1";

/* -------- Toolbar personalizada -------- */
function PersonaToolbar({ onResetColumns }) {
  return (
    <GridToolbarContainer sx={{ p: 1, gap: 1, justifyContent: "space-between" }}>
      <div>
        <GridToolbarColumnsButton />
        <GridToolbarFilterButton />
        <GridToolbarDensitySelector />
      </div>
      <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
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
      </div>
    </GridToolbarContainer>
  );
}

export default function GridPersona({
  // Datos
  personas = [],

  // Selección
  selectedRow = null,
  setSelectedRow,

  // Paginación server-side (opcional)
  paginationModel,        // { page, pageSize } o { page, size }
  setPaginationModel,     // (model) => void
  rowCount,               // total en servidor
  loading = false,
}) {
  const safeDate = (v) => {
    if (!v) return "";
    const d = new Date(v);
    return Number.isNaN(d.getTime())
      ? String(v).substring(0, 10)
      : d.toISOString().substring(0, 10);
  };

  const columns = useMemo(
    () => [
      { field: "id", headerName: "ID", width: 80, hideable: true },
      {
        field: "tipoIdentificacionNombre",
        headerName: "Tipo ID",
        width: 180,
        hideable: true,
        valueGetter: (p) =>
          p?.row?.tipoIdentificacionNombre ??
          p?.row?.tipoIdentificacion?.nombre ??
          p?.row?.tipoIdentificacion?.name ??
          String(p?.row?.tipoIdentificacionId ?? ""),
      },
      { field: "identificacion", headerName: "Identificación", width: 150, hideable: true },
      { field: "nombre", headerName: "Nombre", width: 150, hideable: true },
      { field: "apellido", headerName: "Apellido", width: 150, hideable: true },
      { field: "genero", headerName: "Género", width: 100, hideable: true },
      {
        field: "fechaNacimiento",
        headerName: "Nacimiento",
        width: 140,
        hideable: true,
        valueGetter: (p) => safeDate(p?.row?.fechaNacimiento),
      },
      { field: "estrato", headerName: "Estrato", width: 100, type: "number", hideable: true },
      { field: "direccion", headerName: "Dirección", width: 220, hideable: true },
      { field: "email", headerName: "Email", width: 240, hideable: true },
      { field: "celular", headerName: "Celular", width: 150, hideable: true },
      {
        field: "estado",
        headerName: "Estado",
        width: 130,
        hideable: true,
        valueGetter: (p) =>
          p?.row?.estado?.nombre ??
          p?.row?.estado?.name ??
          (String(p?.row?.estado) === "1" ||
          String(p?.row?.estadoId) === "1"
            ? "Activo"
            : "Inactivo"),
      },
    ],
    []
  );

  /* -------- Persistencia de visibilidad de columnas -------- */
  const [columnVisibilityModel, setColumnVisibilityModel] = useState({});

  useEffect(() => {
    try {
      const saved = JSON.parse(localStorage.getItem(LS_KEY) || "{}");
      if (saved && typeof saved === "object") setColumnVisibilityModel(saved);
    } catch {
      // noop
    }
  }, []);

  const handleVisibilityChange = (model) => {
    setColumnVisibilityModel(model);
    try {
      localStorage.setItem(LS_KEY, JSON.stringify(model));
    } catch {
      // noop
    }
  };

  const handleResetColumns = () => {
    localStorage.removeItem(LS_KEY);
    setColumnVisibilityModel({}); // todas visibles
  };

  /* -------- Paginación: server-side opcional -------- */
  const serverPagination =
    Boolean(paginationModel && setPaginationModel && typeof rowCount === "number");

  return (
    <Box sx={{ width: "100%" }}>
      <GridBase
        rows={Array.isArray(personas) ? personas : []}
        columns={columns}
        getRowId={(row) => row.id}
        loading={loading}
        autoHeight

        // Selección
        onRowClick={(params) => setSelectedRow?.(params.row)}
        rowSelectionModel={selectedRow?.id ? [selectedRow.id] : []}
        disableRowSelectionOnClick

        // Columnas + toolbar
        columnVisibilityModel={columnVisibilityModel}
        onColumnVisibilityModelChange={handleVisibilityChange}
        slots={{ toolbar: PersonaToolbar }}
        slotProps={{ toolbar: { onResetColumns: handleResetColumns } }}

        // Paginación
        paginationMode={serverPagination ? "server" : "client"}
        {...(serverPagination
          ? {
              rowCount,
              paginationModel: {
                page: paginationModel.page ?? 0,
                pageSize: paginationModel.pageSize ?? paginationModel.size ?? 5,
              },
              onPaginationModelChange: (model) => {
                const next = {
                  page: model.page ?? 0,
                  size: model.pageSize ?? model.size ?? 5,
                };
                setPaginationModel?.(next);
              },
              pageSizeOptions: [5, 10, 20, 50],
            }
          : {
              pageSizeOptions: [5, 10, 20, 50],
              initialState: {
                pagination: { paginationModel: { page: 0, pageSize: 5 } },
              },
            })}
      />
    </Box>
  );
}

GridPersona.propTypes = {
  personas: PropTypes.array,
  selectedRow: PropTypes.object,
  setSelectedRow: PropTypes.func,
  paginationModel: PropTypes.shape({
    page: PropTypes.number,
    pageSize: PropTypes.number,
    size: PropTypes.number,
  }),
  setPaginationModel: PropTypes.func,
  rowCount: PropTypes.number,
  loading: PropTypes.bool,
};
