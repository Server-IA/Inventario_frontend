import React, { useState, useEffect } from "react";
import PropTypes from "prop-types";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  TextField,
  Button,
  Box,
} from "@mui/material";
import { useTranslation } from "react-i18next";
import SearchableSelect from "./SearchableSelect.jsx";

/**
 * Modal genérico de filtros con limpieza automática de campos dependientes.
 * Soporta campos select estándar, campos de texto libre (type: "text") y select con búsqueda (type: "searchableSelect").
 * @param {Array} fields -> { name, label, labelKey, type, placeholder, placeholderKey, getOptions, options, dependsOn, disabled, clearChildren, fullRow }
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
}) {
  const { t } = useTranslation();
  const [options, setOptions] = useState({});
  const resolvedTitle = titleKey ? t(titleKey) : title;

  // Función para manejar cambios con limpieza automática de hijos
  const handleFieldChange = (fieldName, value) => {
    const field = fields.find((f) => f.name === fieldName);

    // Primero, actualizar el campo actual
    onChange?.({ name: fieldName, value });

    // Luego, limpiar los campos hijos si existen
    if (field?.clearChildren && field.clearChildren.length > 0) {
      field.clearChildren.forEach((childName) => {
        onChange?.({ name: childName, value: "" });
      });
    }
  };

  useEffect(() => {
    let isCancelled = false;
    fields.forEach(async (f) => {
      if (f.getOptions) {
        const opts = await f.getOptions(values);
        if (isCancelled) return;
        setOptions((prev) => ({ ...prev, [f.name]: opts }));

        // Auto-seleccionar si solo hay una opción y el campo está vacío
        if (opts.length === 1 && !values[f.name]) {
          const field = fields.find((fieldItem) => fieldItem.name === f.name);
          // Solo auto-completar si el campo no está deshabilitado
          if (!field?.disabled?.(values)) {
            handleFieldChange(f.name, opts[0].value);
          }
        }
      }
    });

    return () => {
      isCancelled = true;
    };
  }, [fields, values]);

  return (
    <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
      <DialogTitle>{resolvedTitle}</DialogTitle>
      <DialogContent>
        <Box sx={{ display: "grid", gridTemplateColumns: { xs: "1fr", sm: "1fr 1fr" }, gap: 2, pt: 1 }}>
          {fields.map((f) => {
            const isFullRow = Boolean(f.fullRow);
            const gridStyle = isFullRow ? { gridColumn: "1 / -1" } : {};
            const isDisabled = Boolean(f.disabled?.(values));
            const fieldLabel = f.labelKey ? t(f.labelKey) : f.label;
            const fieldPlaceholder = f.placeholderKey
              ? t(f.placeholderKey)
              : f.placeholder || "";

            // 1. Campo de texto libre
            if (f.type === "text") {
              return (
                <Box key={f.name} sx={gridStyle}>
                  <TextField
                    fullWidth
                    size="small"
                    id={`${f.name}-filter-input`}
                    label={fieldLabel}
                    placeholder={fieldPlaceholder}
                    value={values[f.name] || ""}
                    disabled={isDisabled}
                    onChange={(e) => handleFieldChange(f.name, e.target.value)}
                    InputLabelProps={{ shrink: true }}
                  />
                </Box>
              );
            }

            // 2. Campo SearchableSelect (con búsqueda local y límite de opciones)
            if (f.type === "searchableSelect") {
              const currentOptions = f.options || options[f.name] || [];
              return (
                <Box key={f.name} sx={gridStyle}>
                  <SearchableSelect
                    fullWidth
                    size="small"
                    name={f.name}
                    label={fieldLabel}
                    placeholder={fieldPlaceholder || t("common.actions.search", "Buscar...")}
                    emptyOptionLabel={t("common.labels.all", "Todos")}
                    noOptionsText={t("common.labels.noOptions", "Sin resultados")}
                    value={values[f.name] || ""}
                    disabled={isDisabled}
                    options={currentOptions}
                    onChange={(e) => handleFieldChange(f.name, e.target.value)}
                    InputLabelProps={{ shrink: true }}
                  />
                </Box>
              );
            }

            // 3. Campo Select estándar
            const currentOptions = f.options || options[f.name] || [];
            return (
              <Box key={f.name} sx={gridStyle}>
                <FormControl fullWidth size="small" disabled={isDisabled}>
                  <InputLabel id={`${f.name}-filter-label`}>{fieldLabel}</InputLabel>
                  <Select
                    labelId={`${f.name}-filter-label`}
                    label={fieldLabel}
                    value={values[f.name] || ""}
                    onChange={(e) => handleFieldChange(f.name, e.target.value)}
                  >
                    <MenuItem value="">{t("common.labels.all", "Todos")}</MenuItem>
                    {currentOptions.map((o) => (
                      <MenuItem key={o.value ?? o.id} value={o.value ?? o.id}>
                        {o.label ?? o.nombre ?? o.name}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Box>
            );
          })}
        </Box>
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={onClear} color="inherit">
          {t("common.actions.clear", "Limpiar")}
        </Button>
        <Button variant="contained" onClick={onApply}>
          {t("common.actions.apply", "Aplicar")}
        </Button>
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
};
