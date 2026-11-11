/*=============================================================================
 Nombre del archivo : CrudFilterModal.jsx
 Descripcion        : Modal generico de filtros para modulos CRUD.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-10-30 | 0.4.0   | Cesar Medina         | Evita cargas de opciones    |
 |            |         |                      | con modal cerrado e ignora  |
 |            |         |                      | respuestas obsoletas.       |
 | 2026-10-23 | 0.4.0   | Jeisson Sanchez      | Agrega filtros de texto y   |
 |            |         |                      | búsqueda reutilizables.     |
 | 2026-09-21 | 0.4.0   | Cesar Medina         | Permite personalizar el     |
 |            |         |                      | estilo visual del modal.    |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/

import React, { useEffect, useState } from "react";
import PropTypes from "prop-types";
import {
  Box,
  Button,
  Card,
  CardContent,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControl,
  IconButton,
  InputLabel,
  MenuItem,
  Select,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import { useTranslation } from "react-i18next";
import CloseIcon from "@mui/icons-material/Close";
import SearchableSelect from "./SearchableSelect.jsx";

/**
 * Modal genérico de filtros con limpieza automática de campos dependientes.
 * Soporta selects estándar, texto libre y selects con búsqueda local.
 * @param {Array} fields -> { name, label, labelKey, type, placeholder,
 * placeholderKey, getOptions, options, dependsOn, disabled, clearChildren,
 * fullRow }
 */
export default function CrudFilterModal({
  open,
  onClose,
  title,
  titleKey,
  fields = [],
  values = {},
  onChange,
  onApply,
  onClear,
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

  const handleFieldChange = (fieldName, value) => {
    const field = fields.find((fieldItem) => fieldItem.name === fieldName);

    onChange?.({ name: fieldName, value });

    if (field?.clearChildren?.length) {
      field.clearChildren.forEach((childName) => {
        onChange?.({ name: childName, value: "" });
      });
    }
  };

  useEffect(() => {
    if (!open) return undefined;

    let active = true;

    const loadOptions = async () => {
      for (const field of fields) {
        if (!field.getOptions) continue;

        try {
          const fieldOptions = await field.getOptions(values);
          if (!active) return;

          const safeOptions = Array.isArray(fieldOptions) ? fieldOptions : [];
          setOptions((previous) => ({ ...previous, [field.name]: safeOptions }));

          if (safeOptions.length === 1 && !values[field.name] && !field.disabled?.(values)) {
            handleFieldChange(field.name, safeOptions[0].value ?? safeOptions[0].id);
          }
        } catch {
          if (!active) return;
          setOptions((previous) => ({ ...previous, [field.name]: [] }));
        }
      }
    };

    loadOptions();

    return () => {
      active = false;
    };
  }, [fields, open, values]);

  useEffect(() => {
    if (!open) setOptions({});
  }, [open]);

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
            {titleIcon}
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
        <Stack spacing={3} sx={{ width: "100%", maxWidth: contentMaxWidth, mx: "auto", mt: 1.5 }}>
          {Object.keys(summaryCardSx).length > 0 ? (
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
                {fields.map((field) => {
                  const isDisabled = Boolean(field.disabled?.(values));
                  const gridSx = field.fullRow ? { gridColumn: "1 / -1" } : {};
                  const fieldLabel = field.labelKey ? t(field.labelKey) : field.label;
                  const placeholder = field.placeholderKey
                    ? t(field.placeholderKey)
                    : field.placeholder || "";
                  const fieldOptions = field.options || options[field.name] || [];

                  if (field.type === "text") {
                    return (
                      <Box key={field.name} sx={gridSx}>
                        <TextField
                          fullWidth
                          size="small"
                          id={`${field.name}-filter-input`}
                          label={fieldLabel}
                          placeholder={placeholder}
                          value={values[field.name] || ""}
                          disabled={isDisabled}
                          onChange={(event) => handleFieldChange(field.name, event.target.value)}
                          InputLabelProps={{ shrink: true }}
                        />
                      </Box>
                    );
                  }

                  if (field.type === "searchableSelect") {
                    return (
                      <Box key={field.name} sx={gridSx}>
                        <SearchableSelect
                          fullWidth
                          size="small"
                          name={field.name}
                          label={fieldLabel}
                          placeholder={placeholder || t("common.actions.search", "Buscar...")}
                          emptyOptionLabel={t("common.labels.all", "Todos")}
                          noOptionsText={t("common.labels.noOptions", "Sin resultados")}
                          value={values[field.name] || ""}
                          disabled={isDisabled}
                          options={fieldOptions}
                          onChange={(event) => handleFieldChange(field.name, event.target.value)}
                          InputLabelProps={{ shrink: true }}
                        />
                      </Box>
                    );
                  }

                  return (
                    <Box key={field.name} sx={gridSx}>
                      <FormControl fullWidth size="small" disabled={isDisabled}>
                        <InputLabel id={`${field.name}-filter-label`}>{fieldLabel}</InputLabel>
                        <Select
                          labelId={`${field.name}-filter-label`}
                          label={fieldLabel}
                          value={values[field.name] || ""}
                          onChange={(event) => handleFieldChange(field.name, event.target.value)}
                        >
                          <MenuItem value="">{t("common.labels.all", "Todos")}</MenuItem>
                          {fieldOptions.map((option) => (
                            <MenuItem key={option.value ?? option.id} value={option.value ?? option.id}>
                              {option.label ?? option.nombre ?? option.name}
                            </MenuItem>
                          ))}
                        </Select>
                      </FormControl>
                    </Box>
                  );
                })}
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
            {t("common.actions.clear", "Limpiar")}
          </Button>
          <Stack direction={{ xs: "column", sm: "row" }} spacing={1.5} sx={{ width: { xs: "100%", sm: "auto" } }}>
            <Button onClick={onClose} sx={secondaryButtonSx}>
              {t("common.actions.cancel", "Cancelar")}
            </Button>
            <Button variant="contained" onClick={onApply} sx={primaryButtonSx}>
              {t("common.actions.apply", "Aplicar")}
            </Button>
          </Stack>
        </Stack>
      </DialogActions>
    </Dialog>
  );
}

CrudFilterModal.propTypes = {
  open: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
  title: PropTypes.string,
  titleKey: PropTypes.string,
  fields: PropTypes.arrayOf(
    PropTypes.shape({
      name: PropTypes.string.isRequired,
      label: PropTypes.string,
      labelKey: PropTypes.string,
      type: PropTypes.oneOf(["select", "text", "searchableSelect"]),
      placeholder: PropTypes.string,
      placeholderKey: PropTypes.string,
      getOptions: PropTypes.func,
      options: PropTypes.array,
      dependsOn: PropTypes.array,
      disabled: PropTypes.func,
      clearChildren: PropTypes.array,
      fullRow: PropTypes.bool,
    })
  ).isRequired,
  values: PropTypes.object.isRequired,
  onChange: PropTypes.func.isRequired,
  onApply: PropTypes.func.isRequired,
  onClear: PropTypes.func.isRequired,
  dialogMaxWidth: PropTypes.oneOfType([PropTypes.string, PropTypes.oneOf([false])]),
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
