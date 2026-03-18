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
  if (err?.response?.data) {
    const data = err.response.data;
    return data.detail || data.title || defaultMsg;
  }
  return err?.message || defaultMsg;
};

export default function FormPais({
  selectedRow,
  setSelectedRow,
  setMessage,
  reloadData,
}) {
  const [open, setOpen] = React.useState(false);
  const [openConfirm, setOpenConfirm] = React.useState(false);
  const [methodName, setMethodName] = React.useState("");

  const initialData = {
    nombre: "",
    codigo: "",
    acronimo: "",
    estado: "",
  };

  const [formData, setFormData] = React.useState(initialData);
  const [errors, setErrors] = React.useState({});

  const lettersSpacesRegex = /[^A-Za-zÁÉÍÓÚÜÑáéíóúüñ\s]/g;
  const invalidCharsRegex = /[<>/"'`;(){}[\]\\]/;

  // ======================
  // CREATE
  // ======================
  const create = () => {
    setFormData({
      ...initialData,
      estado: "1", // automático
    });
    setErrors({});
    setMethodName("Crear");
    setOpen(true);
  };

  // ======================
  // UPDATE
  // ======================
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
    setMethodName("Actualizar ");
    setOpen(true);
  };

  // ======================
  // DELETE (abre modal)
  // ======================
  const deleteRow = () => {
    if (!selectedRow?.id) {
      setMessage({
        open: true,
        severity: "error",
        text: "Selecciona un país para eliminar.",
      });
      return;
    }

    setOpenConfirm(true);
  };

  // ======================
  // CONFIRM DELETE
  // ======================
  const confirmDelete = () => {
    axios
      .delete(`/v1/pais/${selectedRow.id}`)
      .then((res) => {
        if (res.status === 204 || res.status === 200) {
          setMessage({
            open: true,
            severity: "success",
            text: "País eliminado correctamente.",
          });
          setSelectedRow({});
          reloadData();
        }
      })
      .catch((err) => {
        const msg = getErrorMessage(
          err,
          "No se pudo eliminar el país."
        );
        setMessage({
          open: true,
          severity: "error",
          text: msg,
        });
      })
      .finally(() => setOpenConfirm(false));
  };

  const handleClose = () => setOpen(false);

  // ======================
  // HANDLE CHANGE
  // ======================
  const handleChange = (e) => {
    let { name, value } = e.target;

    if (name === "nombre") {
      value = value.replace(lettersSpacesRegex, "");
    }

    if (name === "codigo") {
      value = value.replace(/[^0-9]/g, "");
    }

    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  // ======================
  // VALIDATE
  // ======================
  const validate = () => {
    const newErrors = {};

    const baseErrors = validateCamposBase({
      nombre: formData.nombre,
      descripcion: "N/A",
      estado: formData.estado,
    });

    if (baseErrors.nombre) newErrors.nombre = baseErrors.nombre;
    if (baseErrors.estado && methodName === "Actualizar ")
      newErrors.estado = baseErrors.estado;

    if (baseErrors._security) newErrors._security = baseErrors._security;

    if (!formData.nombre.trim()) {
      newErrors.nombre = "El nombre es obligatorio.";
    } else if (invalidCharsRegex.test(formData.nombre)) {
      newErrors.nombre = "El nombre contiene caracteres no permitidos.";
    }

    if (!formData.codigo.toString().trim()) {
      newErrors.codigo = "El código es obligatorio.";
    }

    if (!formData.acronimo.trim()) {
      newErrors.acronimo = "El acrónimo es obligatorio.";
    } else if (invalidCharsRegex.test(formData.acronimo)) {
      newErrors.acronimo =
        "El acrónimo contiene caracteres no permitidos.";
    }

    if (
      methodName === "Actualizar " &&
      !["1", "2"].includes(formData.estado)
    ) {
      newErrors.estado = "Debe seleccionar un estado válido.";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  // ======================
  // SUBMIT
  // ======================
  const handleSubmit = (event) => {
    event.preventDefault();
    if (!validate()) return;

    const payload = {
      nombre: formData.nombre.trim(),
      codigo: parseInt(formData.codigo),
      acronimo: formData.acronimo.trim().toUpperCase(),
      estadoId:
        methodName === "Crear"
          ? 1
          : parseInt(formData.estado),
    };

    const isAdd = methodName === "Crear";
    const method = isAdd ? axios.post : axios.put;
    const url = isAdd
      ? "/v1/pais"
      : `/v1/pais/${selectedRow.id}`;

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
        const msg = getErrorMessage(
          err,
          "No se pudo guardar el país."
        );
        setMessage({
          open: true,
          severity: "error",
          text: msg,
        });
      });
  };

  return (
    <>
      <StackButtons methods={{ create, update, deleteRow }} />

      {/* ================= FORM MODAL ================= */}
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

            {/* SOLO EN UPDATE */}
            {methodName === "Actualizar " && (
              <FormControl fullWidth margin="normal" error={!!errors.estado}>
                <InputLabel>Estado</InputLabel>
                <Select
                  name="estado"
                  value={formData.estado}
                  onChange={handleChange}
                  label="Estado"
                >
                  <MenuItem value="1">Activo</MenuItem>
                  <MenuItem value="2">Inactivo</MenuItem>
                </Select>
                {errors.estado && (
                  <FormHelperText>{errors.estado}</FormHelperText>
                )}
              </FormControl>
            )}
          </DialogContent>

          <DialogActions>
            <Button onClick={handleClose}>Cancelar</Button>
            <Button type="submit">{methodName}</Button>
          </DialogActions>
        </form>
      </Dialog>

      {/* ================= CONFIRM DELETE ================= */}
      <Dialog
        open={openConfirm}
        onClose={() => setOpenConfirm(false)}
      >
        <DialogTitle>Confirmar eliminación</DialogTitle>
        <DialogContent>
          <DialogContentText>
            ¿Estás seguro de eliminar este país?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenConfirm(false)}>
            Cancelar
          </Button>
          <Button color="error" onClick={confirmDelete}>
            Eliminar
          </Button>
        </DialogActions>
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