import React, { useMemo } from "react";
import PropTypes from "prop-types";
import { DataGrid } from "@mui/x-data-grid";
import { Box } from "@mui/material";

export default function GridUnidad({
  rows = [],
  selectedRow = null,
  setSelectedRow,
  loading = false,
}) {
  const columns = useMemo(
    () => [
      { field: "id", headerName: "ID", width: 80 },

      {
        field: "tipoUnidadNombre",
        headerName: "Tipo de unidad",
        width: 180,
        valueGetter: (params) =>
          params.row?.tipoUnidadNombre ??
          params.row?.tipoUnidad?.nombre ??
          params.row?.tipoUnidad?.name ??
          params.row?.tipoUnidadId ??
          "—",
      },

      { field: "nombre", headerName: "Nombre", width: 200 },

      {
        field: "descripcion",
        headerName: "Descripción",
        flex: 1,
        minWidth: 260,
      },

      {
        field: "estadoNombre",
        headerName: "Estado",
        width: 140,
        valueGetter: (params) =>
          params.row?.estadoNombre ??
          params.row?.estado?.name ??
          params.row?.estado?.nombre ??
          (String(params.row?.estadoId) === "1" ? "Activo" : "Inactivo"),
      },
    ],
    []
  );

  return (
    <Box sx={{ width: "100%", mt: 2 }}>
      <DataGrid
        rows={Array.isArray(rows) ? rows : []}
        columns={columns}
        getRowId={(row) => row.id}

        loading={loading}

        // selección
        onRowClick={(params) => setSelectedRow?.(params.row)}
        rowSelectionModel={selectedRow?.id ? [selectedRow.id] : []}
        disableRowSelectionOnClick

        // paginación client-side simple
        pageSizeOptions={[5, 10, 20, 50]}
        initialState={{
          pagination: { paginationModel: { page: 0, pageSize: 10 } },
        }}

        autoHeight
      />
    </Box>
  );
}

GridUnidad.propTypes = {
  rows: PropTypes.array,
  selectedRow: PropTypes.object,
  setSelectedRow: PropTypes.func,
  loading: PropTypes.bool,
};
