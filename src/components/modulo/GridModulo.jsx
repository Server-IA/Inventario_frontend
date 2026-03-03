import React, { useMemo, useState } from "react";
import { Switch, Box, Typography } from "@mui/material";
import { useTheme } from "@mui/material/styles";
import { DataGrid } from "@mui/x-data-grid";
import * as MuiIcons from "@mui/icons-material";
import axios from "../axiosConfig";
import GridBase from "../dashboard/GridBase";

export default function GridModulo({
  modulos = {},
  setSelectedRow,
  authHeaders,
  setMessage,
  reloadData,
  loading = false,
}) {

  const theme = useTheme();
  const isDark = theme.palette.mode === "dark";

  const [paginationModel, setPaginationModel] = useState({
    page: 0,
    pageSize: 5,
  });

  const handleToggleRequerido = async (row) => {
    const nuevoValor = !row.requerido;

    try {
      await axios.patch(
        `/v2/modulos/${row.id}`,
        { requerido: nuevoValor },
        authHeaders
      );

      setMessage?.({
        open: true,
        severity: "success",
        text: "Obligatoriedad actualizada correctamente.",
      });

      reloadData?.(paginationModel.page, paginationModel.pageSize);

    } catch {
      setMessage?.({
        open: true,
        severity: "error",
        text: "Error al actualizar obligatoriedad.",
      });
    }
  };

  const columns = useMemo(() => [

    { field: "id", headerName: "ID", width: 80 },
    { field: "nombre", headerName: "Nombre", width: 180 },
    { field: "nombreId", headerName: "Acrónimo", width: 140 },
    { field: "url", headerName: "URL", width: 220 },
    { field: "descripcion", headerName: "Descripción", flex: 1, minWidth: 220 },

    {
      field: "icon",
      headerName: "Icono",
      width: 120,
      sortable: false,
      renderCell: (params) => {
        const IconComponent = MuiIcons[params.value];
        return IconComponent ? (
          <IconComponent sx={{ fontSize: 22 }} />
        ) : null;
      },
    },

    { field: "subSistema", headerName: "SubSistema", width: 160 },
    { field: "tipoModulo", headerName: "Tipo Módulo", width: 150 },
    { field: "tipoAplicacion", headerName: "Tipo Aplicación", width: 170 },

    {
      field: "requerido",
      headerName: "Requerido",
      width: 180,
      sortable: false,
      renderCell: (params) => {
        const activo = Boolean(params.row.requerido);

        return (
          <Box display="flex" alignItems="center" gap={1}>
            <Switch
              checked={activo}
              onChange={() => handleToggleRequerido(params.row)}
            />
            <Typography>
              {activo ? "Sí" : "No"}
            </Typography>
          </Box>
        );
      },
    },

    { field: "estado", headerName: "Estado", width: 120 },

  ], [isDark, theme]);

  return (
    <div style={{ width: "100%" }}>
      <GridBase
        loading={loading}
        rows={modulos?.content || []}
        columns={columns}
        getRowId={(row) => row.id}
        onRowClick={(params) => setSelectedRow?.(params.row)}

        paginationMode="server"
        rowCount={modulos?.page?.totalElements || 0}

        paginationModel={paginationModel}
        onPaginationModelChange={(model) => {
          setPaginationModel(model);
          reloadData?.(model.page, model.pageSize);
        }}

        pageSizeOptions={[5, 10, 20, 50]}
        disableRowSelectionOnClick
        autoHeight
      />
    </div>
  );
}
