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
import StackButtons from "../StackButtons";

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
          selectedRow.estadoCategoria?.id ??
          selectedRow.estadoCategoriaId ??
          "",
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
    setSelectedRow({});
    setFormData(initialData);
    setErrors({});
  };

  const handleChange = (e) => {
    const { name, value } = e.target;

    // solo letras y espacios para nombre y acronimo
    if (name === "nombre" || name === "acronimo") {
      const soloLetras = value.replace(/[^A-Za-zÁÉÍÓÚáéíóúÑñ\s]/g, "");
      setFormData((prev) => ({ ...prev, [name]: soloLetras }));
    } else {
      setFormData((prev) => ({ ...prev, [name]: value }));
    }

    setErrors((prev) => ({ ...prev, [name]: "" }));
  };

  const validate = () => {
    const e = {};
    const onlyLettersRegex = /^[A-Za-zÁÉÍÓÚáéíóúÑñ\s]+$/;

    // nombre obligatorio y solo letras
    if (!formData.nombre.trim()) {
      e.nombre = "El nombre es obligatorio.";
    } else if (!onlyLettersRegex.test(formData.nombre.trim())) {
      e.nombre = "El nombre solo puede contener letras y espacios.";
    }

    // acronimo obligatorio y solo letras
    if (!formData.acronimo.trim()) {
      e.acronimo = "El acrónimo es obligatorio.";
    } else if (!onlyLettersRegex.test(formData.acronimo.trim())) {
      e.acronimo = "El acrónimo solo puede contener letras y espacios.";
    }

    // descripción obligatoria
    if (!formData.descripcion.trim()) {
      e.descripcion = "La descripción es obligatoria.";
    }

    // categoría obligatoria
    if (!formData.estadoCategoriaId) {
      e.estadoCategoriaId = "La categoría es obligatoria.";
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

  const deleteRow = async () => {
    if (!selectedRow?.id) {
      setMessage({
        open: true,
        severity: "error",
        text: "Selecciona un registro para eliminar",
      });
      return;
    }
    if (!window.confirm(`¿Eliminar "${selectedRow.nombre}"?`)) return;

    try {
      await axios.delete(`/v1/estado/${selectedRow.id}`);
      setMessage({
        open: true,
        severity: "success",
        text: "Eliminado",
      });
      handleClose();
      reloadData();
    } catch {
      setMessage({
        open: true,
        severity: "error",
        text: "No se pudo eliminar",
      });
    }
  };

  return (
    <>
      <StackButtons
        methods={{
          create: () => {
            setMethodName("Agregar");
            setFormData(initialData);
            setErrors({});
            setOpen(true);
          },
          update: () => {
            if (!selectedRow?.id)
              return setMessage({
                open: true,
                severity: "error",
                text: "Selecciona un registro",
              });
            setMethodName("Actualizar");
            setErrors({});
            setOpen(true);
          },
          deleteRow,
        }}
      />

      <Dialog open={open} onClose={handleClose} fullWidth maxWidth="sm">
        <form onSubmit={handleSubmit}>
          <DialogTitle>{methodName} Estado</DialogTitle>
          <DialogContent>
            <DialogContentText>
              Formulario para gestionar estados
            </DialogContentText>

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
              label="Acrónimo"
              value={formData.acronimo}
              onChange={handleChange}
              error={!!errors.acronimo}
              helperText={errors.acronimo}
            />

            <TextField
              fullWidth
              margin="dense"
              name="descripcion"
              label="Descripción"
              value={formData.descripcion}
              onChange={handleChange}
              error={!!errors.descripcion}
              helperText={errors.descripcion}
            />

            <FormControl
              fullWidth
              margin="dense"
              error={!!errors.estadoCategoriaId}
            >
              <InputLabel id="estadoCategoriaId-label">
                Categoría
              </InputLabel>
              <Select
                labelId="estadoCategoriaId-label"
                label="Categoría"
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
    </>
  );
}

FormEstado.propTypes = {
  selectedRow: PropTypes.object.isRequired,
  setSelectedRow: PropTypes.func.isRequired,
  setMessage: PropTypes.func.isRequired,
  reloadData: PropTypes.func.isRequired,
  open: PropTypes.bool.isRequired,
  setOpen: PropTypes.func.isRequired,
  categorias: PropTypes.array,
};
