/*=============================================================================
 Nombre del archivo : CrudFilterModal.jsx
 Descripcion        : Modal generico de filtros para modulos CRUD.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-09-21 | 0.4.0   | Cesar Medina         | Permite personalizar el     |
 |            |         |                      | estilo visual del modal.    |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/

import React, { useState, useEffect } from "react";
import PropTypes from "prop-types";
import {
  Dialog, DialogTitle, DialogContent, DialogActions,
  FormControl, InputLabel, Select, MenuItem,
  Button, Box, Stack, Typography, IconButton, Card, CardContent
} from "@mui/material";
import { useTranslation } from "react-i18next";
import CloseIcon from "@mui/icons-material/Close";

/**
 * Modal genérico de filtros con limpieza automática de campos dependientes.
 * @param {Array} fields -> { name, label, getOptions, dependsOn, disabled, clearChildren }
 * - clearChildren: Array de nombres de campos que se limpiarán cuando este campo cambie
 * - dependsOn: Array de nombres de campos de los que depende este campo
 * - disabled: Función que determina si el campo está deshabilitado basado en values
 */
export default function CrudFilterModal({
  open, onClose, title, titleKey,
  fields, values, onChange,
  onApply, onClear,
  dialogMaxWidth = "md",
  contentMaxWidth = 1180,
  titleIcon = null,
  paperSx = {},
  titleSx = {},
  contentSx = {},
  bodySx = {},
  actionsSx = {},
  summaryCardSx = {},
  formCardSx = {},
  primaryButtonSx = {},
  secondaryButtonSx = {},
  closeButtonSx = {},
}) {
  const { t } = useTranslation();
  const [options, setOptions] = useState({});
  const resolvedTitle = titleKey ? t(titleKey) : title;

  // Función para manejar cambios con limpieza automática de hijos
  const handleFieldChange = (fieldName, value) => {
    const field = fields.find(f => f.name === fieldName);
    
    // Primero, actualizar el campo actual
    onChange({ name: fieldName, value });
    
    // Luego, limpiar los campos hijos si existen
    if (field?.clearChildren && field.clearChildren.length > 0) {
      field.clearChildren.forEach(childName => {
        onChange({ name: childName, value: "" });
      });
    }
  };

  useEffect(() => {
    fields.forEach(async (f) => {
      if (f.getOptions) {
        const opts = await f.getOptions(values);
        setOptions((prev) => ({ ...prev, [f.name]: opts }));
        
        // Auto-seleccionar si solo hay una opción y el campo está vacío
        if (opts.length === 1 && !values[f.name]) {
          const field = fields.find(field => field.name === f.name);
          // Solo auto-completar si el campo no está deshabilitado
          if (!field?.disabled?.(values)) {
            handleFieldChange(f.name, opts[0].value);
          }
        }
      }
    });
  }, [fields, values]);

  return (
    <Dialog
      open={open}
      onClose={onClose}
      maxWidth={dialogMaxWidth}
      fullWidth
      PaperProps={{ sx: paperSx }}
    >
      <DialogTitle sx={titleSx}>
        <Stack
          direction="row"
          alignItems="center"
          justifyContent="space-between"
          spacing={2}
          sx={{ width: "100%", maxWidth: contentMaxWidth, mx: "auto" }}
        >
          <Stack direction="row" spacing={1.5} alignItems="center">
            {titleIcon ? titleIcon : null}
            <Typography variant="h5" sx={{ fontSize: "1.15rem", fontWeight: 700, color: "inherit" }}>
              {resolvedTitle}
            </Typography>
          </Stack>
          <IconButton
            onClick={onClose}
            size="small"
            aria-label={t("common.actions.close")}
            sx={closeButtonSx}
          >
            <CloseIcon sx={{ color: "inherit" }} />
          </IconButton>
        </Stack>
      </DialogTitle>
      <DialogContent sx={contentSx}>
        <Stack
          spacing={3}
          sx={{ width: "100%", maxWidth: contentMaxWidth, mx: "auto", mt: 1.5 }}
        >
          {Object.keys(summaryCardSx || {}).length > 0 ? (
            <Card elevation={0} sx={summaryCardSx}>
              <CardContent sx={{ p: { xs: 2, sm: 2.5 } }}>
                <Typography variant="h6" sx={{ fontWeight: 700, color: "inherit" }}>
                  {resolvedTitle}
                </Typography>
              </CardContent>
            </Card>
          ) : null}

          <Card sx={formCardSx}>
            <CardContent sx={{ p: { xs: 2.75, sm: 3.25 } }}>
              <Box
                sx={{
                  display: "grid",
                  gridTemplateColumns: { xs: "1fr", sm: "1fr 1fr" },
                  gap: 2,
                  ...bodySx,
                }}
              >
                {fields.map((f) => (
                  <FormControl key={f.name} fullWidth disabled={f.disabled?.(values)}>
                    <InputLabel>{f.labelKey ? t(f.labelKey) : f.label}</InputLabel>
                    <Select
                      value={values[f.name] || ""}
                      onChange={(e) =>
                        handleFieldChange(f.name, e.target.value)
                      }
                    >
                      <MenuItem value="">{t("common.labels.all")}</MenuItem>
                      {(options[f.name] || []).map((o) => (
                        <MenuItem key={o.value} value={o.value}>
                          {o.label}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                ))}
              </Box>
            </CardContent>
          </Card>
        </Stack>
      </DialogContent>
      <DialogActions sx={actionsSx}>
        <Stack
          direction={{ xs: "column-reverse", sm: "row" }}
          spacing={1.5}
          justifyContent="space-between"
          alignItems={{ xs: "stretch", sm: "center" }}
          sx={{ width: "100%", maxWidth: contentMaxWidth, mx: "auto" }}
        >
          <Button onClick={onClear} sx={secondaryButtonSx}>
            {t("common.actions.clear")}
          </Button>
          <Stack direction={{ xs: "column", sm: "row" }} spacing={1.5} sx={{ width: { xs: "100%", sm: "auto" } }}>
            <Button onClick={onClose} sx={secondaryButtonSx}>
              {t("common.actions.cancel")}
            </Button>
            <Button variant="contained" onClick={onApply} sx={primaryButtonSx}>
              {t("common.actions.apply")}
            </Button>
          </Stack>
        </Stack>
      </DialogActions>
    </Dialog>
  );
}

CrudFilterModal.propTypes = {
  open: PropTypes.bool,
  onClose: PropTypes.func,
  title: PropTypes.string,
  titleKey: PropTypes.string,
  fields: PropTypes.arrayOf(
    PropTypes.shape({
      name: PropTypes.string.isRequired,
      label: PropTypes.string,
      labelKey: PropTypes.string,
      getOptions: PropTypes.func,
      dependsOn: PropTypes.array,
      disabled: PropTypes.func,
      clearChildren: PropTypes.array,
    })
  ),
  values: PropTypes.object,
  onChange: PropTypes.func,
  onApply: PropTypes.func,
  onClear: PropTypes.func,
  dialogMaxWidth: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.oneOf([false]),
  ]),
  contentMaxWidth: PropTypes.number,
  titleIcon: PropTypes.node,
  paperSx: PropTypes.object,
  titleSx: PropTypes.object,
  contentSx: PropTypes.object,
  bodySx: PropTypes.object,
  actionsSx: PropTypes.object,
  summaryCardSx: PropTypes.object,
  formCardSx: PropTypes.object,
  primaryButtonSx: PropTypes.object,
  secondaryButtonSx: PropTypes.object,
  closeButtonSx: PropTypes.object,
};
