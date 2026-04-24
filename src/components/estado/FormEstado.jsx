// src/components/Estado/FormEstado.jsx
import * as React from "react";
import PropTypes from "prop-types";
import axios from "../axiosConfig";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  FormHelperText,
} from "@mui/material";

export default function FormEstado({
  selectedRow,
  setSelectedRow,
  setMessage,
  reloadData,
  open,
  setOpen,
  categorias = [],
}) {
  const [methodName, setMethodName] = React.useState("Agregar");

  const initialData = {
    nombre: "",
    acronimo: "",
    descripcion: "",
    estadoCategoriaId: "",
  };

  const [formData, setFormData] = React.useState(initialData);
  const [errors, setErrors] = React.useState({});

  React.useEffect(() => {
    if (!open) return;

    if (selectedRow?.id) {
      setFormData({
        nombre: selectedRow.nombre || "",
        acronimo: selectedRow.acronimo || "",
        descripcion: selectedRow.descripcion || "",
        estadoCategoriaId:
          selectedRow.estadoCategoria?.id ?? selectedRow.estadoCategoriaId ?? "",
      });
      setMethodName("Actualizar");
    } else {
      setFormData(initialData);
      setMethodName("Agregar");
    }

    setErrors({});
  }, [open, selectedRow]);

  const handleClose = () => {
    setOpen(false);
    setSelectedRow(null);
    setFormData(initialData);
    setErrors({});
  };

  const handleChange = (e) => {
    const { name, value } = e.target;

    if (name === "nombre" || name === "acronimo") {
      const soloLetras = value.replace(/[^A-Za-z\s]/g, "");
      setFormData((prev) => ({ ...prev, [name]: soloLetras }));
    } else {
      setFormData((prev) => ({ ...prev, [name]: value }));
    }

    setErrors((prev) => ({ ...prev, [name]: "" }));
  };

  const validate = () => {
    const e = {};
    const onlyLettersRegex = /^[A-Za-z\s]+$/;

    if (!formData.nombre.trim()) {
      e.nombre = "El nombre es obligatorio.";
    } else if (!onlyLettersRegex.test(formData.nombre.trim())) {
      e.nombre = "El nombre solo puede contener letras y espacios.";
    }

    if (!formData.acronimo.trim()) {
      e.acronimo = "El acronimo es obligatorio.";
    } else if (!onlyLettersRegex.test(formData.acronimo.trim())) {
      e.acronimo = "El acronimo solo puede contener letras y espacios.";
    }

    if (!formData.descripcion.trim()) {
      e.descripcion = "La descripcion es obligatoria.";
    }

    if (!formData.estadoCategoriaId) {
      e.estadoCategoriaId = "La categoria es obligatoria.";
    }

    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleSubmit = async (ev) => {
    ev.preventDefault();
    if (!validate()) return;

    const payload = {
      nombre: formData.nombre.trim(),
      acronimo: formData.acronimo.trim(),
      descripcion: formData.descripcion.trim(),
      estadoCategoriaId: Number(formData.estadoCategoriaId),
    };

    const creating = methodName === "Agregar";
    const url = creating ? "/v1/estado" : `/v1/estado/${selectedRow.id}`;
    const req = creating ? axios.post : axios.put;

    try {
      await req(url, payload);
      setMessage({
        open: true,
        severity: "success",
        text: creating ? "Estado creado" : "Estado actualizado",
      });
      handleClose();
      reloadData();
    } catch (err) {
      setMessage({
        open: true,
        severity: "error",
        text: err?.response?.data?.message || "Error al guardar",
      });
    }
  };

  return (
    <Dialog open={open} onClose={handleClose} fullWidth maxWidth="sm">
      <form onSubmit={handleSubmit}>
        <DialogTitle>{methodName} Estado</DialogTitle>
        <DialogContent>
          <DialogContentText>Formulario para gestionar estados</DialogContentText>

          <TextField
            fullWidth
            margin="dense"
            name="nombre"
            label="Nombre"
            value={formData.nombre}
            onChange={handleChange}
            error={!!errors.nombre}
            helperText={errors.nombre}
          />

          <TextField
            fullWidth
            margin="dense"
            name="acronimo"
            label="Acronimo"
            value={formData.acronimo}
            onChange={handleChange}
            error={!!errors.acronimo}
            helperText={errors.acronimo}
          />

          <TextField
            fullWidth
            margin="dense"
            name="descripcion"
            label="Descripcion"
            value={formData.descripcion}
            onChange={handleChange}
            error={!!errors.descripcion}
            helperText={errors.descripcion}
          />

          <FormControl fullWidth margin="dense" error={!!errors.estadoCategoriaId}>
            <InputLabel id="estadoCategoriaId-label">Categoria</InputLabel>
            <Select
              labelId="estadoCategoriaId-label"
              label="Categoria"
              name="estadoCategoriaId"
              value={formData.estadoCategoriaId}
              onChange={handleChange}
            >
              {categorias.map((c) => (
                <MenuItem key={c.id} value={c.id}>
                  {c.nombre}
                </MenuItem>
              ))}
            </Select>
            <FormHelperText>{errors.estadoCategoriaId}</FormHelperText>
          </FormControl>
        </DialogContent>

        <DialogActions>
          <Button onClick={handleClose}>Cancelar</Button>
          <Button type="submit" variant="contained">
            {methodName}
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  );
}

FormEstado.propTypes = {
  selectedRow: PropTypes.object,
  setSelectedRow: PropTypes.func.isRequired,
  setMessage: PropTypes.func.isRequired,
  reloadData: PropTypes.func.isRequired,
  open: PropTypes.bool.isRequired,
  setOpen: PropTypes.func.isRequired,
  categorias: PropTypes.array,
};
