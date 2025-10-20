import React, { useMemo } from "react";
import PropTypes from "prop-types";
import { Box } from "@mui/material";
import {
  DataGrid,
  GridToolbarContainer,
  GridToolbarFilterButton,
  esES,
} from "@mui/x-data-grid";

function Toolbar() {
  return (
    <GridToolbarContainer>
      <GridToolbarFilterButton />
    </GridToolbarContainer>
  );
}

export default function GridArticuloOrdenCompra({
  items = [],
  selectedRow = null,
  setSelectedRow,
  paginationModel,
  setPaginationModel,
  rowCount,
  loading = false,
  presentacionesMap = {},
}) {
  const columns = useMemo(() => {
    const presentacionNameGetter = ({ row }) => {
      const presId =
        row?.presentacionProductoId ??
        row?.presentacion_id ??
        row?.presentacionId ??
        row?.presentacion?.id ??
        null;
      const directName =
        row?.presentacionNombre ??
        row?.presentacion_name ??
        row?.presentacion?.nombre ??
        row?.presentacion?.name;
      return (
        directName ?? (presId != null ? presentacionesMap[Number(presId)] : "") ?? ""
      );
    };

    return [
      { field: "id", headerName: "ID", width: 90, type: "number" },
      { field: "cantidad", headerName: "Cantidad", width: 120, type: "number" },
      { field: "precio", headerName: "Precio", width: 120, type: "number" },
      {
        field: "ordenCompraId",
        headerName: "Orden Compra",
        width: 150,
        type: "number",
      },
      {
        field: "presentacion",
        headerName: "Presentación",
        width: 240,
        valueGetter: presentacionNameGetter,
      },
      {
        field: "estadoId",
        headerName: "Estado",
        width: 130,
        valueGetter: ({ row }) =>
          row?.estado?.nombre ??
          row?.estado?.name ??
          (String(row?.estadoId) === "1" ? "Activo" : "Inactivo"),
      },
    ];
  }, [presentacionesMap]);

  const serverPagination =
    paginationModel && setPaginationModel && typeof rowCount === "number";

  return (
    <Box sx={{ width: "100%", overflowX: "auto" }}>
      <DataGrid
        rows={Array.isArray(items) ? items : []}
        columns={columns}
        getRowId={(row) => row.id}
        onRowClick={(params) => setSelectedRow?.(params.row)}
        rowSelectionModel={selectedRow?.id ? [selectedRow.id] : []}
        disableRowSelectionOnClick
        localeText={esES.components.MuiDataGrid.defaultProps.localeText}
        pagination
        pageSizeOptions={[5, 10, 20, 50]}
        components={{ Toolbar }}
        slots={{ toolbar: Toolbar }}
        paginationMode={serverPagination ? "server" : "client"}
        loading={loading}
        {...(serverPagination
          ? {
              rowCount: Math.max(
                Number(rowCount ?? 0),
                Array.isArray(items) ? items.length : 0
              ),
              paginationModel: {
                page: paginationModel.page ?? 0,
                pageSize: paginationModel.pageSize ?? paginationModel.size ?? 10,
              },
              onPaginationModelChange: (model) => {
                const next = {
                  page: model.page ?? 0,
                  size: model.pageSize ?? model.size ?? 10,
                };
                setPaginationModel?.(next);
              },
            }
          : {
              initialState: {
                pagination: { paginationModel: { page: 0, pageSize: 5 } },
              },
            })}
        autoHeight
        sx={{ minWidth: 720 }}
      />
    </Box>
  );
}

GridArticuloOrdenCompra.propTypes = {
  items: PropTypes.array,
  selectedRow: PropTypes.object,
  setSelectedRow: PropTypes.func,
  paginationModel: PropTypes.object,
  setPaginationModel: PropTypes.func,
  rowCount: PropTypes.number,
  loading: PropTypes.bool,
  presentacionesMap: PropTypes.object,
};
