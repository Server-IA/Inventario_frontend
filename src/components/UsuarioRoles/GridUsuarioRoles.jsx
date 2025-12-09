// src/components/UsuarioRoles/GridUsuarioRoles.jsx
import React from "react";
import PropTypes from "prop-types";
import { Box } from "@mui/material";
import {
  DataGrid,
  esES,
  GridToolbarContainer,
  GridToolbarColumnsButton,
  GridToolbarFilterButton,
  GridToolbarDensitySelector,
  GridToolbarQuickFilter,
} from "@mui/x-data-grid";

const LS_KEY = "gridUsuarioRoles:columnVisibility:v1";

function CustomToolbar({ onResetColumns }) {
  return (
    <GridToolbarContainer sx={{ p: 1, gap: 1, justifyContent: "space-between" }}>
      <div>
        <GridToolbarColumnsButton />
        <GridToolbarFilterButton />
        <GridToolbarDensitySelector />
      </div>
      <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
        <GridToolbarQuickFilter debounceMs={300} />
        <button onClick={onResetColumns}>Reset columnas</button>
      </div>
    </GridToolbarContainer>
  );
}

CustomToolbar.propTypes = {
  onResetColumns: PropTypes.func,
};

export default function GridUsuarioRoles({
  rows,
  loading,
  selectedRow,
  setSelectedRow,
}) {
  const [columnVisibilityModel, setColumnVisibilityModel] = React.useState(() => {
    try {
      const stored = localStorage.getItem(LS_KEY);
      return stored ? JSON.parse(stored) : {};
    } catch {
      return {};
    }
  });

  const handleColumnVisibilityChange = (newModel) => {
    setColumnVisibilityModel(newModel);
    localStorage.setItem(LS_KEY, JSON.stringify(newModel));
  };

  const handleResetColumns = () => {
    setColumnVisibilityModel({});
    localStorage.removeItem(LS_KEY);
  };

  const columns = [
    { field: "id", headerName: "ID", width: 70 },
    { field: "usuarioId", headerName: "Usuario ID", width: 110 },
    { field: "empresaId", headerName: "Empresa ID", width: 110 },
    { field: "rolId", headerName: "Rol ID", width: 110 },
    { field: "estadoId", headerName: "Estado ID", width: 110 },
    {
      field: "iniciaContratoEn",
      headerName: "Inicia contrato",
      width: 180,
      valueGetter: (params) =>
        params.value ? new Date(params.value).toLocaleString() : "",
    },
    {
      field: "finalizaContratoEn",
      headerName: "Finaliza contrato",
      width: 180,
      valueGetter: (params) =>
        params.value ? new Date(params.value).toLocaleString() : "",
    },
  ];

  return (
    <Box sx={{ height: 430, width: "100%" }}>
      <DataGrid
        rows={rows}
        columns={columns}
        loading={loading}
        getRowId={(row) => row.id}
        localeText={esES.components.MuiDataGrid.defaultProps.localeText}
        paginationMode="client"
        disableRowSelectionOnClick
        columnVisibilityModel={columnVisibilityModel}
        onColumnVisibilityModelChange={handleColumnVisibilityChange}
        slots={{
          toolbar: CustomToolbar,
        }}
        slotProps={{
          toolbar: {
            onResetColumns: handleResetColumns,
          },
        }}
        onRowClick={(params) => setSelectedRow(params.row)}
        rowSelectionModel={selectedRow ? [selectedRow.id] : []}
        onRowSelectionModelChange={(ids) => {
          const id = ids[0];
          const row = rows.find((r) => r.id === id) || null;
          setSelectedRow(row);
        }}
      />
    </Box>
  );
}

GridUsuarioRoles.propTypes = {
  rows: PropTypes.array.isRequired,
  loading: PropTypes.bool,
  selectedRow: PropTypes.object,
  setSelectedRow: PropTypes.func.isRequired,
};
