/*=============================================================================
Nombre del archivo : GridActionBar.jsx
Descripción        : Componente reutilizable para los botones de acción de CRUD
===============================================================================
CONTROL DE CAMBIOS
+------------+---------+----------------------+-----------------------------+
|   Fecha    | Versión |      Autor           | Descripción del cambio      |
+------------+---------+----------------------+-----------------------------+
| 2026-05-06 | 0.4.0   | Cesar Medina         | Creación del archivo.       |
+------------+---------+----------------------+-----------------------------+
=============================================================================*/
/**
 * Componente reusable de barra de acciones para grillas CRUD.
 * @module GridActionBar
 */
import React from "react";
import PropTypes from "prop-types";
import { Stack, Button, Box, Tooltip } from "@mui/material";
import { useTheme, alpha } from "@mui/material/styles";
import AddIcon from "@mui/icons-material/Add";
import ModeEditOutlineIcon from "@mui/icons-material/ModeEditOutline";
import DeleteOutlineIcon from "@mui/icons-material/DeleteOutline";
import FilterListIcon from "@mui/icons-material/FilterList";
import { useTranslation } from "react-i18next";

/**
 * Barra de acciones reusable para módulos CRUD.
 *
 * Centraliza las acciones principales de agregar, actualizar, eliminar y filtros,
 * manteniendo estilos consistentes, modo compacto responsive e integración con i18n.
 *
 * @param {Object} props Propiedades del componente.
 * @param {function} [props.onAdd] Acción principal de creación.
 * @param {function} [props.onUpdate] Acción de edición de la fila seleccionada.
 * @param {function} [props.onDelete] Acción de eliminación de la fila seleccionada.
 * @param {boolean} [props.canUpdate=false] Habilita o deshabilita el botón actualizar.
 * @param {boolean} [props.canDelete=false] Habilita o deshabilita el botón eliminar.
 * @param {*} [props.extraActions] Acciones extra que se muestran antes de agregar.
 * @param {function} [props.onFilters] Abre o activa el flujo de filtros.
 * @param {function} [props.onClearFilters] Limpia filtros activos.
 * @param {boolean} [props.hasActiveFilters=false] Indica si hay filtros activos para mostrar el botón limpiar.
 * @param {*} [props.rightActions] Acciones adicionales en el bloque secundario.
 * @param {Object} [props.labels] Sobrescritura puntual de etiquetas visibles.
 * @returns {JSX.Element}
 */
export default function GridActionBar({
  onAdd,
  onUpdate,
  onDelete,
  canAdd = true,
  canUpdate = false,
  canDelete = false,
  extraActions,
  onFilters,
  onClearFilters,
  hasActiveFilters = false,
  rightActions,
  labels,
}) {
  const theme = useTheme();
  const { t } = useTranslation();
  const isDark = theme.palette.mode === "dark";
  const resolvedLabels = {
    add: labels?.add ?? t("common.actions.add"),
    update: labels?.update ?? t("common.actions.update"),
    delete: labels?.delete ?? t("common.actions.delete"),
    filters: labels?.filters ?? t("common.actions.filters"),
    clear: labels?.clear ?? t("common.actions.clear"),
  };
  const softShadow = isDark
    ? "0 8px 20px rgba(0,0,0,0.24), 0 2px 8px rgba(0,0,0,0.16)"
    : "0 6px 18px rgba(23,63,57,0.08), 0 2px 6px rgba(0,0,0,0.06)";
  const softDangerShadow = isDark
    ? "0 8px 20px rgba(0,0,0,0.24), 0 2px 8px rgba(0,0,0,0.16)"
    : "0 6px 18px rgba(211,47,47,0.08), 0 2px 6px rgba(0,0,0,0.05)";

  const addButtonSx = {
    bgcolor: isDark ? "#173f39" : "#1d4d45",
    color: "#fff",
    px: { xs: 1.25, sm: 1.5, md: 2.5 },
    py: 1,
    borderRadius: 2,
    textTransform: "uppercase",
    fontWeight: 700,
    fontSize: { xs: 0, sm: 0, md: "0.75rem" },
    minWidth: { xs: 44, sm: 44, md: "auto" },
    boxShadow: softShadow,
    "&:hover": {
      bgcolor: isDark ? "#21534b" : "#173f39",
      boxShadow: softShadow,
    },
    "& .MuiButton-startIcon": {
      mr: { xs: 0, sm: 0, md: 1 },
      ml: 0,
    },
    "& .MuiButton-startIcon svg": { fontSize: 16 },
  };

  const editButtonSx = {
    bgcolor: isDark ? alpha("#2b6b60", 0.28) : "#d9e9e3",
    color: isDark ? "#e7f6f7" : "#173f39",
    px: { xs: 1.25, sm: 1.5, md: 2.5 },
    py: 1,
    borderRadius: 2,
    textTransform: "uppercase",
    fontWeight: 700,
    fontSize: { xs: 0, sm: 0, md: "0.75rem" },
    minWidth: { xs: 44, sm: 44, md: "auto" },
    boxShadow: softShadow,
    "&:hover": {
      bgcolor: isDark ? alpha("#2b6b60", 0.4) : "#cfe1da",
      boxShadow: softShadow,
    },
    "&.Mui-disabled": {
      color: isDark ? alpha("#e7f6f7", 0.38) : "#7f9790",
      bgcolor: isDark ? alpha("#2b6b60", 0.12) : "#edf3f0",
    },
    "& .MuiButton-startIcon": {
      mr: { xs: 0, sm: 0, md: 1 },
      ml: 0,
    },
    "& .MuiButton-startIcon svg": { fontSize: 16 },
  };

  const deleteButtonSx = {
    bgcolor: isDark ? alpha("#ffb4ab", 0.12) : "#fff0f0",
    color: isDark ? "#ffb4ab" : "#d32f2f",
    px: { xs: 1.25, sm: 1.5, md: 2.5 },
    py: 1,
    borderRadius: 2,
    textTransform: "uppercase",
    fontWeight: 700,
    fontSize: { xs: 0, sm: 0, md: "0.75rem" },
    minWidth: { xs: 44, sm: 44, md: "auto" },
    boxShadow: softDangerShadow,
    "&:hover": {
      bgcolor: isDark ? alpha("#ffb4ab", 0.18) : "#ffdede",
      boxShadow: softDangerShadow,
    },
    "&.Mui-disabled": {
      color: isDark ? alpha("#ffb4ab", 0.4) : "#f19999",
      bgcolor: isDark ? alpha("#ffb4ab", 0.08) : "#fff6f6",
    },
    "& .MuiButton-startIcon": {
      mr: { xs: 0, sm: 0, md: 1 },
      ml: 0,
    },
    "& .MuiButton-startIcon svg": { fontSize: 16 },
  };

  const extraActionsSx = {
    "& .MuiButton-root": {
      bgcolor: isDark ? alpha("#2b6b60", 0.28) : "#d9e9e3",
      color: isDark ? "#e7f6f7" : "#173f39",
      px: { xs: 1.25, sm: 1.5, md: 2.5 },
      py: 1,
      borderRadius: 2,
      textTransform: "uppercase",
      fontWeight: 700,
      fontSize: { xs: 0, sm: 0, md: "0.75rem" },
      minWidth: { xs: 44, sm: 44, md: "auto" },
      boxShadow: softShadow,
      "&:hover": {
        bgcolor: isDark ? alpha("#2b6b60", 0.4) : "#cfe1da",
        boxShadow: softShadow,
      },
      "&.Mui-disabled": {
        color: isDark ? alpha("#e7f6f7", 0.38) : "#7f9790",
        bgcolor: isDark ? alpha("#2b6b60", 0.12) : "#edf3f0",
      },
      "& .MuiButton-startIcon": {
        mr: { xs: 0, sm: 0, md: 1 },
        ml: 0,
      },
      "& .MuiButton-startIcon svg": { fontSize: 16 },
    },
  };

  const rightActionsSx = {
    display: "flex",
    justifyContent: "flex-end",
    gap: 1,
    ml: "auto",
    width: "auto",
    flexShrink: 0,
    "& .MuiButton-root": {
      bgcolor: isDark ? alpha("#2b6b60", 0.24) : "#dfeae6",
      color: isDark ? "#e7f6f7" : "#173f39",
      px: { xs: 1.25, sm: 1.5, md: 2.25 },
      py: 1,
      borderRadius: 2,
      textTransform: "uppercase",
      fontWeight: 700,
      fontSize: { xs: 0, sm: 0, md: "0.75rem" },
      minWidth: { xs: 44, sm: 44, md: "auto" },
      boxShadow: softShadow,
      "&:hover": {
        bgcolor: isDark ? alpha("#2b6b60", 0.36) : "#d3e2dc",
        boxShadow: softShadow,
      },
      "& .MuiButton-startIcon": {
        mr: { xs: 0, sm: 0, md: 1 },
        ml: 0,
      },
      "& .MuiButton-startIcon svg": { fontSize: 16 },
    },
  };

  const rightContent = (
    <>
      {onFilters ? (
        <Tooltip title={resolvedLabels.filters}>
          <Button onClick={onFilters} startIcon={<FilterListIcon />}>
            {resolvedLabels.filters}
          </Button>
        </Tooltip>
      ) : null}
      {hasActiveFilters && onClearFilters ? (
        <Tooltip title={resolvedLabels.clear}>
          <Button onClick={onClearFilters}>
            {resolvedLabels.clear}
          </Button>
        </Tooltip>
      ) : null}
      {rightActions}
    </>
  );

  return (
    <Stack
      direction="row"
      spacing={{ xs: 1, md: 2 }}
      alignItems="center"
      sx={{
        mb: 2,
        width: "100%",
        pt: 0.75,
        pb: 2.0,
        px: 0.25,
        flexWrap: "nowrap",
        overflowX: "auto",
        overflowY: "visible",
        scrollbarWidth: "thin",
        minWidth: 0,
      }}
    >
      {(onFilters || (hasActiveFilters && onClearFilters) || rightActions) ? (
        <Box sx={rightActionsSx}>{rightContent}</Box>
      ) : null}
      <Box sx={{ flex: 1, minWidth: { xs: 12, md: 24 } }} />
      <Stack
        direction="row"
        spacing={{ xs: 1, md: 2 }}
        sx={{
          flexWrap: "nowrap",
          flexShrink: 0,
          minWidth: "max-content",
        }}
      >
        {extraActions ? <Box sx={extraActionsSx}>{extraActions}</Box> : null}
        <Tooltip title={resolvedLabels.add}>
          <Button
            onClick={onAdd}
            startIcon={<AddIcon />}
            disabled={!canAdd}
            sx={{
              ...addButtonSx,
              "&.Mui-disabled": {
                color: isDark ? alpha("#e7f6f7", 0.38) : "#7f9790",
                bgcolor: isDark ? alpha("#173f39", 0.12) : "#edf3f0",
              }
            }}
          >
            {resolvedLabels.add}
          </Button>
        </Tooltip>
        <Tooltip title={resolvedLabels.update}>
          <Button
            onClick={onUpdate}
            startIcon={<ModeEditOutlineIcon />}
            disabled={!canUpdate}
            sx={editButtonSx}
          >
            {resolvedLabels.update}
          </Button>
        </Tooltip>
        <Tooltip title={resolvedLabels.delete}>
          <Button
            onClick={onDelete}
            startIcon={<DeleteOutlineIcon />}
            disabled={!canDelete}
            sx={deleteButtonSx}
          >
            {resolvedLabels.delete}
          </Button>
        </Tooltip>
      </Stack>
    </Stack>
  );
}

GridActionBar.propTypes = {
  onAdd: PropTypes.func,
  onUpdate: PropTypes.func,
  onDelete: PropTypes.func,
  canAdd: PropTypes.bool,
  canUpdate: PropTypes.bool,
  canDelete: PropTypes.bool,
  extraActions: PropTypes.node,
  onFilters: PropTypes.func,
  onClearFilters: PropTypes.func,
  hasActiveFilters: PropTypes.bool,
  rightActions: PropTypes.node,
  labels: PropTypes.shape({
    add: PropTypes.string,
    update: PropTypes.string,
    delete: PropTypes.string,
    filters: PropTypes.string,
    clear: PropTypes.string,
  }),
};
