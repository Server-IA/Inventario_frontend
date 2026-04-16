import React, { useEffect, useMemo, useState } from "react";
import PropTypes from "prop-types";
import {
  GridToolbarContainer,
  GridToolbarColumnsButton,
  GridToolbarFilterButton,
  GridToolbarDensitySelector,
  GridToolbarQuickFilter,
} from "@mui/x-data-grid";
import { Box, Button } from "@mui/material";
import RestartAltIcon from "@mui/icons-material/RestartAlt";
import AppDataGrid from "../common/AppDataGrid";

const LS_KEY = "gridEstado:columnVisibility:v1";

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

export default function GridEstado({
  rows = [],
  loading = false,
  selectedRow = null,
  setSelectedRow = () => {},
}) {
  const columns = useMemo(
    () => [
      { field: "id", headerName: "ID", width: 80 },
      { field: "nombre", headerName: "Nombre", width: 220 },
      { field: "acronimo", headerName: "Acronimo", width: 150 },
      { field: "descripcion", headerName: "Descripcion", flex: 1, minWidth: 300 },
      {
        field: "categoriaNombre",
        headerName: "Categoria",
        width: 200,
        valueGetter: (params) => params.row?.estadoCategoria?.nombre || "",
      },
      {
        field: "categoriaDescripcion",
        headerName: "Desc. categoria",
        flex: 1,
        minWidth: 260,
        valueGetter: (params) => params.row?.estadoCategoria?.descripcion || "",
      },
    ],
    []
  );

  const [columnVisibilityModel, setColumnVisibilityModel] = useState({});

  useEffect(() => {
    try {
      const saved = JSON.parse(localStorage.getItem(LS_KEY) || "{}");
      setColumnVisibilityModel(saved);
    } catch {
      setColumnVisibilityModel({});
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

  return (
    <Box sx={{ width: "100%", mt: 1 }}>
      <AppDataGrid
        rows={Array.isArray(rows) ? rows : []}
        columns={columns}
        getRowId={(r) => r.id}
        loading={loading}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
        columnVisibilityModel={columnVisibilityModel}
        onColumnVisibilityModelChange={handleVisibilityChange}
        slots={{ toolbar: EstadoToolbar }}
        slotProps={{ toolbar: { onResetColumns: handleResetColumns } }}
        pageSizeOptions={[5, 10, 20, 50]}
        autoHeight
        containerSx={{ borderRadius: 4 }}
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
