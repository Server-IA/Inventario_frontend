// src/components/Proveedor/GridProveedor.jsx
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
import { Box, Button } from "@mui/material";
import RestartAltIcon from "@mui/icons-material/RestartAlt";
import GridBase from "../dashboard/GridBase";

const LS_KEY = "gridProveedor:columnVisibility:v1";

/* ---------- Toolbar personalizada ---------- */
function ProveedorToolbar({ onResetColumns }) {
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

export default function GridProveedor({
  // Datos
  proveedores = [],

  // Selección
  selectedRow = null,
  setSelectedRow,

  // Paginación (server-side opcional)
  paginationModel,        // { page, pageSize } o { page, size }
  setPaginationModel,     // (model) => void
  rowCount,               // total en servidor
  loading = false,
}) {
  const safeDateTime = (v) => {
    if (!v) return "";
    const d = new Date(v);
    return Number.isNaN(d.getTime())
      ? String(v).toString().substring(0, 19)
      : d.toLocaleString();
  };

  /* ---------- Columnas (todas hideable) ---------- */
  const columns = useMemo(
    () => [
      { field: "id", headerName: "ID", width: 80, type: "number", hideable: true },
      { field: "nombre", headerName: "Nombre", width: 180, hideable: true },
      { field: "identificacion", headerName: "Identificación", width: 160, hideable: true },
      { field: "contacto", headerName: "Contacto", width: 170, hideable: true },
      { field: "correo", headerName: "Correo", width: 220, hideable: true },
      { field: "celular", headerName: "Celular", width: 140, hideable: true },
      {
        field: "tipoIdentificacionId",
        headerName: "Tipo Identificación",
        width: 200,
        hideable: true,
        valueGetter: (p) =>
          p?.row?.tipoIdentificacionNombre ??
          p?.row?.tipoIdentificacion?.nombre ??
          p?.row?.tipoIdentificacion?.name ??
          String(p?.row?.tipoIdentificacionId ?? ""),
      },
      {
        field: "fechaCreacion",
        headerName: "Fecha de Creación",
        width: 200,
        hideable: true,
        valueGetter: (p) => safeDateTime(p?.row?.fechaCreacion),
      },
      {
        field: "estadoId",
        headerName: "Estado",
        width: 130,
        hideable: true,
        valueGetter: (p) =>
          p?.row?.estado?.nombre ??
          p?.row?.estado?.name ??
          (String(p?.row?.estadoId) === "1" ? "Activo" : "Inactivo"),
      },
    ],
    []
  );

  /* ---------- Persistencia visibilidad ---------- */
  const [columnVisibilityModel, setColumnVisibilityModel] = useState({});

  useEffect(() => {
    try {
      const saved = JSON.parse(localStorage.getItem(LS_KEY) || "{}");
      if (saved && typeof saved === "object") setColumnVisibilityModel(saved);
    } catch {
      /* noop */
    }
  }, []);

  const handleVisibilityChange = (model) => {
    setColumnVisibilityModel(model);
    try {
      localStorage.setItem(LS_KEY, JSON.stringify(model));
    } catch {
      /* noop */
    }
  };

  const handleResetColumns = () => {
    localStorage.removeItem(LS_KEY);
    setColumnVisibilityModel({});
  };

  const serverPagination =
    Boolean(paginationModel && setPaginationModel && typeof rowCount === "number");

  return (
    <Box sx={{ width: "100%" }}>
      <GridBase
        rows={Array.isArray(proveedores) ? proveedores : []}
        columns={columns}
        getRowId={(row) => row.id}
        loading={loading}

        // Selección controlada
        onRowClick={(params) => setSelectedRow?.(params.row)}
        rowSelectionModel={selectedRow?.id ? [selectedRow.id] : []}
        disableRowSelectionOnClick

        // Columnas + toolbar
        columnVisibilityModel={columnVisibilityModel}
        onColumnVisibilityModelChange={handleVisibilityChange}
        slots={{ toolbar: ProveedorToolbar }}
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
              pageSizeOptions: [5, 10, 15, 20, 50],
            }
          : {
              pageSizeOptions: [5, 10, 15, 20, 50],
              initialState: {
                pagination: { paginationModel: { page: 0, pageSize: 5 } },
              },
            })}
        autoHeight
      />
    </Box>
  );
}

GridProveedor.propTypes = {
  proveedores: PropTypes.array,
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
