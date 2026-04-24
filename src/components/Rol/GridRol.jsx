// src/components/rol/GridRol.jsx
import React, { useEffect, useMemo, useState } from "react";
import PropTypes from "prop-types";
import { Box } from "@mui/material";
import AppDataGrid from "../common/AppDataGrid";

const LS_KEY = "gridRol:columnVisibility:v1";
const DEFAULT_COLUMN_VISIBILITY = {
  createdBy: false,
  createdAt: false,
  updatedBy: false,
  updatedAt: false,
  deletedBy: false,
  deletedAt: false,
};

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
  const [columnVisibilityModel, setColumnVisibilityModel] = useState(
    DEFAULT_COLUMN_VISIBILITY
  );

  useEffect(() => {
    try {
      const saved = JSON.parse(localStorage.getItem(LS_KEY) || "{}");
      setColumnVisibilityModel({
        ...DEFAULT_COLUMN_VISIBILITY,
        ...saved,
      });
    } catch {
      // ignore
      setColumnVisibilityModel(DEFAULT_COLUMN_VISIBILITY);
    }
  }, []);

  const handleVisibilityChange = (model) => {
    setColumnVisibilityModel(model);
    localStorage.setItem(LS_KEY, JSON.stringify(model));
  };

  return (
    <Box sx={{ width: "100%", mt: 1 }}>
      <AppDataGrid
        rows={Array.isArray(rows) ? rows : []}
        columns={columns}
        getRowId={(r) => r.id}
        loading={loading}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
        quickFilter
        columnVisibilityModel={columnVisibilityModel}
        onColumnVisibilityModelChange={handleVisibilityChange}
        columnVisibilityKey={LS_KEY}
        pageSizeOptions={[5, 10, 20, 50]}
        autoHeight
        sx={{
          minHeight: 300,
          "& .MuiDataGrid-virtualScroller": { overflow: "auto" },
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
