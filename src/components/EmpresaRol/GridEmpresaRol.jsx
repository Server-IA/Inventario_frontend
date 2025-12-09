import React, { useMemo } from "react";
import PropTypes from "prop-types";
import { Box, Button } from "@mui/material";
import DeleteRounded from "@mui/icons-material/DeleteRounded";
import RestartAltIcon from "@mui/icons-material/RestartAlt";

import {
  DataGrid,
  esES,
  GridToolbarContainer,
  GridToolbarColumnsButton,
  GridToolbarFilterButton,
  GridToolbarDensitySelector,
  GridToolbarQuickFilter,
} from "@mui/x-data-grid";

const LS_KEY = "gridEmpresaRol:columnVisibility:v1";

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

EmpresaRolToolbar.propTypes = {
  onResetColumns: PropTypes.func,
};

export default function GridEmpresaRol({
  rows = [],
  loading = false,
  onDelete = () => {},
  columnVisibilityModel,
  setColumnVisibilityModel,
  onResetColumns,
}) {
  const columns = useMemo(
    () => [
      { field: "id", headerName: "ID", width: 80 },
      {
        field: "empresaNombre",
        headerName: "Empresa",
        flex: 1,
        minWidth: 200,
      },
      {
        field: "rolNombre",
        headerName: "Rol",
        flex: 1,
        minWidth: 200,
      },
      {
        field: "estadoNombre",
        headerName: "Estado",
        width: 150,
      },
      {
        field: "acciones",
        headerName: "Acciones",
        width: 140,
        sortable: false,
        filterable: false,
        renderCell: (params) => (
          <Button
            variant="outlined"
            size="small"
            color="error"
            startIcon={<DeleteRounded />}
            onClick={() => onDelete(params.row.id)}
          >
            Eliminar
          </Button>
        ),
      },
    ],
    [onDelete]
  );

  const handleColumnVisibilityChange = (model) => {
    setColumnVisibilityModel(model);
    try {
      localStorage.setItem(LS_KEY, JSON.stringify(model));
    } catch (e) {
      // ignore
    }
  };

  // Cargar columnas desde localStorage
  React.useEffect(() => {
    try {
      const saved = localStorage.getItem(LS_KEY);
      if (saved) {
        const parsed = JSON.parse(saved);
        setColumnVisibilityModel((prev) => ({ ...prev, ...parsed }));
      }
    } catch (e) {
      // ignore
    }
    // solo al montar
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <Box sx={{ height: 450, width: "100%" }}>
      <DataGrid
        rows={rows}
        columns={columns}
        loading={loading}
        getRowId={(row) => row.id}
        pageSizeOptions={[5, 10, 25]}
        initialState={{
          pagination: { paginationModel: { pageSize: 10, page: 0 } },
        }}
        columnVisibilityModel={columnVisibilityModel}
        onColumnVisibilityModelChange={handleColumnVisibilityChange}
        slots={{
          toolbar: EmpresaRolToolbar,
        }}
        slotProps={{
          toolbar: { onResetColumns },
        }}
        localeText={esES.components.MuiDataGrid.defaultProps.localeText}
      />
    </Box>
  );
}

GridEmpresaRol.propTypes = {
  rows: PropTypes.array,
  loading: PropTypes.bool,
  onDelete: PropTypes.func,
  columnVisibilityModel: PropTypes.object.isRequired,
  setColumnVisibilityModel: PropTypes.func.isRequired,
  onResetColumns: PropTypes.func.isRequired,
};
