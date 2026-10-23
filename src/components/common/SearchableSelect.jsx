/*=============================================================================
 Nombre del archivo : SearchableSelect.jsx
 Descripcion        : Selector MUI con limitación de altura (~10 opciones) y búsqueda local.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-10-16 | 0.4.0   | Jeisson Sanchez      | [Issue #324] Creación de selector con altura limitada y búsqueda local en frontend. |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/
import React, { useState, useMemo } from "react";
import PropTypes from "prop-types";
import {
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  ListSubheader,
  TextField,
  InputAdornment,
  Box,
  Typography,
} from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";

export default function SearchableSelect({
  label,
  value,
  onChange,
  options = [],
  disabled = false,
  required = false,
  error = false,
  helperText = "",
  placeholder = "Buscar...",
  emptyOptionLabel = "Todos",
  size = "small",
  fullWidth = true,
  name,
  maxItemsVisible = 10,
  startAdornment,
  InputLabelProps,
}) {
  const [searchTerm, setSearchTerm] = useState("");

  const filteredOptions = useMemo(() => {
    if (!searchTerm.trim()) return options;
    const term = searchTerm.toLowerCase();
    return options.filter((opt) =>
      String(opt.label ?? opt.name ?? opt.nombre ?? "").toLowerCase().includes(term)
    );
  }, [options, searchTerm]);

  // Altura máxima equivalente a ~10 opciones visibles con scroll interno
  const maxHeight = maxItemsVisible * 36 + 60;

  const inputLabelId = label ? `${name || label.replace(/\s+/g, "-").toLowerCase()}-label` : undefined;

  return (
    <FormControl
      size={size}
      fullWidth={fullWidth}
      disabled={disabled}
      required={required}
      error={error}
    >
      {label && (
        <InputLabel id={inputLabelId} shrink={InputLabelProps?.shrink}>
          {label}
        </InputLabel>
      )}
      <Select
        id={inputLabelId ? `${inputLabelId}-select` : undefined}
        labelId={inputLabelId}
        name={name}
        value={value ?? ""}
        label={label}
        onChange={onChange}
        startAdornment={startAdornment}
        onClose={() => setSearchTerm("")}
        MenuProps={{
          autoFocus: false,
          PaperProps: {
            sx: {
              maxHeight,
              "& .MuiList-root": {
                pt: 0,
              },
            },
          },
        }}
      >
        <ListSubheader
          sx={{
            pt: 1,
            pb: 1,
            px: 1.5,
            bgcolor: "background.paper",
            lineHeight: "normal",
          }}
        >
          <TextField
            size="small"
            autoFocus
            fullWidth
            placeholder={placeholder}
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            onKeyDown={(e) => e.stopPropagation()}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon fontSize="small" />
                </InputAdornment>
              ),
            }}
          />
        </ListSubheader>

        {emptyOptionLabel && !searchTerm && (
          <MenuItem value="">
            <em>{emptyOptionLabel}</em>
          </MenuItem>
        )}

        {filteredOptions.length === 0 ? (
          <Box sx={{ py: 1.5, px: 2, textAlign: "center" }}>
            <Typography variant="body2" color="text.secondary">
              Sin resultados
            </Typography>
          </Box>
        ) : (
          filteredOptions.map((opt) => (
            <MenuItem key={opt.value ?? opt.id} value={opt.value ?? opt.id}>
              {opt.label ?? opt.name ?? opt.nombre}
            </MenuItem>
          ))
        )}
      </Select>
      {helperText && (
        <Typography variant="caption" color={error ? "error" : "text.secondary"} sx={{ mt: 0.5, mx: 1.75 }}>
          {helperText}
        </Typography>
      )}
    </FormControl>
  );
}

SearchableSelect.propTypes = {
  label: PropTypes.string,
  value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  onChange: PropTypes.func.isRequired,
  options: PropTypes.array,
  disabled: PropTypes.bool,
  required: PropTypes.bool,
  error: PropTypes.bool,
  helperText: PropTypes.node,
  placeholder: PropTypes.string,
  emptyOptionLabel: PropTypes.string,
  size: PropTypes.string,
  fullWidth: PropTypes.bool,
  name: PropTypes.string,
  maxItemsVisible: PropTypes.number,
  startAdornment: PropTypes.node,
  InputLabelProps: PropTypes.object,
};
