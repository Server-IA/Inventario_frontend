import React from "react";
import PropTypes from "prop-types";
import { Box } from "@mui/material";
import AppDataGrid from "../common/AppDataGrid";

export default function GridCategoriaEstado({
  rows = [],
  selectedRow = null,
  setSelectedRow = () => {},
}) {
  const columns = [
    { field: "id", headerName: "ID", width: 90 },
    { field: "nombre", headerName: "Nombre", width: 250 },
    { field: "descripcion", headerName: "Descripcion", width: 400 },
  ];

  return (
    <Box sx={{ width: "100%", mt: 2 }}>
      <AppDataGrid
        rows={Array.isArray(rows) ? rows : []}
        columns={columns}
        getRowId={(r) => r.id}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
        pageSizeOptions={[5, 10, 25, 50]}
        autoHeight
        containerSx={{ borderRadius: 4 }}
      />
    </Box>
  );
}

GridCategoriaEstado.propTypes = {
  rows: PropTypes.array,
  selectedRow: PropTypes.object,
  setSelectedRow: PropTypes.func,
};
