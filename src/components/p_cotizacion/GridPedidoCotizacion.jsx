import React, { useState } from "react";
import PropTypes from "prop-types";
import { DataGrid } from "@mui/x-data-grid";
import { Box } from "@mui/material";

export default function GridPedidoCotizacion({
  rows = [],
  setSelectedRow = () => {},
}) {
  const [paginationModel, setPaginationModel] = useState({
    pageSize: 5,
    page: 0,
  });

  const columns = [
    { field: "id", headerName: "ID", width: 80 },
    { field: "descripcion", headerName: "Descripción", width: 250 },
    { field: "archivo", headerName: "Archivo", width: 220 },
    {
      field: "pedidoId",
      headerName: "Pedido",
      width: 150,
      valueGetter: (params) =>
        params.row?.pedido?.nombre ||
        params.row?.pedido?.descripcion ||
        params.row?.pedidoId ||
        "",
    },
    {
      field: "proveedorId",
      headerName: "Proveedor",
      width: 200,
      valueGetter: (params) =>
        params.row?.proveedor?.nombre ||
        params.row?.proveedor?.razonSocial ||
        params.row?.proveedorId ||
        "",
    },
    {
      field: "estado",
      headerName: "Estado",
      width: 130,
      valueGetter: (params) =>
        params.row?.estado?.nombre ||
        (params.row?.estadoId === 1
          ? "Activo"
          : params.row?.estadoId === 2
          ? "Inactivo"
          : ""),
    },
  ];

  return (
    <Box sx={{ width: "100%", mt: 2 }}>
      <DataGrid
        autoHeight
        rows={rows}
        columns={columns}
        getRowId={(r) => r.id}
        onRowClick={(p) => setSelectedRow(p.row)}
        disableRowSelectionOnClick
        paginationModel={paginationModel}
        onPaginationModelChange={setPaginationModel}
        pageSizeOptions={[5, 10, 25, 50]}
      />
    </Box>
  );
}

GridPedidoCotizacion.propTypes = {
  rows: PropTypes.array.isRequired,
  setSelectedRow: PropTypes.func.isRequired,
};
