// src/components/Ingrediente/GridIngrediente.jsx
import React, { useMemo } from "react";
import PropTypes from "prop-types";
import { DataGrid } from "@mui/x-data-grid";
import { Box } from "@mui/material";
import GridBase from "../dashboard/GridBase";

export default function GridIngrediente({
  // Datos
  rows = [],

  // Selección
  selectedRow = null,
  setSelectedRow,

  // Paginación (server-side)
  paginationModel,        // { page, pageSize }
  setPaginationModel,     // (model) => void
  rowCount,               // total en servidor
  loading = false,
}) {
  const columns = useMemo(
    () => [
      { field: "id", headerName: "ID", width: 90, type: "number" },
      { field: "nombre", headerName: "Nombre", width: 220 },
      { field: "descripcion", headerName: "Descripción", flex: 1, minWidth: 280 },
      {
        field: "estadoId",
        headerName: "Estado",
        width: 140,
        valueGetter: (p) =>
          p?.row?.estado?.name ??
          p?.row?.estado?.nombre ??
          (String(p?.row?.estadoId) === "1" ? "Activo" : "Inactivo"),
      },
    ],
    []
  );

  const serverPagination = Boolean(
    paginationModel && setPaginationModel && typeof rowCount === "number"
  );

  return (
    <Box sx={{ width: "100%", mt: 2 }}>
      <GridBase
        rows={Array.isArray(rows) ? rows : []}
        columns={columns}
        getRowId={(row) => row.id}

        // Selección controlada
        onRowClick={(params) => setSelectedRow?.(params.row)}
        rowSelectionModel={selectedRow?.id ? [selectedRow.id] : []}
        disableRowSelectionOnClick

        // -------- Paginación --------
        paginationMode={serverPagination ? "server" : "client"}
        loading={loading}
        {...(serverPagination
          ? {
              // ----- Server controlled -----
              paginationModel: {
                page: paginationModel.page ?? 0,
                pageSize: paginationModel.pageSize ?? 10,
              },
              onPaginationModelChange: (model) => {
                // El padre hará el fetch cuando cambie el modelo
                setPaginationModel?.({
                  page: model.page ?? 0,
                  pageSize: model.pageSize ?? 10,
                });
              },
              rowCount,
              pageSizeOptions: [5, 10, 15, 20, 50],
            }
          : {
              // ----- Client fallback -----
              pageSizeOptions: [5, 10, 15, 20, 50],
              initialState: {
                pagination: { paginationModel: { page: 0, pageSize: 5 } },
              },
            })}
        autoHeight
      />
    </Box>
  );
}

GridIngrediente.propTypes = {
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
