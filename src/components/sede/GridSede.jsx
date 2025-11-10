// src/components/Sede/GridSede.jsx
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

const LS_KEY = "gridSede:columnVisibility:v1";

/* -------- Toolbar personalizada -------- */
function SedeToolbar({ onResetColumns }) {
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

export default function GridSede({
  // Datos
  sedes = [],

  // Selección
  selectedRow = null,
  setSelectedRow,

  // Paginación (server-side opcional)
  paginationModel,        // { page, pageSize } o { page, size }
  setPaginationModel,     // (model) => void
  rowCount,               // total en servidor
  loading = false,        // spinner
}) {
  const columns = useMemo(
    () => [
      { field: "id", headerName: "ID", width: 80, hideable: true },
      { field: "nombre", headerName: "Nombre", width: 220, hideable: true },

      // Nombres con fallback a ID
      {
        field: "municipioNombre",
        headerName: "Municipio",
        width: 200,
        hideable: true,
        valueGetter: (p) =>
          p?.row?.municipioNombre ??
          p?.row?.municipio?.name ??
          p?.row?.municipio?.nombre ??
          String(p?.row?.municipioId ?? ""),
      },
      {
        field: "grupoNombre",
        headerName: "Grupo",
        width: 200,
        hideable: true,
        valueGetter: (p) =>
          p?.row?.grupoNombre ??
          p?.row?.grupo?.name ??
          p?.row?.grupo?.nombre ??
          String(p?.row?.grupoId ?? ""),
      },
      {
        field: "tipoSedeNombre",
        headerName: "Tipo Sede",
        width: 220,
        hideable: true,
        valueGetter: (p) =>
          p?.row?.tipoSedeNombre ??
          p?.row?.tipoSede?.name ??
          p?.row?.tipoSede?.nombre ??
          String(p?.row?.tipoSedeId ?? ""),
      },

      { field: "geolocalizacion", headerName: "Geolocalización", width: 200, hideable: true },
      { field: "coordenadas", headerName: "Coordenadas", width: 200, hideable: true },
      { field: "area", headerName: "Área", width: 120, hideable: true },
      { field: "comuna", headerName: "Comuna", width: 140, hideable: true },
      { field: "descripcion", headerName: "Descripción", flex: 1, minWidth: 280, hideable: true },

      {
        field: "estadoId",
        headerName: "Estado",
        width: 140,
        hideable: true,
        valueGetter: (p) =>
          p?.row?.estado?.name ??
          p?.row?.estado?.nombre ??
          (String(p?.row?.estadoId) === "1" ? "Activo" : "Inactivo"),
      },
    ],
    []
  );

  /* -------- Persistencia visibilidad -------- */
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

  // ¿Server o Cliente?
  const serverPagination = Boolean(
    paginationModel && setPaginationModel && typeof rowCount === "number"
  );

  return (
    <div style={{ width: "100%" }}>
      <DataGrid
        rows={Array.isArray(sedes) ? sedes : []}
        columns={columns}
        getRowId={(row) => row.id}

        // Selección
        onRowClick={(params) => setSelectedRow?.(params.row)}
        rowSelectionModel={selectedRow?.id ? [selectedRow.id] : []}
        disableRowSelectionOnClick

        // Columnas + toolbar
        columnVisibilityModel={columnVisibilityModel}
        onColumnVisibilityModelChange={handleVisibilityChange}
        slots={{ toolbar: SedeToolbar }}
        slotProps={{ toolbar: { onResetColumns: handleResetColumns } }}

        // Paginación
        paginationMode={serverPagination ? "server" : "client"}
        loading={loading}
        {...(serverPagination
          ? {
              // ----- Server controlled -----
              paginationModel: {
                page: paginationModel.page ?? 0,
                pageSize: paginationModel.pageSize ?? paginationModel.size ?? 10,
              },
              onPaginationModelChange: (model) => {
                const next = {
                  page: model.page ?? 0,
                  size: model.pageSize ?? model.size ?? 10,
                };
                setPaginationModel?.(next); // el padre hace el fetch con estos valores
              },
              rowCount,
              pageSizeOptions: [5, 10, 15, 20, 50],
            }
          : {
              // ----- Client fallback -----
              pageSizeOptions: [5, 10, 15, 20, 50],
              initialState: {
                pagination: { paginationModel: { page: 0, pageSize: 5 } },
              },
            })}
        autoHeight
      />
    </div>
  );
}

GridSede.propTypes = {
  sedes: PropTypes.array,
  selectedRow: PropTypes.object,
  setSelectedRow: PropTypes.func.isRequired,
  paginationModel: PropTypes.shape({
    page: PropTypes.number,
    pageSize: PropTypes.number,
    size: PropTypes.number,
  }),
  setPaginationModel: PropTypes.func,
  rowCount: PropTypes.number,
  loading: PropTypes.bool,
};
