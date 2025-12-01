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

const LS_KEY = "gridEstado:columnVisibility:v1";

/* ---------- Toolbar personalizada ---------- */
function EstadoToolbar({ onResetColumns }) {
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

/* ---------- GridEstado ---------- */
export default function GridEstado({
  rows = [],
  loading = false,
  selectedRow = null,
  setSelectedRow = () => {},
}) {
  /* ---------- Definición de columnas ---------- */
  const columns = useMemo(
    () => [
      { field: "id", headerName: "ID", width: 80 },
      { field: "nombre", headerName: "Nombre", width: 220 },
      { field: "acronimo", headerName: "Acrónimo", width: 150 },
      { field: "descripcion", headerName: "Descripción", flex: 1, minWidth: 300 },

      {
        field: "categoriaNombre",
        headerName: "Categoría",
        width: 200,
        valueGetter: (params) => params.row?.estadoCategoria?.nombre || "",
      },
      {
        field: "categoriaDescripcion",
        headerName: "Desc. categoría",
        flex: 1,
        minWidth: 260,
        valueGetter: (params) =>
          params.row?.estadoCategoria?.descripcion || "",
      },
    ],
    []
  );

  /* ---------- Persistencia de visibilidad ---------- */
  const [columnVisibilityModel, setColumnVisibilityModel] = useState({});

  useEffect(() => {
    try {
      const saved = JSON.parse(localStorage.getItem(LS_KEY) || "{}");
      setColumnVisibilityModel(saved);
    } catch {}
  }, []);

  const handleVisibilityChange = (model) => {
    setColumnVisibilityModel(model);
    localStorage.setItem(LS_KEY, JSON.stringify(model));
  };

  const handleResetColumns = () => {
    localStorage.removeItem(LS_KEY);
    setColumnVisibilityModel({});
  };

  /* ---------- Paginación controlada ---------- */
  const [paginationModel, setPaginationModel] = useState({
    page: 0,
    pageSize: 10,
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
      <DataGrid
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
        slots={{ toolbar: EstadoToolbar }}
        slotProps={{ toolbar: { onResetColumns: handleResetColumns } }}

        /* paginación */
        pagination
        paginationModel={paginationModel}
        onPaginationModelChange={handlePaginationChange}
        pageSizeOptions={[5, 10, 20, 50]}

        /* autoHeight para evitar huecos */
        autoHeight
        sx={{
          minHeight: 300,
          "& .MuiDataGrid-virtualScroller": { overflow: "auto" },
        }}
      />
    </Box>
  );
}

GridEstado.propTypes = {
  rows: PropTypes.array,
  loading: PropTypes.bool,
  selectedRow: PropTypes.object,
  setSelectedRow: PropTypes.func,
};
