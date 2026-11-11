/*=============================================================================
 Nombre del archivo : FiltroTipoCostoIndirecto.jsx
 Descripcion        : Modal de filtros para el listado de Tipos de Costos Indirectos.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-10-16 | 0.4.0   | Jeisson Sanchez      | Creación del modal de filtros para HU-044.2. |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/
import React, { useState, useEffect } from "react";
import PropTypes from "prop-types";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Box,
  TextField,
  MenuItem,
  Stack,
  IconButton,
  Typography,
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import FilterListIcon from "@mui/icons-material/FilterList";
import { useTranslation } from "react-i18next";
import { filtrosVacios } from "./tipoCostoIndirectoValidation";

export default function FiltroTipoCostoIndirecto({
  open,
  filtros = filtrosVacios,
  onApply,
  onClose,
}) {
  const { t } = useTranslation();
  const [localFiltros, setLocalFiltros] = useState(filtros);

  useEffect(() => {
    if (open) {
      setLocalFiltros(filtros);
    }
  }, [open, filtros]);

  const handleChange = (field) => (event) => {
    setLocalFiltros((prev) => ({
      ...prev,
      [field]: event.target.value,
    }));
  };

  const handleClear = () => {
    setLocalFiltros(filtrosVacios);
  };

  const handleApply = () => {
    onApply?.(localFiltros);
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="xs" fullWidth>
      <DialogTitle sx={{ m: 0, p: 2 }}>
        <Stack direction="row" alignItems="center" justifyContent="space-between">
          <Stack direction="row" spacing={1} alignItems="center">
            <FilterListIcon fontSize="small" color="primary" />
            <Typography variant="h6" component="span" sx={{ fontWeight: 600 }}>
              {t("costos.tipoCostoIndirecto.filters.title", "Filtrar tipos de costos indirectos")}
            </Typography>
          </Stack>
          <IconButton aria-label="close" onClick={onClose} size="small">
            <CloseIcon fontSize="small" />
          </IconButton>
        </Stack>
      </DialogTitle>

      <DialogContent dividers>
        <Box sx={{ display: "flex", flexDirection: "column", gap: 2.5, pt: 1 }}>
          <TextField
            label={t("costos.tipoCostoIndirecto.filters.nombre", "Nombre")}
            value={localFiltros.nombre}
            onChange={handleChange("nombre")}
            fullWidth
            size="small"
            placeholder={t("common.actions.search", "Buscar...")}
            InputLabelProps={{ shrink: true }}
          />

          <TextField
            select
            label={t("costos.tipoCostoIndirecto.filters.estado", "Estado")}
            value={localFiltros.estadoId}
            onChange={handleChange("estadoId")}
            fullWidth
            size="small"
            InputLabelProps={{ shrink: true }}
          >
            <MenuItem value="">{t("common.labels.all", "Todos")}</MenuItem>
            <MenuItem value="1">{t("common.labels.active", "Activo")}</MenuItem>
            <MenuItem value="2">{t("common.labels.inactive", "Inactivo")}</MenuItem>
          </TextField>
        </Box>
      </DialogContent>

      <DialogActions sx={{ px: 2.5, py: 1.5 }}>
        <Button onClick={handleClear} color="inherit">
          {t("common.actions.clear", "Limpiar")}
        </Button>
        <Button onClick={onClose} color="inherit">
          {t("common.actions.cancel", "Cancelar")}
        </Button>
        <Button onClick={handleApply} variant="contained" color="primary">
          {t("common.actions.apply", "Aplicar")}
        </Button>
      </DialogActions>
    </Dialog>
  );
}

FiltroTipoCostoIndirecto.propTypes = {
  open: PropTypes.bool.isRequired,
  filtros: PropTypes.shape({
    nombre: PropTypes.string,
    estadoId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  }),
  onApply: PropTypes.func.isRequired,
  onClose: PropTypes.func.isRequired,
};
