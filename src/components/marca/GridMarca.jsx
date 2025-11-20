import React, { useMemo } from "react";
import PropTypes from "prop-types";
import {
  DataGrid,
  GridToolbarContainer,
  GridToolbarFilterButton,
} from "@mui/x-data-grid";
import { Box } from "@mui/material";

/**
 * GridMarca
 * - Usa paginación server con paginationModel (page, pageSize)
 */
export default function GridMarca({
  // Datos
  rows = [],

  // Selección
  selectedRow = null,
  setSelectedRow,

  // Paginación controlada (server-side)
  paginationModel,        // { page, pageSize }
  setPaginationModel,     // (model) => void
  rowCount = 0,           // total en servidor
  loading = false,        // spinner
}) {
  /* ---------- Columnas ---------- */
  const columns = useMemo(
    () => [
      { field: "id", headerName: "ID", width: 90, type: "number" },
      { field: "nombre", headerName: "Nombre", width: 200, type: "string" },
      {
        field: "descripcion",
        headerName: "Descripción",
        flex: 1,
        minWidth: 240,
        type: "string",
      },
      {
        field: "estadoId",
        headerName: "Estado",
        width: 140,
        type: "number",
        valueGetter: (params) =>
          params?.row?.estado?.name ??
          params?.row?.estado?.nombre ??
          (String(params?.row?.estadoId) === "1" ? "Activo" : "Inactivo"),
      },
    ],
    []
  );

  /* ---------- Toolbar (filtros) ---------- */
  function CustomToolbar() {
    return (
      <GridToolbarContainer>
        <GridToolbarFilterButton />
      </GridToolbarContainer>
    );
  }

  return (
    <Box sx={{ width: "100%", mt: 2 }}>
      <DataGrid
        rows={Array.isArray(rows) ? rows : []}
        columns={columns}
        getRowId={(row) => row.id}

        // Selección
        onRowClick={(params) => setSelectedRow?.(params.row)}
        rowSelectionModel={selectedRow?.id ? [selectedRow.id] : []}
        disableRowSelectionOnClick

        // Toolbar
        slots={{ toolbar: CustomToolbar }}

        // 🔹 Paginación server-side
        paginationMode="server"
        paginationModel={{
          page: paginationModel?.page ?? 0,
          pageSize: paginationModel?.pageSize ?? 5,
        }}
        onPaginationModelChange={(model) => {
          const next = {
            page: model.page ?? 0,
            pageSize: model.pageSize ?? 5,
          };
          setPaginationModel?.(next);
        }}
        pageSizeOptions={[5, 10, 15, 20]}   // 👈 aquí tus 5,10,15,20
        rowCount={rowCount}

        loading={loading}
        autoHeight
      />
    </Box>
  );
}

GridMarca.propTypes = {
  rows: PropTypes.array,
  selectedRow: PropTypes.object,
  setSelectedRow: PropTypes.func,
  paginationModel: PropTypes.shape({
    page: PropTypes.number,
    pageSize: PropTypes.number,
  }),
  setPaginationModel: PropTypes.func,
  rowCount: PropTypes.number,
  loading: PropTypes.bool,
};
