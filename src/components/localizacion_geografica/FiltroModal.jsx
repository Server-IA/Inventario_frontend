/*=============================================================================
 Nombre del archivo : FiltroModal.jsx
 Descripcion        : Modal para filtrar registros por código, nombre, acrónimo y estado.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-05-23 | 1.0.0   | Jeisson Sanchez      | Creación del archivo.       |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/

import React, { useState, useEffect } from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  MenuItem,
  Box,
} from "@mui/material";

export default function FiltroModal({ open, onClose, onFilter, currentFilters }) {
  const [filters, setFilters] = useState({
    nombre: "",
    codigo: "",
    acronimo: "",
    estado: "Todos",
  });

  useEffect(() => {
    if (open) {
      setFilters(currentFilters || {
        nombre: "",
        codigo: "",
        acronimo: "",
        estado: "Todos",
      });
    }
  }, [open, currentFilters]);

  const handleChange = (e) => {
    setFilters({ ...filters, [e.target.name]: e.target.value });
  };

  const handleFilter = () => {
    onFilter(filters);
    onClose();
  };

  const handleClear = () => {
    const cleared = { nombre: "", codigo: "", acronimo: "", estado: "Todos" };
    setFilters(cleared);
    onFilter(cleared);
    onClose();
  };

  return (
    <Dialog
      open={open}
      onClose={onClose}
      PaperProps={{
        sx: {
          backgroundColor: "#1e1e1e",
          color: "#fff",
          minWidth: 400,
          border: "1px solid #fff",
          borderRadius: 2,
        },
      }}
    >
      <DialogTitle sx={{ textAlign: "center", pb: 1, borderBottom: "1px solid #555" }}>
        Filtrar
      </DialogTitle>
      <DialogContent sx={{ pt: 3 }}>
        <Box sx={{ display: "flex", flexDirection: "column", gap: 2, mt: 1 }}>
          <TextField
            label="Nombre"
            name="nombre"
            value={filters.nombre}
            onChange={handleChange}
            fullWidth
            size="small"
            InputLabelProps={{ style: { color: "#aaa" } }}
            InputProps={{ style: { color: "#fff", borderColor: "#555" } }}
            sx={{
              "& .MuiOutlinedInput-root": {
                "& fieldset": { borderColor: "#555" },
                "&:hover fieldset": { borderColor: "#888" },
                "&.Mui-focused fieldset": { borderColor: "#fff" },
              },
            }}
          />
          <Box sx={{ display: "flex", gap: 2 }}>
            <TextField
              label="Código"
              name="codigo"
              value={filters.codigo}
              onChange={handleChange}
              fullWidth
              size="small"
              InputLabelProps={{ style: { color: "#aaa" } }}
              InputProps={{ style: { color: "#fff" } }}
              sx={{
                "& .MuiOutlinedInput-root": {
                  "& fieldset": { borderColor: "#555" },
                  "&:hover fieldset": { borderColor: "#888" },
                  "&.Mui-focused fieldset": { borderColor: "#fff" },
                },
              }}
            />
            <TextField
              label="Acrónimo"
              name="acronimo"
              value={filters.acronimo}
              onChange={handleChange}
              fullWidth
              size="small"
              InputLabelProps={{ style: { color: "#aaa" } }}
              InputProps={{ style: { color: "#fff" } }}
              sx={{
                "& .MuiOutlinedInput-root": {
                  "& fieldset": { borderColor: "#555" },
                  "&:hover fieldset": { borderColor: "#888" },
                  "&.Mui-focused fieldset": { borderColor: "#fff" },
                },
              }}
            />
            <TextField
              select
              label="Estado"
              name="estado"
              value={filters.estado}
              onChange={handleChange}
              fullWidth
              size="small"
              InputLabelProps={{ style: { color: "#aaa" } }}
              InputProps={{ style: { color: "#fff" } }}
              sx={{
                "& .MuiOutlinedInput-root": {
                  "& fieldset": { borderColor: "#555" },
                  "&:hover fieldset": { borderColor: "#888" },
                  "&.Mui-focused fieldset": { borderColor: "#fff" },
                },
                "& .MuiSelect-icon": { color: "#aaa" },
              }}
            >
              <MenuItem value="Todos">Todos</MenuItem>
              <MenuItem value="Activo">Activo</MenuItem>
              <MenuItem value="Inactivo">Inactivo</MenuItem>
            </TextField>
          </Box>
        </Box>
      </DialogContent>
      <DialogActions sx={{ justifyContent: "center", pb: 3, pt: 2 }}>
        <Button
          onClick={handleClear}
          variant="outlined"
          sx={{
            color: "#fff",
            borderColor: "#fff",
            "&:hover": { borderColor: "#ccc", backgroundColor: "rgba(255,255,255,0.1)" },
          }}
        >
          Limpiar
        </Button>
        <Button
          onClick={onClose}
          variant="outlined"
          sx={{
            color: "#fff",
            borderColor: "#fff",
            "&:hover": { borderColor: "#ccc", backgroundColor: "rgba(255,255,255,0.1)" },
          }}
        >
          Cancelar
        </Button>
        <Button
          onClick={handleFilter}
          variant="contained"
          sx={{
            backgroundColor: "#2e7d32",
            "&:hover": { backgroundColor: "#1b5e20" },
          }}
        >
          Filtrar
        </Button>
      </DialogActions>
    </Dialog>
  );
}
