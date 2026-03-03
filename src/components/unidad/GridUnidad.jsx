import React, { useMemo } from "react";
import PropTypes from "prop-types";
import { DataGrid } from "@mui/x-data-grid";
import { Box } from "@mui/material";
import GridBase from "../dashboard/GridBase";

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
        flex: 1,
        minWidth: 160,
        valueGetter: (params) =>
          params.row?.tipoUnidadNombre ||
          params.row?.tipoUnidad?.nombre ||
          "",
      },

      {
        field: "nombre",
        headerName: "Nombre",
        flex: 1,
        minWidth: 160,
      },

      {
        field: "descripcion",
        headerName: "Descripción",
        flex: 1,
        minWidth: 200,
      },

      {
        field: "estadoNombre",
        headerName: "Estado",
        width: 120,
        valueGetter: (params) =>
          params.row?.estadoNombre ??
          params.row?.estado?.nombre ??
          (String(params.row?.estadoId) === "1" ? "Activo" : "Inactivo"),
      },
    ],
    []
  );

  const selectedId = selectedRow?.id ?? 0;

  return (
    <Box sx={{ mt: 2 }}>
      <GridBase
        rows={Array.isArray(rows) ? rows : []}
        columns={columns}
        getRowId={(row) => row.id}
        rowSelectionModel={selectedId ? [selectedId] : []}
        onRowClick={(params) => {
          if (typeof setSelectedRow === "function") {
            setSelectedRow(params.row);
          }
        }}
        loading={loading}
        disableRowSelectionOnClick={false}
        autoHeight
        pageSizeOptions={[5, 10, 20, 50]}
        initialState={{
          pagination: {
            paginationModel: { page: 0, pageSize: 10 },
          },
        }}
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
