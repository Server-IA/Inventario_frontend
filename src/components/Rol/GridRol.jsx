// src/components/rol/GridRol.jsx
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
const LS_KEY = "gridRol:columnVisibility:v1";

/* ---------- Toolbar personalizada ---------- */
function RolToolbar({ onResetColumns }) {
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

/* ---------- GridRol ---------- */
export default function GridRol({
  rows = [],
  loading = false,
  selectedRow = null,
  setSelectedRow = () => {},
}) {
  /* ---------- Columnas ---------- */
  const columns = useMemo(
    () => [
      { field: "id", headerName: "ID", width: 90, type: "number" },
      { field: "nombre", headerName: "Nombre", flex: 1, minWidth: 220 },
      { field: "descripcion", headerName: "Descripción", flex: 1.4, minWidth: 260 },
      { field: "estadoId", headerName: "Estado ID", width: 110, type: "number" },
      { field: "estadoNombre", headerName: "Estado", width: 160 },

      // Auditoría
      { field: "createdBy", headerName: "Creado por", width: 130, type: "number" },
      { field: "createdAt", headerName: "Creado el", width: 200 },
      { field: "updatedBy", headerName: "Actualizado por", width: 140, type: "number" },
      { field: "updatedAt", headerName: "Actualizado el", width: 200 },
      { field: "deletedBy", headerName: "Eliminado por", width: 140, type: "number" },
      { field: "deletedAt", headerName: "Eliminado el", width: 200 },
    ],
    []
  );

  /* ---------- Persistencia visibilidad ---------- */
  const [columnVisibilityModel, setColumnVisibilityModel] = useState({});

  useEffect(() => {
    try {
      const saved = JSON.parse(localStorage.getItem(LS_KEY) || "{}");
      setColumnVisibilityModel(saved);
    } catch {
      // ignore
    }
  }, []);

  const handleVisibilityChange = (model) => {
    setColumnVisibilityModel(model);
    localStorage.setItem(LS_KEY, JSON.stringify(model));
  };

  const handleResetColumns = () => {
    localStorage.removeItem(LS_KEY);
    setColumnVisibilityModel({});
  };

  /* ---------- Paginación CONTROLADA ---------- */
  const [paginationModel, setPaginationModel] = useState({
    page: 0,
    pageSize: 5,
  });

  const handlePaginationChange = (model) => {
    if (model.pageSize !== paginationModel.pageSize) {
      setPaginationModel({ page: 0, pageSize: model.pageSize });
    } else {
      setPaginationModel(model);
    }
  };

  return (
    <Box sx={{ width: "100%", mt: 1 }}>
      <GridBase
        rows={Array.isArray(rows) ? rows : []}
        columns={columns}
        getRowId={(r) => r.id}
        loading={loading}
        /* selección */
        onRowClick={(p) => setSelectedRow(p.row)}
        rowSelectionModel={selectedRow?.id ? [selectedRow.id] : []}
        disableRowSelectionOnClick
        /* columnas + toolbar */
        columnVisibilityModel={columnVisibilityModel}
        onColumnVisibilityModelChange={handleVisibilityChange}
        slots={{ toolbar: RolToolbar }}
        slotProps={{ toolbar: { onResetColumns: handleResetColumns } }}
        /* paginación controlada (cliente) */
        pagination
        paginationModel={paginationModel}
        onPaginationModelChange={handlePaginationChange}
        pageSizeOptions={[5, 10, 20, 50]}
        /* altura fija para que funcione la paginación */
        autoHeight
sx={{
  minHeight: 300,      // evita que se vea muy pequeño
  '& .MuiDataGrid-virtualScroller': { overflow: 'auto' },
}}

      />
    </Box>
  );
}

GridRol.propTypes = {
  rows: PropTypes.array,
  loading: PropTypes.bool,
  selectedRow: PropTypes.object,
  setSelectedRow: PropTypes.func,
};
