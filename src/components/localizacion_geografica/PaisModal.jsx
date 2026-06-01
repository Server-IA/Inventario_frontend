/*=============================================================================
 Nombre del archivo : PaisModal.jsx
 Descripcion        : Modal para crear o actualizar un País.
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
  Box,
} from "@mui/material";

export default function PaisModal({ open, onClose, onSave, paisToEdit }) {
  const [formData, setFormData] = useState({
    nombre: "",
    codigo: "",
    acronimo: "",
  });

  useEffect(() => {
    if (open) {
      if (paisToEdit) {
        setFormData({
          nombre: paisToEdit.nombre || "",
          codigo: paisToEdit.codigo || "",
          acronimo: paisToEdit.acronimo || "",
        });
      } else {
        setFormData({ nombre: "", codigo: "", acronimo: "" });
      }
    }
  }, [open, paisToEdit]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSave = () => {
    onSave(formData);
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
        {paisToEdit ? "Actualizar País" : "Crear País"}
      </DialogTitle>
      <DialogContent sx={{ pt: 3 }}>
        <Box sx={{ display: "flex", flexDirection: "column", gap: 2, mt: 1 }}>
          <TextField
            label="Nombre"
            name="nombre"
            value={formData.nombre}
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
          <Box sx={{ display: "flex", gap: 2 }}>
            <TextField
              label="Código"
              name="codigo"
              value={formData.codigo}
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
              value={formData.acronimo}
              onChange={handleChange}
              fullWidth
              size="small"
              inputProps={{ maxLength: 3 }}
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
          </Box>
        </Box>
      </DialogContent>
      <DialogActions sx={{ justifyContent: "center", pb: 3, pt: 2 }}>
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
          onClick={handleSave}
          variant="contained"
          disabled={!formData.nombre || !formData.codigo || !formData.acronimo}
          sx={{
            backgroundColor: "#2e7d32",
            "&:hover": { backgroundColor: "#1b5e20" },
            "&.Mui-disabled": { backgroundColor: "#555", color: "#888" }
          }}
        >
          Guardar
        </Button>
      </DialogActions>
    </Dialog>
  );
}
