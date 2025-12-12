import React, { useMemo } from "react";
import PropTypes from "prop-types";
import {
  DataGrid,
  GridToolbarContainer,
  GridToolbarColumnsButton,
  GridToolbarFilterButton,
  GridToolbarDensitySelector,
  GridToolbarQuickFilter,
  esES,
} from "@mui/x-data-grid";
import { Box, Button } from "@mui/material";
import RestartAltIcon from "@mui/icons-material/RestartAlt";

/* ---------- Toolbar personalizada ---------- */
function EmpresaRolToolbar({ onResetColumns }) {
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

export default function GridEmpresaRol({
  rows = [],
  loading = false,
  columnVisibilityModel,
  setColumnVisibilityModel,
  onResetColumns,
}) {
  /* ---------- Columnas sin acciones ---------- */
  const columns = useMemo(
    () => [
      {
        field: "id",
        headerName: "ID",
        width: 90,
      },
      {
        field: "empresaNombre",
        headerName: "Empresa",
        flex: 1,
        minWidth: 150,
      },
      {
        field: "rolNombre",
        headerName: "Rol",
        flex: 1,
        minWidth: 150,
      },
      {
        field: "estadoNombre",
        headerName: "Estado",
        flex: 1,
        minWidth: 150,
      },
    ],
    []
  );

  return (
    <Box sx={{ height: 500, width: "100%" }}>
      <DataGrid
        rows={rows}
        columns={columns}
        loading={loading}
        disableRowSelectionOnClick
        getRowId={(row) => row.id}
        slots={{
          toolbar: EmpresaRolToolbar,
        }}
        slotProps={{
          toolbar: { onResetColumns },
        }}
        columnVisibilityModel={columnVisibilityModel}
        onColumnVisibilityModelChange={setColumnVisibilityModel}
        localeText={esES.components.MuiDataGrid.defaultProps.localeText}
      />
    </Box>
  );
}

/* ---------- PropTypes ---------- */
GridEmpresaRol.propTypes = {
  rows: PropTypes.array,
  loading: PropTypes.bool,
  columnVisibilityModel: PropTypes.object.isRequired,
  setColumnVisibilityModel: PropTypes.func.isRequired,
  onResetColumns: PropTypes.func.isRequired,
};
