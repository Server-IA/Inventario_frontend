// src/components/IngredientePP/GridIngredientePresentacionP.jsx
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
import GridBase from "../dashboard/GridBase";
const LS_KEY = "gridIngredientePP:columnVisibility:v1";

/* ---------- Toolbar personalizada ---------- */
function IngredientePPToolbar({ onResetColumns }) {
  return (
    <GridToolbarContainer
      sx={{
        p: 1,
        gap: 1,
        justifyContent: "space-between",
        alignItems: "center",
      }}
    >
      <div>
        <GridToolbarColumnsButton />
        <GridToolbarFilterButton />
        <GridToolbarDensitySelector />
      </div>

      <GridToolbarQuickFilter debounceMs={300} />

      <button
        type="button"
        onClick={onResetColumns}
        style={{
          border: "1px solid rgba(0,0,0,.2)",
          background: "transparent",
          padding: "4px 8px",
          borderRadius: 6,
          cursor: "pointer",
        }}
        title="Restablecer columnas"
      >
        Restablecer columnas
      </button>
    </GridToolbarContainer>
  );
}

IngredientePPToolbar.propTypes = {
  onResetColumns: PropTypes.func,
};

/* ---------- Grid principal ---------- */
export default function GridIngredientePresentacionP({
  rows = [],
  selectedRow = null,
  setSelectedRow = () => {},
  loading = false,

  // paginación server-side (Estilo B)
  page = 0,
  rowsPerPage = 10,
  totalElements = 0,
  onPageChange = () => {},
  onRowsPerPageChange = () => {},
}) {
  /* ----- Definición de columnas (según DTO del backend / join del padre) ----- */
  const columns = useMemo(
    () => [
      {
        field: "id",
        headerName: "ID",
        width: 90,
        hideable: true,
      },
      {
        field: "nombreProducto",
        headerName: "Producto",
        flex: 1.4,
        minWidth: 200,
        valueGetter: (p) =>
          p?.row?.nombreProducto ??
          p?.row?.productoNombre ??
          "",
        hideable: true,
      },
      {
        field: "presentacionNombre",
        headerName: "Presentación",
        flex: 1.2,
        minWidth: 200,
        valueGetter: (p) =>
          p?.row?.presentacionNombre ??
          p?.row?.nombrePresentacionProducto ??
          "",
        hideable: true,
      },
      {
        field: "ingredienteNombre",
        headerName: "Ingrediente",
        flex: 1.2,
        minWidth: 200,
        valueGetter: (p) =>
          p?.row?.ingredienteNombre ??
          p?.row?.ingrediente?.nombreIngrediente ??
          "",
        hideable: true,
      },
      {
        field: "cantidad",
        headerName: "Cantidad",
        flex: 0.6,
        minWidth: 100,
        valueGetter: (p) => p?.row?.cantidad ?? "",
        hideable: true,
      },
      {
        field: "unidadNombre",
        headerName: "Unidad",
        flex: 0.8,
        minWidth: 120,
        valueGetter: (p) =>
          p?.row?.unidadNombre ??
          p?.row?.ingrediente?.nombreUnidad ??
          "",
        hideable: true,
      },
      {
        field: "estadoNombre",
        headerName: "Estado",
        flex: 0.7,
        minWidth: 120,
        valueGetter: (p) =>
          p?.row?.estadoNombre ??
          p?.row?.ingrediente?.nombreEstado ??
          (String(p?.row?.estadoId) === "1" ? "Activo" : "Inactivo"),
        hideable: true,
      },
    ],
    []
  );

  /* ----- Column visibility con persistencia en localStorage ----- */
  const [columnVisibilityModel, setColumnVisibilityModel] = useState({});

  useEffect(() => {
    try {
      const saved = JSON.parse(localStorage.getItem(LS_KEY) || "{}");
      if (saved && typeof saved === "object") {
        setColumnVisibilityModel(saved);
      }
    } catch {
      // ignore
    }
  }, []);

  const handleVisibilityChange = (model) => {
    setColumnVisibilityModel(model);
    try {
      localStorage.setItem(LS_KEY, JSON.stringify(model));
    } catch {
      // ignore
    }
  };

  const handleResetColumns = () => {
    localStorage.removeItem(LS_KEY);
    setColumnVisibilityModel({});
  };

  /* ----- Handlers de paginación (adaptados a Estilo B del padre) ----- */
  const handlePaginationModelChange = (model) => {
    const nextPage = model?.page ?? 0;
    const nextSize = model?.pageSize ?? rowsPerPage ?? 10;

    if (nextSize !== rowsPerPage) {
      // El padre espera algo tipo evento o valor, vamos a mandarle un "event-like"
      onRowsPerPageChange({ target: { value: nextSize } });
    } else if (nextPage !== page) {
      onPageChange(null, nextPage);
    }
  };

  return (
    <div style={{ width: "100%", height: 500 }}>
      <GridBase
        rows={Array.isArray(rows) ? rows : []}
        columns={columns}
        getRowId={(row) => row.id}
        loading={loading}
        disableRowSelectionOnClick
        disableColumnMenu
        rowSelectionModel={selectedRow?.id ? [selectedRow.id] : []}
        onRowClick={(params) => setSelectedRow(params.row)}
        columnVisibilityModel={columnVisibilityModel}
        onColumnVisibilityModelChange={handleVisibilityChange}
        slots={{ toolbar: IngredientePPToolbar }}
        slotProps={{ toolbar: { onResetColumns: handleResetColumns } }}
        paginationMode="server"
        rowCount={totalElements ?? 0}
        paginationModel={{ page, pageSize: rowsPerPage }}
        onPaginationModelChange={handlePaginationModelChange}
        pageSizeOptions={[5, 10, 15, 20, 50]}
      />
    </div>
  );
}

GridIngredientePresentacionP.propTypes = {
  rows: PropTypes.array,
  selectedRow: PropTypes.object,
  setSelectedRow: PropTypes.func,
  loading: PropTypes.bool,
  page: PropTypes.number,
  rowsPerPage: PropTypes.number,
  totalElements: PropTypes.number,
  onPageChange: PropTypes.func,
  onRowsPerPageChange: PropTypes.func,
};
