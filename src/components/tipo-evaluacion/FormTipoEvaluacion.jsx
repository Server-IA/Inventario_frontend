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

export default function FormTipoEvaluacion({ selectedRow, setSelectedRow, setMessage, reloadData }) {
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
    setMethodName("Add");
    setOpen(true);
  };

  const update = () => {
    if (!selectedRow?.id) {
      setMessage({ open: true, severity: "error", text: "Selecciona un tipo evaluacion para editar." });
      return;
    }

    setFormData({
      nombre: selectedRow.nombre || "",
      descripcion: selectedRow.descripcion || "",
      estado: selectedRow.estadoId?.toString() || ""
    });

    setMethodName("Update");
    setOpen(true);
  };

  const deleteRow = () => {
    if (!selectedRow?.id) {
      setMessage({ open: true, severity: "error", text: "Selecciona un tipo evaluacion  para eliminar." });
      return;
    }

    axios.delete(`/v1/tipo-evaluacion/${selectedRow.id}`)
      .then(() => {
        setMessage({ open: true, severity: "success", text: "Tipo evaluacion eliminada correctamente." });
        setSelectedRow({});
        reloadData();
      })
      .catch((err) => {
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
      descripcion: formData.descripcion, // requerida en tu UI
      estado: formData.estado
    });

    if (baseErrors.nombre) e.nombre = baseErrors.nombre;
    if (baseErrors.descripcion) e.descripcion = baseErrors.descripcion;
    if (baseErrors.estado) e.estado = baseErrors.estado;
    if (baseErrors._security) e._security = baseErrors._security;

    // Refuerzo: rango de estado permitido (1|2)
    if (!["1", "2"].includes(formData.estado)) {
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

    const method = methodName === "Add" ? axios.post : axios.put;
    const url = methodName === "Add" ? "/v1/tipo-evaluacion" : `/v1/tipo-evaluacion/${selectedRow.id}`;

    method(url, payload)
      .then(() => {
        setMessage({
          open: true,
          severity: "success",
          text: methodName === "Add" ? "Tipo evaluacion creada con éxito!" : "Tipo evaluacion actualizada con éxito!"
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
          <DialogTitle>{methodName} Tipo evaluacion de Producto</DialogTitle>
          <DialogContent>
            <DialogContentText>Formulario para gestionar tipo evaluacion</DialogContentText>

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

FormTipoEvaluacion.propTypes = {
  selectedRow: PropTypes.object.isRequired,
  setSelectedRow: PropTypes.func.isRequired,
  setMessage: PropTypes.func.isRequired,
  reloadData: PropTypes.func.isRequired,
};
