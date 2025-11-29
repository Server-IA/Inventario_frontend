// src/components/usuarioRol/GridUsuarioRol.jsx
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

const LS_KEY = "gridUsuarioRol:columnVisibility:v1";

function UsuarioRolToolbar({ onResetColumns }) {
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

UsuarioRolToolbar.propTypes = {
  onResetColumns: PropTypes.func,
};

export default function GridUsuarioRol({
  rows = [],
  loading = false,
  selectedRow = null,
  setSelectedRow = () => {},
}) {
  const columns = useMemo(
    () => [
      { field: "id", headerName: "ID", width: 90, type: "number" },

      { field: "usuarioId", headerName: "Usuario ID", width: 110, type: "number" },
      { field: "usuarioEmail", headerName: "Usuario", flex: 1.2, minWidth: 220 },

      { field: "empresaId", headerName: "Empresa ID", width: 110, type: "number" },
      { field: "empresaNombre", headerName: "Empresa", flex: 1.2, minWidth: 220 },

      { field: "rolId", headerName: "Rol ID", width: 100, type: "number" },
      { field: "rolNombre", headerName: "Rol", flex: 1.1, minWidth: 220 },

      { field: "estadoId", headerName: "Estado ID", width: 110, type: "number" },
      { field: "estadoNombre", headerName: "Estado", width: 160 },

      { field: "iniciaContratoEn", headerName: "Inicia contrato", width: 210 },
      { field: "finalizaContratoEn", headerName: "Finaliza contrato", width: 210 },

      // Auditoría
      { field: "createdBy", headerName: "Creado por", width: 180 },
      { field: "createdAt", headerName: "Creado el", width: 210 },
      { field: "updatedBy", headerName: "Actualizado por", width: 180 },
      { field: "updatedAt", headerName: "Actualizado el", width: 210 },
    ],
    []
  );

  // Persistencia visibilidad columnas
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

  // Paginación controlada
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
      <DataGrid
        rows={Array.isArray(rows) ? rows : []}
        columns={columns}
        getRowId={(r) => r.id}
        loading={loading}
        onRowClick={(p) => setSelectedRow(p.row)}
        rowSelectionModel={selectedRow?.id ? [selectedRow.id] : []}
        disableRowSelectionOnClick
        columnVisibilityModel={columnVisibilityModel}
        onColumnVisibilityModelChange={handleVisibilityChange}
        slots={{ toolbar: UsuarioRolToolbar }}
        slotProps={{ toolbar: { onResetColumns: handleResetColumns } }}
        pagination
        paginationModel={paginationModel}
        onPaginationModelChange={handlePaginationChange}
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

GridUsuarioRol.propTypes = {
  rows: PropTypes.array,
  loading: PropTypes.bool,
  selectedRow: PropTypes.object,
  setSelectedRow: PropTypes.func,
};
