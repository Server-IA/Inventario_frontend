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

import { validateCamposBase } from "../utils/validations";

const getErrorMessage = (err, defaultMsg) => {
  if (err?.response) {
    const data = err.response.data;
    if (typeof data === "string") return data;
    return data?.message || data?.error || data?.detalle || defaultMsg;
  }
  if (err?.message) return err.message;
  return defaultMsg;
};

export default function FormPais({
  selectedRow,
  setSelectedRow,
  setMessage,
  reloadData,
}) {
  const [open, setOpen] = React.useState(false);
  const [methodName, setMethodName] = React.useState("");

  const initialData = {
    nombre: "",
    codigo: "",
    acronimo: "",
    estado: "",
  };

  const [formData, setFormData] = React.useState(initialData);
  const [errors, setErrors] = React.useState({});

  // solo letras (incluye tildes y espacios)
  const lettersSpacesRegex = /[^A-Za-zÁÉÍÓÚÜÑáéíóúüñ\s]/g;
  // caracteres especiales NO permitidos (para validación extra)
  const invalidCharsRegex = /[<>/"'`;(){}[\]\\]/;

  const create = () => {
    setFormData(initialData);
    setErrors({});
    setMethodName("Add");
    setOpen(true);
  };

  const update = () => {
    if (!selectedRow?.id) {
      setMessage({
        open: true,
        severity: "error",
        text: "Selecciona un país para editar.",
      });
      return;
    }

    setFormData({
      nombre: selectedRow.nombre || "",
      codigo: selectedRow.codigo || "",
      acronimo: selectedRow.acronimo || "",
      estado: selectedRow.estadoId?.toString() || "",
    });
    setErrors({});
    setMethodName("Update");
    setOpen(true);
  };

  const deleteRow = () => {
    if (!selectedRow?.id) {
      setMessage({
        open: true,
        severity: "error",
        text: "Selecciona un país para eliminar.",
      });
      return;
    }

    axios
      .delete(`/v1/pais/${selectedRow.id}`)
      .then(() => {
        setMessage({
          open: true,
          severity: "success",
          text: "País eliminado correctamente.",
        });
        setSelectedRow({});
        reloadData();
      })
      .catch((err) => {
        let userMsg = "No se pudo eliminar el país.";

        if (err?.response) {
          const { status, data } = err.response;
          const rawMsg =
            typeof data === "string"
              ? data
              : data?.message || data?.error || data?.detalle || "";
          const lower = (rawMsg || "").toLowerCase();

          // 🔎 Detectar caso de país con departamentos asociados
          if (
            status === 409 || // típico de FK/CONFLICT
            lower.includes("departament") || // 'departamento' / 'departamentos'
            lower.includes("foreign key") ||
            lower.includes("clave foránea") ||
            lower.includes("fk")
          ) {
            userMsg =
              "No se puede eliminar el país porque tiene departamentos asociados.";
          } else if (rawMsg) {
            userMsg = rawMsg;
          }
        } else if (err?.message) {
          userMsg = err.message;
        }

        setMessage({
          open: true,
          severity: "error",
          text: userMsg,
        });
      });
  };

  const handleClose = () => setOpen(false);

  const handleChange = (e) => {
    let { name, value } = e.target;

    // Nombre: solo letras y espacios
    if (name === "nombre") {
      value = value.replace(lettersSpacesRegex, "");
    }

    // Código: solo números
    if (name === "codigo") {
      value = value.replace(/[^0-9]/g, "");
    }

    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const validate = () => {
    const newErrors = {};

    const baseErrors = validateCamposBase({
      nombre: formData.nombre,
      descripcion: "N/A",
      estado: formData.estado,
    });

    if (baseErrors.nombre) newErrors.nombre = baseErrors.nombre;
    if (baseErrors.estado) newErrors.estado = baseErrors.estado;
    if (baseErrors._security) newErrors._security = baseErrors._security;

    // nombre obligatorio
    if (!formData.nombre.trim()) {
      newErrors.nombre = newErrors.nombre || "El nombre es obligatorio.";
    } else if (invalidCharsRegex.test(formData.nombre)) {
      newErrors.nombre = "El nombre contiene caracteres no permitidos.";
    }

    // código obligatorio
    if (!formData.codigo.toString().trim()) {
      newErrors.codigo = "El código es obligatorio.";
    }

    // acrónimo obligatorio
    if (!formData.acronimo.trim()) {
      newErrors.acronimo = "El acrónimo es obligatorio.";
    } else if (invalidCharsRegex.test(formData.acronimo)) {
      newErrors.acronimo = "El acrónimo contiene caracteres no permitidos.";
    }

    // estado válido
    if (!["1", "2"].includes(formData.estado)) {
      newErrors.estado =
        newErrors.estado || "Debe seleccionar un estado válido.";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    if (!validate()) return;

    const payload = {
      nombre: formData.nombre.trim(),
      codigo: parseInt(formData.codigo),
      acronimo: formData.acronimo.trim().toUpperCase(),
      estadoId: parseInt(formData.estado),
    };

    const isAdd = methodName === "Add";
    const method = isAdd ? axios.post : axios.put;
    const url = isAdd ? "/v1/pais" : `/v1/pais/${selectedRow.id}`;

    method(url, payload)
      .then(() => {
        setMessage({
          open: true,
          severity: "success",
          text: isAdd
            ? "País creado con éxito!"
            : "País actualizado con éxito!",
        });
        setOpen(false);
        setSelectedRow({});
        reloadData();
      })
      .catch((err) => {
        const msg = getErrorMessage(err, "No se pudo guardar el país.");
        setMessage({
          open: true,
          severity: "error",
          text: `Error al guardar país: ${msg}`,
        });
      });
  };

  return (
    <>
      <StackButtons methods={{ create, update, deleteRow }} />

      <Dialog open={open} onClose={handleClose}>
        <form onSubmit={handleSubmit}>
          <DialogTitle>{methodName} País</DialogTitle>

          <DialogContent>
            <DialogContentText>
              {errors._security || "Formulario para gestionar país"}
            </DialogContentText>

            <TextField
              fullWidth
              margin="dense"
              name="nombre"
              label="Nombre del País"
              value={formData.nombre}
              onChange={handleChange}
              error={!!errors.nombre}
              helperText={errors.nombre}
            />

            <TextField
              fullWidth
              margin="dense"
              name="codigo"
              label="Código"
              value={formData.codigo}
              onChange={handleChange}
              error={!!errors.codigo}
              helperText={errors.codigo}
            />

            <TextField
              fullWidth
              margin="dense"
              name="acronimo"
              label="Acrónimo"
              inputProps={{ maxLength: 3 }}
              value={formData.acronimo}
              onChange={handleChange}
              error={!!errors.acronimo}
              helperText={errors.acronimo}
            />

            <FormControl fullWidth margin="normal" error={!!errors.estado}>
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
              {errors.estado && (
                <FormHelperText>{errors.estado}</FormHelperText>
              )}
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

FormPais.propTypes = {
  selectedRow: PropTypes.object.isRequired,
  setSelectedRow: PropTypes.func.isRequired,
  setMessage: PropTypes.func.isRequired,
  reloadData: PropTypes.func.isRequired,
};
