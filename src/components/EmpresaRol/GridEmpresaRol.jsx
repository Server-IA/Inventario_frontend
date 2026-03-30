import React from "react";
import { DataGrid } from "@mui/x-data-grid";
import { Chip } from "@mui/material";
import { useTheme, alpha } from "@mui/material/styles";

export default function GridEmpresaRol({
  rows,
  loading,
  selectedRow,
  setSelectedRow,
}) {
const theme = useTheme();
const isDark = theme.palette.mode === "dark";
  const columns = [
    { field: "rolNombre", headerName: "Rol", flex: 1 },
    {
  field: "permisos",
  headerName: "Permisos",
  flex: 2,
  renderCell: (params) => {
  const permisos = params.row.permisos;

  if (params.row.permisosError) {
    return (
      <span style={{ color: "red", fontStyle: "italic" }}>
        Error cargando permisos
      </span>
    );
  }

  if (!Array.isArray(permisos) || permisos.length === 0) {
    return (
      <span style={{ color: theme.palette.text.secondary, fontStyle: "italic" }}>
        Sin permisos
      </span>
    );
  }


    const visibles = permisos.slice(0, 3);

    return (
      <div
        style={{
          display: "flex",
          flexWrap: "wrap",
          gap: "6px",
          maxWidth: "100%",
        }}
      >
        {visibles.map((permiso) => (
          <Chip
          key={permiso.id}
          label={permiso.nombre}
          size="small"
          sx={{
            fontSize: "11px",
            fontWeight: 500,

            backgroundColor: isDark
              ? alpha(theme.palette.primary.light, 0.25)
              : alpha(theme.palette.primary.main, 0.15),

            color: isDark
              ? theme.palette.primary.light
              : theme.palette.primary.main,

            border: `1px solid ${
              isDark
                ? theme.palette.primary.light
                : theme.palette.primary.main
            }`,
          }}
        />
        ))}

        {permisos.length > 3 && (
          <span
            style={{
              fontSize: "11px",
              color: theme.palette.text.secondary,
              alignSelf: "center",
            }}
          >
            +{permisos.length - 3} más
          </span>
            )}
          </div>
        );
      }
    },
    {
        field: "estadoNombre",
        headerName: "Estado",
        width: 160,
        valueGetter: (params) =>
          params.row.estadoNombre ??
          estadosMap[params.row.estadoId] ??
          params.row.estadoId ??
          "",
      }
  ];

return (
  <DataGrid
    rows={rows}
    columns={columns}
    loading={loading}
    autoHeight
    pageSizeOptions={[5, 10, 20, 50]}
    initialState={{
      pagination: {
        paginationModel: { pageSize: 5, page: 0 },
      },
    }}
    getRowId={(row) => row.id}
    onRowClick={(params) => setSelectedRow(params.row)}
    sx={{
      backgroundColor: theme.palette.background.paper,
      color: theme.palette.text.primary,
      border: `1px solid ${theme.palette.divider}`,

      "& .MuiDataGrid-columnHeaders": {
        backgroundColor: alpha(
          theme.palette.primary.main,
          isDark ? 0.15 : 0.05
        ),
        color: theme.palette.text.primary,
        fontWeight: 600,
      },

      "& .MuiDataGrid-row": {
        borderBottom: `1px solid ${theme.palette.divider}`,
      },

      "& .MuiDataGrid-cell": {
        borderBottom: `1px solid ${theme.palette.divider}`,
      },

      "& .MuiDataGrid-row:hover": {
        backgroundColor: alpha(
          theme.palette.primary.main,
          isDark ? 0.08 : 0.04
        ),
      },
    }}
  />
);
}