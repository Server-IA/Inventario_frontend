import * as React from "react";
import PropTypes from "prop-types";
import axios from "../axiosConfig";
import {
  Button, Dialog, DialogActions, DialogContent,
  DialogContentText, DialogTitle, TextField, FormControl,
  InputLabel, Select, MenuItem
} from "@mui/material";
import StackButtons from "../StackButtons";

import { validateCamposBase } from "../utils/validations";

export default function FormTipoIdentificacion({ selectedRow, setSelectedRow, setMessage, reloadData }) {
  const [open, setOpen] = React.useState(false);
  const [methodName, setMethodName] = React.useState("");

  const initialData = {
    nombre: "",
    descripcion: "",
    estado: ""
  };

  const [formData, setFormData] = React.useState(initialData);

  const create = () => {
    setFormData(initialData);
    setMethodName("Crear");
    setOpen(true);
  };

  const update = () => {
    if (!selectedRow?.id) {
      setMessage({ open: true, severity: "error", text: "Selecciona un tipo para editar." });
      return;
    }

    setFormData({
      nombre: selectedRow.nombre || "",
      descripcion: selectedRow.descripcion || "",
      estado: selectedRow.estadoId?.toString() || ""
    });

    setMethodName("Actualizar");
    setOpen(true);
  };

  const deleteRow = () => {
    if (!selectedRow?.id) {
      setMessage({ open: true, severity: "error", text: "Selecciona un tipo para eliminar." });
      return;
    }

    axios.delete(`/v1/tipo_identificacion/${selectedRow.id}`)
      .then(() => {
        setMessage({ open: true, severity: "success", text: "Tipo eliminado correctamente." });
        setSelectedRow({});
        reloadData();
      })
      .catch((err) => {
        console.error("❌ Error al eliminar:", err);
        setMessage({
          open: true,
          severity: "error",
          text: `Error al eliminar: ${err.message}`,
        });
      });
  };

  const handleClose = () => setOpen(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };


  const validate = () => {
    const e = {};
    const baseErrors = validateCamposBase({
      nombre: formData.nombre,
      descripcion: formData.descripcion,
      estado: formData.estado, // el validador espera 'estado'
    });

    if (baseErrors.nombre) e.nombre = baseErrors.nombre;
    if (baseErrors.descripcion) e.descripcion = baseErrors.descripcion;
    if (baseErrors.estado) e.estado = baseErrors.estado;
    if (baseErrors._security) e._security = baseErrors._security;

    // Refuerzo de rango para estado
    if (!["", "1", "2"].includes(formData.estado)) {
      e.estado = e.estado || "Debe seleccionar un estado válido.";
    }

    setErrors(e);
    return Object.keys(e).length === 0;
  };


  const handleSubmit = (event) => {
    event.preventDefault();
    if (!validate()) return;

    const payload = {
      nombre: formData.nombre,
      descripcion: formData.descripcion,
      estadoId: parseInt(formData.estado)
    };

    const method = methodName === "Crear" ? axios.post : axios.put;
    const url = methodName === "Crear" ? "/v1/tipo_identificacion" : `/v1/tipo_identificacion/${selectedRow.id}`;

    method(url, payload)
      .then(() => {
        setMessage({
          open: true,
          severity: "success",
          text: methodName === "Crear" ? "Tipo creado con éxito!" : "Tipo actualizado con éxito!"
        });
        setOpen(false);
        setSelectedRow({});
        reloadData();
      })
      .catch(err => {
        setMessage({
          open: true,
          severity: "error",
          text: `Error: ${err.message || "Network Error"}`
        });
      });
  };

  return (
    <>
      <StackButtons methods={{ create, update, deleteRow }} />
      <Dialog open={open} onClose={handleClose}>
        <form onSubmit={handleSubmit}>
          <DialogTitle>{methodName} Tipo de Identificación</DialogTitle>
          <DialogContent>
            <DialogContentText>Formulario para gestionar tipos de identificación</DialogContentText>

            <TextField
              fullWidth margin="dense" required
              name="nombre" label="Nombre"
              value={formData.nombre}
              onChange={handleChange}
            />
            <TextField
              fullWidth margin="dense" required
              name="descripcion" label="Descripción"
              value={formData.descripcion}
              onChange={handleChange}
            />
            <FormControl fullWidth margin="normal" required>
              <InputLabel>Estado</InputLabel>
              <Select
                name="estado"
                value={formData.estado}
                onChange={handleChange}
                label="Estado"
              >
                <MenuItem value="">Seleccione...</MenuItem>
                <MenuItem value="1">Activo</MenuItem>
                <MenuItem value="2">Inactivo</MenuItem>
              </Select>
            </FormControl>
          </DialogContent>
          <DialogActions>
            <Button onClick={handleClose}>Cancelar</Button>
            <Button type="submit">{methodName}</Button>
          </DialogActions>
        </form>
      </Dialog>
    </>
  );
}

FormTipoIdentificacion.propTypes = {
  selectedRow: PropTypes.object.isRequired,
  setSelectedRow: PropTypes.func.isRequired,
  setMessage: PropTypes.func.isRequired,
  reloadData: PropTypes.func.isRequired,
};
