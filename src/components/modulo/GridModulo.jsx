import React, { useMemo } from "react";
import { DataGrid } from "@mui/x-data-grid";

export default function GridModulo({
  modulos = [],
  setSelectedRow,
}) {

  const columns = useMemo(() => [

    { field: "id", headerName: "ID", width: 80 },

    { field: "nombre", headerName: "Nombre", width: 180 },

    { field: "nombreId", headerName: "Acrónimo", width: 140 },

    { field: "url", headerName: "URL", width: 220 },

    { field: "descripcion", headerName: "Descripción", flex: 1, minWidth: 220 },

    { field: "icon", headerName: "Icono", width: 160 },

    { field: "subSistema", headerName: "SubSistema", width: 160 },

    { field: "tipoModulo", headerName: "Tipo Módulo", width: 140 },

    { field: "tipoAplicacion", headerName: "Tipo Aplicación", width: 150 },

    {
      field: "roles",
      headerName: "Roles",
      width: 280,
      valueGetter: (params) =>
        Array.isArray(params.row.roles)
          ? params.row.roles.join(", ")
          : "",
    },

    {
      field: "requerido",
      headerName: "Requerido",
      width: 120,
      valueGetter: (params) =>
        params.row.requerido ? "Sí" : "No",
    },

    { field: "estado", headerName: "Estado", width: 120 },

  ], []);

  return (
    <div style={{ width: "100%" }}>
      <DataGrid
        rows={Array.isArray(modulos) ? modulos : []}
        columns={columns}
        getRowId={(row) => row.id}
        onRowClick={(params) => setSelectedRow?.(params.row)}
        disableRowSelectionOnClick
        pageSizeOptions={[5, 10, 20]}
        initialState={{
          pagination: {
            paginationModel: { page: 0, pageSize: 5 },
          },
        }}
        autoHeight
      />
    </div>
  );
}
