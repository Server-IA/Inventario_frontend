// src/components/Producto/GridProducto.jsx
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

const LS_KEY = "gridProducto:columnVisibility:v1";

/* ---------- Toolbar personalizada ---------- */
function ProductoToolbar({ onResetColumns }) {
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

export default function GridProducto({
  rows = [],
  loading = false,
  rowCount = 0,

  selectedRow = null,
  setSelectedRow = () => {},

  // server-side
  paginationModel,
  onPaginationModelChange,
  sortModel,
  onSortModelChange,
}) {
  /* ---------- Columnas (todas hideable) ---------- */
  const columns = useMemo(
    () => [
      { field: "id", headerName: "ID", width: 90, type: "number", hideable: true },
      { field: "nombre", headerName: "Nombre", flex: 1, minWidth: 220, hideable: true },
      { field: "productoCategoriaId", headerName: "Cat. ID", width: 110, type: "number", hideable: true },
      { field: "productoCategoriaNombre", headerName: "Categoría", flex: 1, minWidth: 200, hideable: true },
      { field: "unidadMinimaId", headerName: "Unidad ID", width: 110, type: "number", hideable: true },
      { field: "unidadMinimaNombre", headerName: "Unidad mínima", width: 180, hideable: true },
      { field: "cantidadMinima", headerName: "Cant. mínima", width: 140, type: "number", hideable: true },
      { field: "descripcion", headerName: "Descripción", flex: 1.2, minWidth: 260, hideable: true },
      {
        field: "esOrganico",
        headerName: "Orgánico",
        width: 120,
        hideable: true,
        valueGetter: (params) => {
          const v = params.value;
          if (v === true) return "Sí";
          if (v === false) return "No";
          return "";
        },
        sortComparator: (a, b) => {
          const va = a === "Sí" ? 1 : a === "No" ? 0 : -1;
          const vb = b === "Sí" ? 1 : b === "No" ? 0 : -1;
          return va - vb;
        },
      },
      { field: "estadoId", headerName: "Estado ID", width: 110, type: "number", hideable: true },
      { field: "estadoNombre", headerName: "Estado", width: 140, hideable: true },
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

  return (
    <Box sx={{ width: "100%", mt: 1 }}>
      <DataGrid
        rows={Array.isArray(rows) ? rows : []}
        columns={columns}
        getRowId={(r) => r.id}
        loading={loading}
        rowCount={rowCount}

        // selección
        onRowClick={(p) => setSelectedRow(p.row)}
        rowSelectionModel={selectedRow?.id ? [selectedRow.id] : []}
        disableRowSelectionOnClick

        // server-side
        paginationMode="server"
        sortingMode="server"
        paginationModel={paginationModel}
        onPaginationModelChange={onPaginationModelChange}
        sortModel={sortModel}
        onSortModelChange={onSortModelChange}

        // columnas + toolbar
        columnVisibilityModel={columnVisibilityModel}
        onColumnVisibilityModelChange={handleVisibilityChange}
        slots={{ toolbar: ProductoToolbar }}
        slotProps={{ toolbar: { onResetColumns: handleResetColumns } }}

        // UX
        autoHeight
        pageSizeOptions={[5, 10, 20, 50]}
      />
    </Box>
  );
}

GridProducto.propTypes = {
  rows: PropTypes.array,
  loading: PropTypes.bool,
  rowCount: PropTypes.number,
  selectedRow: PropTypes.object,
  setSelectedRow: PropTypes.func,
  paginationModel: PropTypes.object.isRequired,
  onPaginationModelChange: PropTypes.func.isRequired,
  sortModel: PropTypes.array.isRequired,
  onSortModelChange: PropTypes.func.isRequired,
};
