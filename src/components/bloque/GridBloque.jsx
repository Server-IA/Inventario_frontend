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
import GridBase from "../dashboard/GridBase";

const LS_KEY = "gridBloque:columnVisibility:v1";

/* -------- Toolbar personalizada -------- */
function BloqueToolbar({ onResetColumns }) {
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

export default function GridBloque({
  // Datos
  bloques = [],

  // Selección
  selectedRow = null,
  setSelectedRow,

  // Paginación server-side (opcional)
  paginationModel,        // { page, pageSize } o { page, size }
  setPaginationModel,     // (model) => void
  rowCount,               // total en servidor
  loading = false,
}) {
  /* -------- Columnas (todas hideable) -------- */
  const columns = useMemo(
    () => [
      { field: "id", headerName: "ID", width: 80, hideable: true },
      { field: "nombre", headerName: "Nombre", width: 200, hideable: true },
      {
        field: "tipoBloqueNombre",
        headerName: "Tipo Bloque",
        width: 200,
        hideable: true,
        valueGetter: (p) =>
          p?.row?.tipoBloqueNombre ??
          p?.row?.tipoBloque?.nombre ??
          p?.row?.tipoBloque?.name ??
          String(p?.row?.tipoBloqueId ?? ""),
      },
      {
        field: "sedeNombre",
        headerName: "Sede",
        width: 200,
        hideable: true,
        valueGetter: (p) =>
          p?.row?.sedeNombre ??
          p?.row?.sede?.nombre ??
          p?.row?.sede?.name ??
          String(p?.row?.sedeId ?? ""),
      },
      { field: "numeroPisos", headerName: "Pisos", width: 110, type: "number", hideable: true },
      { field: "descripcion", headerName: "Descripción", flex: 1, minWidth: 220, hideable: true },
      { field: "direccion", headerName: "Dirección", width: 220, hideable: true },
      { field: "geolocalizacion", headerName: "Geolocalización", width: 180, hideable: true },
      { field: "coordenadas", headerName: "Coordenadas", width: 180, hideable: true },
      {
        field: "estadoId",
        headerName: "Estado",
        width: 140,
        hideable: true,
        valueGetter: (p) =>
          p?.row?.estado?.nombre ??
          p?.row?.estado?.name ??
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
    setColumnVisibilityModel({}); // todas visibles por defecto
  };

  const serverPagination =
    Boolean(paginationModel && setPaginationModel && typeof rowCount === "number");

  return (
    <div style={{ width: "100%" }}>
      <GridBase
        rows={Array.isArray(bloques) ? bloques : []}
        columns={columns}
        getRowId={(row) => row.id}
        loading={loading}

        // Selección controlada por clic
        onRowClick={(params) => setSelectedRow?.(params.row)}
        rowSelectionModel={selectedRow?.id ? [selectedRow.id] : []}
        disableRowSelectionOnClick
        autoHeight

        // Columnas + toolbar
        columnVisibilityModel={columnVisibilityModel}
        onColumnVisibilityModelChange={handleVisibilityChange}
        slots={{ toolbar: BloqueToolbar }}
        slotProps={{ toolbar: { onResetColumns: handleResetColumns } }}

        // Paginación
        paginationMode={serverPagination ? "server" : "client"}
        {...(serverPagination
          ? {
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
              pageSizeOptions: [5, 10, 20, 50],
            }
          : {
              pageSizeOptions: [5, 10, 20, 50],
              initialState: {
                pagination: { paginationModel: { page: 0, pageSize: 5 } },
              },
            })}
      />
    </div>
  );
}

GridBloque.propTypes = {
  bloques: PropTypes.array,
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
