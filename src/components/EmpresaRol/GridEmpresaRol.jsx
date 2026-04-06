import React from "react";
import PropTypes from "prop-types";
import { Chip } from "@mui/material";
import { useTheme, alpha } from "@mui/material/styles";
import AppDataGrid from "../common/AppDataGrid.jsx";

export default function GridEmpresaRol({
  rows,
  loading,
  selectedRow,
  setSelectedRow,
  isSystemAdmin = false,
}) {
const theme = useTheme();
const isDark = theme.palette.mode === "dark";
  const estadosMap = {
    1: "Activo",
    0: "Inactivo",
    23: "Activo",
    24: "Inactivo",
  };

  const columns = [
    { field: "id", headerName: "ID", width: 80 },
    ...(isSystemAdmin
      ? [{ field: "empresaNombre", headerName: "Empresa", flex: 1 }]
      : []),

    { field: "rolNombre", headerName: "Rol", flex: 1 },
    {
  field: "permisos",
  headerName: "Permisos",
  flex: 2,
  renderCell: (params) => {
    const permisos = params.row.permisos;

    if (!Array.isArray(permisos) || permisos.length === 0) {
      return (
        <span style={{ color: theme.palette.text.secondary, fontStyle: "italic" }}>
          Sin permisos
        </span>
      );
    }

    const visibles = permisos.slice(0, 2);

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
            fontSize: "9px",
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
              fontSize: "10px",
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
  <AppDataGrid
    rows={rows}
    columns={columns}
    loading={loading}
    selectedRow={selectedRow}
    setSelectedRow={setSelectedRow}
    pageSizeOptions={[5, 10, 20, 50]}
    containerSx={{ borderRadius: 4 }}
    sx={{
      backgroundColor: theme.palette.background.paper,
      color: theme.palette.text.primary,
      border: `1px solid ${theme.palette.divider}`,
      "& .MuiDataGrid-columnHeaders": {
        backgroundColor: alpha(theme.palette.primary.main, isDark ? 0.15 : 0.05),
        color: theme.palette.text.primary,
        fontWeight: 600,
      },
    }}
  />
);
}

GridEmpresaRol.propTypes = {
  rows: PropTypes.array,
  loading: PropTypes.bool,
  selectedRow: PropTypes.object,
  setSelectedRow: PropTypes.func,
  isSystemAdmin: PropTypes.bool,
};
