// src/components/estado/GridEstado.jsx
import React, { useState } from "react";
import PropTypes from "prop-types";
import { DataGrid } from "@mui/x-data-grid";
import { Box } from "@mui/material";

export default function GridEstado({ rows = [], setSelectedRow = () => {} }) {
  const [paginationModel, setPaginationModel] = useState({
    pageSize: 5,
    page: 0,
  });

  const columns = [
    { field: "id", headerName: "ID", width: 80 },
    { field: "nombre", headerName: "Nombre", width: 220 },
    { field: "acronimo", headerName: "Acrónimo", width: 120 },
    { field: "descripcion", headerName: "Descripción", width: 350 },
    {
      field: "categoriaNombre",
      headerName: "Categoría",
      width: 200,
      valueGetter: (params) => params.row?.estadoCategoria?.nombre || "",
    },
    {
      field: "categoriaDescripcion",
      headerName: "Desc. categoría",
      width: 350,
      valueGetter: (params) =>
        params.row?.estadoCategoria?.descripcion || "",
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

GridEstado.propTypes = {
  rows: PropTypes.array.isRequired,
  setSelectedRow: PropTypes.func.isRequired,
};
