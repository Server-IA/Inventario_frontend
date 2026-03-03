// src/components/UsuarioRoles/GridUsuarioRoles.jsx
import React from "react";
import PropTypes from "prop-types";
import { Box, Button } from "@mui/material";
import {
  DataGrid,
  esES,
  GridToolbarContainer,
  GridToolbarColumnsButton,
  GridToolbarFilterButton,
  GridToolbarDensitySelector,
  GridToolbarQuickFilter,
} from "@mui/x-data-grid";
import GridBase from "../dashboard/GridBase";
const LS_KEY = "gridUsuarioRoles:columnVisibility:v1";

/* ---------- Toolbar personalizada ---------- */
function CustomToolbar({ onResetColumns }) {
  return (
    <GridToolbarContainer
      sx={{ p: 1, gap: 1, justifyContent: "space-between" }}
    >
      <div>
        <GridToolbarColumnsButton />
        <GridToolbarFilterButton />
        <GridToolbarDensitySelector />
      </div>
      <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
        <GridToolbarQuickFilter debounceMs={300} />
        <Button variant="outlined" size="small" onClick={onResetColumns}>
          RESET COLUMNAS
        </Button>
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
  selectedRow,      // lo seguimos recibiendo por si lo quieres usar
  setSelectedRow,
  usuariosMap = {},
  empresasMap = {},
  rolesMap = {},
  estadosMap = {},
}) {
  /* ---------- visibilidad de columnas (localStorage) ---------- */
  const [columnVisibilityModel, setColumnVisibilityModel] = React.useState(
    () => {
      try {
        const stored = localStorage.getItem(LS_KEY);
        return stored ? JSON.parse(stored) : {};
      } catch {
        return {};
      }
    }
  );

  const handleColumnVisibilityChange = (newModel) => {
    setColumnVisibilityModel(newModel);
    localStorage.setItem(LS_KEY, JSON.stringify(newModel));
  };

  const handleResetColumns = () => {
    setColumnVisibilityModel({});
    localStorage.removeItem(LS_KEY);
  };

  /* ---------- paginación 5 / 10 / 15 ---------- */
  const [paginationModel, setPaginationModel] = React.useState({
    pageSize: 5,
    page: 0,
  });

  const handlePaginationModelChange = (newModel) => {
    const totalPages =
      newModel.pageSize > 0
        ? Math.max(1, Math.ceil(rows.length / newModel.pageSize))
        : 1;
    const maxPage = totalPages - 1;

    setPaginationModel({
      ...newModel,
      page: Math.min(newModel.page, maxPage),
    });
  };

  /* ---------- definición de columnas ---------- */
  const columns = [
    { field: "id", headerName: "ID", width: 70 },
    {
      field: "usuarioId",
      headerName: "Usuario",
      width: 220,
      valueGetter: (params) =>
        usuariosMap[params.row.usuarioId] ?? params.row.usuarioId,
    },
    {
      field: "empresaId",
      headerName: "Empresa",
      width: 220,
      valueGetter: (params) =>
        empresasMap[params.row.empresaId] ?? params.row.empresaId,
    },
    {
      field: "rolId",
      headerName: "Rol",
      width: 220,
      valueGetter: (params) => rolesMap[params.row.rolId] ?? params.row.rolId,
    },
    {
      field: "estadoId",
      headerName: "Estado",
      width: 150,
      valueGetter: (params) =>
        estadosMap[params.row.estadoId] ?? params.row.estadoId,
    },
    {
      field: "iniciaContratoEn",
      headerName: "Inicia contrato",
      width: 190,
      valueGetter: (params) =>
        params.value ? new Date(params.value).toLocaleString() : "",
    },
    {
      field: "finalizaContratoEn",
      headerName: "Finaliza contrato",
      width: 190,
      valueGetter: (params) =>
        params.value ? new Date(params.value).toLocaleString() : "",
    },
  ];

  return (
    <Box sx={{ height: 430, width: "100%" }}>
      <GridBase
        rows={rows}
        columns={columns}
        loading={loading}
        getRowId={(row) => row.id}
        localeText={esES.components.MuiDataGrid.defaultProps.localeText}
        disableRowSelectionOnClick
        paginationMode="client"
        columnVisibilityModel={columnVisibilityModel}
        onColumnVisibilityModelChange={handleColumnVisibilityChange}
        slots={{
          toolbar: CustomToolbar,
        }}
        slotProps={{
          toolbar: { onResetColumns: handleResetColumns },
        }}
        /* ---------- PAGINACIÓN: SOLO 5, 10, 15 ---------- */
        pagination
        paginationModel={paginationModel}
        onPaginationModelChange={handlePaginationModelChange}
        pageSizeOptions={[5, 10, 15]}
        /* ---------- OCULTAR SCROLLBAR VERTICAL ---------- */
        sx={{
          "& .MuiDataGrid-virtualScroller": {
            overflowY: "auto",
            scrollbarWidth: "none",
            msOverflowStyle: "none",
          },
          "& .MuiDataGrid-virtualScroller::-webkit-scrollbar": {
            display: "none",
          },
        }}
        /* ---------- SELECCIÓN SIMPLE: SOLO onRowClick ---------- */
        onRowClick={(params) => {
          setSelectedRow(params.row ?? null);
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
  usuariosMap: PropTypes.object,
  empresasMap: PropTypes.object,
  rolesMap: PropTypes.object,
  estadosMap: PropTypes.object,
};
