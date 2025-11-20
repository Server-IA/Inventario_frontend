// src/components/Proveedor/FormProveedor.jsx
import React, { useEffect, useState } from "react";
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
} from "@mui/material";
import axios from "../axiosConfig";

export default function FormProveedor({
  open = false,
  setOpen = () => {},
  selectedRow = null,
  formMode = "create", // "create" | "edit"
  setMessage,
  reloadData,
  setSelectedRow,
  tiposIdentificacion = [],
}) {
  const initialData = {
    id: null,
    nombre: "",
    identificacion: "",
    contacto: "",
    correo: "",
    celular: "",
    empresaId: "",
    tipoIdentificacionId: "",
    estadoId: 1,
    fechaCreacion: "",
  };

  const [formData, setFormData] = useState(initialData);
  const [errors, setErrors] = useState({});

  const getLocalDateTime = () => {
    const now = new Date();
    return new Date(now.getTime() - now.getTimezoneOffset() * 60000)
      .toISOString()
      .slice(0, 16); // yyyy-MM-ddTHH:mm
  };

  useEffect(() => {
    if (open) {
      if (formMode === "edit" && selectedRow) {
        setFormData({
          ...initialData,
          ...selectedRow,
          fechaCreacion: selectedRow.fechaCreacion
            ? selectedRow.fechaCreacion.slice(0, 16)
            : getLocalDateTime(),
        });
      } else {
        setFormData({
          ...initialData,
          fechaCreacion: getLocalDateTime(),
        });
      }
      setErrors({});
    }
  }, [open, formMode, selectedRow]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    let safeValue = value;

    // BLOQUEO EN TIEMPO REAL SEGÚN CAMPO
    if (name === "nombre") {
      // Solo letras y espacios
      safeValue = value.replace(/[^A-Za-zÁÉÍÓÚáéíóúÑñ ]/g, "");
    }

    if (name === "identificacion" || name === "celular") {
      // Solo números
      safeValue = value.replace(/\D/g, "");
    }

    if (name === "contacto") {
      // Solo letras, números y espacios
      safeValue = value.replace(/[^A-Za-zÁÉÍÓÚáéíóúÑñ0-9 ]/g, "");
    }

    setFormData((prev) => ({ ...prev, [name]: safeValue }));
    setErrors((prev) => ({ ...prev, [name]: "" }));
  };

  const validate = () => {
    const newErrors = {};

    // === NOMBRE ===
    if (!formData.nombre?.trim()) {
      newErrors.nombre = "El nombre es obligatorio";
    } else if (!/^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$/.test(formData.nombre.trim())) {
      newErrors.nombre = "El nombre solo puede contener letras y espacios";
    }

    // === TIPO IDENTIFICACIÓN ===
    if (!formData.tipoIdentificacionId) {
      newErrors.tipoIdentificacionId = "Seleccione un tipo de identificación";
    }

    // === IDENTIFICACIÓN (solo números) ===
    if (!formData.identificacion?.trim()) {
      newErrors.identificacion = "La identificación es obligatoria";
    } else if (!/^\d+$/.test(formData.identificacion.trim())) {
      newErrors.identificacion = "La identificación solo puede contener números";
    }

    // === CONTACTO (si se llena, solo letras, números y espacios) ===
    if (formData.contacto?.trim()) {
      if (!/^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9 ]+$/.test(formData.contacto.trim())) {
        newErrors.contacto =
          "El contacto solo puede contener letras, números y espacios";
      }
    }

    // === CELULAR (obligatorio, solo números) ===
    if (!formData.celular?.trim()) {
      newErrors.celular = "El celular es obligatorio";
    } else if (!/^\d+$/.test(formData.celular.trim())) {
      newErrors.celular = "El celular solo puede contener números";
    }

    // === CORREO (opcional pero debe ser válido si se escribe) ===
    if (formData.correo?.trim()) {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(formData.correo.trim())) {
        newErrors.correo = "Ingrese un correo electrónico válido";
      }
    }

    // === ESTADO ===
    if (formData.estadoId !== 0 && formData.estadoId !== 1) {
      newErrors.estadoId = "Seleccione un estado válido";
    }

    // === FECHA CREACIÓN ===
    const isValidFecha =
      /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}$/.test(formData.fechaCreacion);
    if (!formData.fechaCreacion || !isValidFecha) {
      newErrors.fechaCreacion = "Debe seleccionar una fecha válida";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async () => {
    if (!validate()) {
      setMessage?.({
        open: true,
        severity: "error",
        text: "Por favor revisa los campos marcados en rojo.",
      });
      return;
    }

    try {
      const tokenData = JSON.parse(localStorage.getItem("tokenData") || "{}");
      const dataEnviar = {
        ...formData,
        empresaId: formData.empresaId || tokenData.empresaId || 0,
        fechaCreacion: new Date(formData.fechaCreacion).toISOString(),
      };

      if (formMode === "edit") {
        await axios.put(`/v1/proveedor/${formData.id}`, dataEnviar);
        setMessage({
          open: true,
          severity: "success",
          text: "Proveedor actualizado correctamente.",
        });
      } else {
        await axios.post("/v1/proveedor", dataEnviar);
        setMessage({
          open: true,
          severity: "success",
          text: "Proveedor creado correctamente.",
        });
      }

      setOpen(false);
      setSelectedRow(null);
      reloadData?.();
    } catch (err) {
      setMessage({
        open: true,
        severity: "error",
        text: err.response?.data?.message || "Error al guardar proveedor.",
      });
    }
  };

  return (
    <Dialog open={open} onClose={() => setOpen(false)} fullWidth maxWidth="md">
      <DialogTitle>
        {formMode === "edit" ? "Editar Proveedor" : "Nuevo Proveedor"}
      </DialogTitle>

      <DialogContent>
        {/* Nombre */}
        <TextField
          fullWidth
          margin="dense"
          label="Nombre"
          name="nombre"
          value={formData.nombre}
          onChange={handleChange}
          error={!!errors.nombre}
          helperText={errors.nombre}
        />

        {/* Fecha creación */}
        <TextField
          fullWidth
          margin="dense"
          label="Fecha de Creación"
          name="fechaCreacion"
          type="datetime-local"
          value={formData.fechaCreacion}
          onChange={handleChange}
          InputLabelProps={{ shrink: true }}
          error={!!errors.fechaCreacion}
          helperText={errors.fechaCreacion}
        />

        {/* Tipo identificación */}
        <FormControl
          fullWidth
          margin="dense"
          error={!!errors.tipoIdentificacionId}
        >
          <InputLabel>Tipo de Identificación</InputLabel>
          <Select
            name="tipoIdentificacionId"
            value={formData.tipoIdentificacionId}
            onChange={handleChange}
            label="Tipo de Identificación"
          >
            {tiposIdentificacion.map((tipo) => (
              <MenuItem key={tipo.id} value={tipo.id}>
                {tipo.name}
              </MenuItem>
            ))}
          </Select>
          {errors.tipoIdentificacionId && (
            <FormHelperText>{errors.tipoIdentificacionId}</FormHelperText>
          )}
        </FormControl>

        {/* Identificación */}
        <TextField
          fullWidth
          margin="dense"
          label="Identificación"
          name="identificacion"
          value={formData.identificacion}
          onChange={handleChange}
          error={!!errors.identificacion}
          helperText={errors.identificacion}
        />

        {/* Celular */}
        <TextField
          fullWidth
          margin="dense"
          label="Celular"
          name="celular"
          value={formData.celular}
          onChange={handleChange}
          error={!!errors.celular}
          helperText={errors.celular}
        />

        {/* Contacto */}
        <TextField
          fullWidth
          margin="dense"
          label="Contacto"
          name="contacto"
          value={formData.contacto}
          onChange={handleChange}
          error={!!errors.contacto}
          helperText={errors.contacto}
        />

        {/* Correo */}
        <TextField
          fullWidth
          margin="dense"
          label="Correo"
          name="correo"
          value={formData.correo}
          onChange={handleChange}
          error={!!errors.correo}
          helperText={errors.correo}
        />

        {/* Estado */}
        <FormControl fullWidth margin="dense" error={!!errors.estadoId}>
          <InputLabel>Estado</InputLabel>
          <Select
            name="estadoId"
            value={formData.estadoId}
            onChange={handleChange}
            label="Estado"
          >
            <MenuItem value={1}>Activo</MenuItem>
            <MenuItem value={0}>Inactivo</MenuItem>
          </Select>
          {errors.estadoId && (
            <FormHelperText>{errors.estadoId}</FormHelperText>
          )}
        </FormControl>
      </DialogContent>

      <DialogActions>
        <Button onClick={() => setOpen(false)}>Cancelar</Button>
        <Button onClick={handleSubmit} variant="contained">
          Guardar
        </Button>
      </DialogActions>
    </Dialog>
  );
}
