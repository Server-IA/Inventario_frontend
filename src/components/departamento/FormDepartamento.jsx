import React, { useState, useEffect } from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  MenuItem,
  FormControl,
  InputLabel,
  Select,
  FormHelperText,
  DialogContentText,
} from "@mui/material";
import axios from "../axiosConfig";
import { validateCamposBase } from "../utils/validations";
import StackButtons from "../StackButtons";

// ===========================
// ERROR HANDLER
// ===========================
const getErrorMessage = (err, defaultMsg) => {
  if (err?.response?.data) {
    const { detail, title } = err.response.data;
    return detail || title || defaultMsg;
  }
  return err?.message || defaultMsg;
};

export default function FormDepartamento({
  selectedRow,
  setSelectedRow,
  setMessage,
  reloadData,
  paisId = "",
  paises = [],
  externalMethods,
}) {
  const [open, setOpen] = useState(false);
  const [openConfirm, setOpenConfirm] = useState(false);
  const [formMode, setFormMode] = useState("create");

  const initialData = {
    nombre: "",
    codigo: "",
    acronimo: "",
    estadoId: 1,
    paisId: paisId || "",
  };

  const [formData, setFormData] = useState(initialData);
  const [errors, setErrors] = useState({});

  const toNum = (v, def = 0) =>
    v === null || v === undefined || v === "" ? def : Number(v);

  const invalidCharsRegex = /[<>/"'`;(){}[\]\\]/;

  // ===========================
  // ACTIONS (BOTONES)
  // ===========================
  const create = () => {
    setFormMode("create");
    setFormData({
      ...initialData,
      estadoId: 1,
      paisId: toNum(paisId, ""),
    });
    setErrors({});
    setOpen(true);
  };

  const update = () => {
    if (!selectedRow?.id) {
      setMessage({
        open: true,
        severity: "error",
        text: "Selecciona un departamento para editar.",
      });
      return;
    }

    setFormMode("edit");
    setFormData({
      id: selectedRow.id,
      nombre: selectedRow.nombre || "",
      codigo: selectedRow.codigo || "",
      acronimo: selectedRow.acronimo || "",
      estadoId: selectedRow.estadoId,
      paisId: selectedRow.paisId,
    });

    setErrors({});
    setOpen(true);
  };

  const deleteRow = () => {
    if (!selectedRow?.id) {
      setMessage({
        open: true,
        severity: "error",
        text: "Selecciona un departamento para eliminar.",
      });
      return;
    }

    setOpenConfirm(true);
  };

  const confirmDelete = async () => {
    try {
      const res = await axios.delete(`/v1/departamento/${selectedRow.id}`);

      if (res.status === 204 || res.status === 200) {
        setMessage({
          open: true,
          severity: "success",
          text: "Departamento eliminado correctamente.",
        });
        setSelectedRow({});
        reloadData();
      }
    } catch (err) {
      const msg = getErrorMessage(
        err,
        "No se pudo eliminar el departamento."
      );

      setMessage({
        open: true,
        severity: "error",
        text: msg,
      });
    } finally {
      setOpenConfirm(false);
    }
  };

  // ===========================
  // HANDLE CHANGE
  // ===========================
  const handleChange = (e) => {
    const { name, value } = e.target;

    if (["estadoId", "paisId"].includes(name)) {
      setFormData((prev) => ({ ...prev, [name]: toNum(value, "") }));
      return;
    }

    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  // ===========================
  // VALIDATE
  // ===========================
  const validate = () => {
    const newErrors = {};

    const baseErrors = validateCamposBase({
      nombre: formData.nombre,
      descripcion: "N/A",
      estado: formData.estadoId,
    });

    if (baseErrors.nombre) newErrors.nombre = baseErrors.nombre;

    if (formMode === "edit" && baseErrors.estado) {
      newErrors.estadoId = baseErrors.estado;
    }

    if (baseErrors._security) newErrors._security = baseErrors._security;

    if (!formData.nombre?.trim()) {
      newErrors.nombre = "El nombre es obligatorio.";
    } else if (invalidCharsRegex.test(formData.nombre)) {
      newErrors.nombre = "El nombre contiene caracteres no permitidos.";
    }

    if (!formData.codigo?.toString().trim()) {
      newErrors.codigo = "El código es obligatorio.";
    }

    if (!formData.acronimo?.trim()) {
      newErrors.acronimo = "El acrónimo es obligatorio.";
    }

    if (formMode === "edit" && ![1, 2].includes(Number(formData.estadoId))) {
      newErrors.estadoId = "Debe seleccionar un estado válido.";
    }

    if (!Number(formData.paisId)) {
      newErrors.paisId = "Debe seleccionar un país.";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  // ===========================
  // SUBMIT
  // ===========================
  const handleSubmit = async () => {
    if (!validate()) return;

    const payload = {
      paisId: parseInt(formData.paisId),
      nombre: formData.nombre.trim(),
      codigo: Number(formData.codigo),
      acronimo: formData.acronimo.trim().toUpperCase(),
      estadoId: formMode === "create" ? 1 : Number(formData.estadoId),
    };
   if (!formData.paisId || isNaN(formData.paisId)) {
      setMessage({
        open: true,
        severity: "error",
        text: "El país es obligatorio.",
      });
      return;
    }
    try {
      if (formMode === "edit") {
        const res = await axios.put(
          `/v1/departamento/${formData.id}`,
          { id: Number(formData.id), ...payload }
        );

        if (res.status === 200) {
          setMessage({
            open: true,
            severity: "success",
            text: "Departamento actualizado correctamente.",
          });
        }
      } else {
        const res = await axios.post("/v1/departamento", payload);

        if (res.status === 201 || res.status === 200) {
          setMessage({
            open: true,
            severity: "success",
            text: "Departamento creado correctamente.",
          });
        }
      }

      setOpen(false);
      setSelectedRow({});
      reloadData();
    } catch (err) {
      const msg = getErrorMessage(
        err,
        "Error al guardar departamento."
      );

      setMessage({
        open: true,
        severity: "error",
        text: msg,
      });
    }
  };
  if (externalMethods) {
  externalMethods.current = {
    create,
    update,
    deleteRow,
  };
}
  // ===========================
  // RENDER
  // ===========================
  return (
    <>
      {/* ================= FORM ================= */}
      <Dialog open={open} onClose={() => setOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>
          {formMode === "edit" ? "Editar Departamento" : "Nuevo Departamento"}
        </DialogTitle>

        <DialogContent>
          {!paisId && (
            <FormControl fullWidth margin="normal" error={!!errors.paisId}>
              <InputLabel>País</InputLabel>
              <Select
                name="paisId"
                value={formData.paisId || ""}
                onChange={handleChange}
                label="País"
              >
                {paises.map((p) => (
                  <MenuItem key={p.id} value={p.id}>
                    {p.nombre}
                  </MenuItem>
                ))}
              </Select>
              {errors.paisId && (
                <FormHelperText>{errors.paisId}</FormHelperText>
              )}
            </FormControl>
          )}

          <TextField
            fullWidth
            margin="normal"
            label="Nombre"
            name="nombre"
            value={formData.nombre}
            onChange={handleChange}
            error={!!errors.nombre}
            helperText={errors.nombre}
          />

          <TextField
            fullWidth
            margin="normal"
            label="Código"
            name="codigo"
            value={formData.codigo}
            onChange={handleChange}
            error={!!errors.codigo}
            helperText={errors.codigo}
          />

          <TextField
            fullWidth
            margin="normal"
            label="Acrónimo"
            name="acronimo"
            value={formData.acronimo}
            onChange={handleChange}
            error={!!errors.acronimo}
            helperText={errors.acronimo}
          />

          {/* SOLO EDIT */}
          {formMode === "edit" && (
            <FormControl fullWidth margin="normal" error={!!errors.estadoId}>
              <InputLabel>Estado</InputLabel>
              <Select
                name="estadoId"
                value={formData.estadoId}
                onChange={handleChange}
                label="Estado"
              >
                <MenuItem value={1}>Activo</MenuItem>
                <MenuItem value={2}>Inactivo</MenuItem>
              </Select>
              {errors.estadoId && (
                <FormHelperText>{errors.estadoId}</FormHelperText>
              )}
            </FormControl>
          )}
        </DialogContent>

        <DialogActions>
          <Button onClick={() => setOpen(false)}>Cancelar</Button>
          <Button onClick={handleSubmit} variant="contained">
            Guardar
          </Button>
        </DialogActions>
      </Dialog>

      {/* ================= DELETE MODAL ================= */}
      <Dialog open={openConfirm} onClose={() => setOpenConfirm(false)}>
        <DialogTitle>Confirmar eliminación</DialogTitle>
        <DialogContent>
          <DialogContentText>
            ¿Estás seguro de eliminar este departamento?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenConfirm(false)}>Cancelar</Button>
          <Button color="error" onClick={confirmDelete}>
            Eliminar
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
}