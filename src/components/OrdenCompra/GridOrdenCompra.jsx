import React, { useMemo } from "react";
import PropTypes from "prop-types";
import { Box } from "@mui/material";
import {
  DataGrid,
  GridToolbarContainer,
  GridToolbarFilterButton,
  esES,
} from "@mui/x-data-grid";

function CustomToolbar() {
  return (
    <GridToolbarContainer>
      <GridToolbarFilterButton />
    </GridToolbarContainer>
  );
}

export default function GridOrdenCompra({
  ordenes = [],
  rowCount,
  loading = false,
  paginationModel,
  setPaginationModel,
  sortModel,
  setSortModel,
  setFilterModel,
  setSelectedRow,
  proveedoresMap = {},
}) {
  const safeDateTime = (val) => (val ? new Date(val).toLocaleString() : "");

  const columns = useMemo(() => {
    const proveedorValueGetter = ({ row }) => {
      const provId =
        row?.proveedorId ??
        row?.proveedor_id ??
        row?.proveedorIdFk ??
        row?.proveedor?.id ??
        null;
      const provName =
        row?.proveedorName ??
        row?.proveedor_name ??
        row?.proveedor?.name ??
        (provId != null ? proveedoresMap[Number(provId)] : undefined);
      return provName ?? String(provId ?? "");
    };

    return [
      { field: "id", headerName: "ID", width: 90, type: "number" },
      {
        field: "fechaHora",
        headerName: "Fecha y Hora",
        width: 200,
        valueGetter: ({ row }) => safeDateTime(row?.fechaHora),
      },
      { field: "pedidoId", headerName: "Pedido", width: 120, type: "number" },
      {
        field: "proveedor",
        headerName: "Proveedor",
        width: 240,
        valueGetter: proveedorValueGetter,
      },
      { field: "descripcion", headerName: "Descripción", flex: 1, minWidth: 260 },
      {
        field: "estadoId",
        headerName: "Estado",
        width: 140,
        valueGetter: ({ row }) =>
          row?.estado?.nombre ??
          row?.estado?.name ??
          (String(row?.estadoId) === "1" ? "Activo" : "Inactivo"),
      },
    ];
  }, [proveedoresMap]);

  const serverPagination =
    paginationModel && setPaginationModel && typeof rowCount === "number";

  return (
    <Box sx={{ width: "100%", overflowX: "auto" }}>
      <DataGrid
        rows={Array.isArray(ordenes) ? ordenes : []}
        columns={columns}
        getRowId={(row) => row.id}
        onRowClick={(params) => setSelectedRow?.(params.row)}
        disableRowSelectionOnClick
        localeText={esES.components.MuiDataGrid.defaultProps.localeText}
        pagination
        pageSizeOptions={[5, 10, 20, 50]}
        components={{ Toolbar: CustomToolbar }}
        slots={{ toolbar: CustomToolbar }}
        paginationMode={serverPagination ? "server" : "client"}
        loading={loading}
        {...(serverPagination
          ? {
              rowCount: Math.max(
                Number(rowCount ?? 0),
                Array.isArray(ordenes) ? ordenes.length : 0
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
                pagination: { paginationModel: { page: 0, pageSize: 10 } },
              },
            })}
        sortingMode={setSortModel ? "server" : "client"}
        sortModel={sortModel}
        onSortModelChange={setSortModel}
        filterMode={setFilterModel ? "server" : "client"}
        onFilterModelChange={setFilterModel}
        autoHeight
        sx={{ minWidth: 720 }}
      />
    </Box>
  );
}

GridOrdenCompra.propTypes = {
  ordenes: PropTypes.array,
  rowCount: PropTypes.number,
  loading: PropTypes.bool,
  paginationModel: PropTypes.object,
  setPaginationModel: PropTypes.func,
  sortModel: PropTypes.array,
  setSortModel: PropTypes.func,
  setFilterModel: PropTypes.func,
  setSelectedRow: PropTypes.func.isRequired,
  proveedoresMap: PropTypes.object,
};
