import React, { useEffect, useState } from "react";
import {
  Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, Button, FormControl, InputLabel,
  Select, MenuItem, FormHelperText
} from "@mui/material";
import axios from "../axiosConfig";
import { validateCamposBase } from "../utils/validations";

export default function FormEspacio({
  open = false,
  setOpen = () => {},
  formMode = "create",
  selectedRow = null,
  bloqueId = "",
  bloques = [],
  tiposEspacio = [],
  reloadData = () => {},
  setMessage = () => {},
  authHeaders = {},
}) {
  const initialData = {
    id: null,
    bloqueId: bloqueId || "",
    tipoEspacioId: "",
    nombre: "",
    descripcion: "",
    estadoId: 1,
  };

  const [formData, setFormData] = useState(initialData);
  const [errors, setErrors] = useState({});
  const [tiposEspacioLocal, setTiposEspacioLocal] = useState([]);

  const asArray = (payload) => {
    if (Array.isArray(payload)) return payload;
    if (payload && Array.isArray(payload.content)) return payload.content;
    return [];
  };

  const toNum = (v) =>
    v === "" || v === null || v === undefined ? "" : Number(v);

  // Regex de validaciones
  const lettersOnlyRegex = /^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ\s]+$/;
  const onlyNumbersRegex = /^\d+$/;

  useEffect(() => {
    if (!tiposEspacio.length) {
      axios
        .get("/v1/tipo_espacio", authHeaders)
        .then((res) => setTiposEspacioLocal(asArray(res.data)))
        .catch(() => setTiposEspacioLocal([]));
    }
  }, [tiposEspacio]);

  useEffect(() => {
    if (!open) return;

    if (formMode === "edit" && selectedRow) {
      setFormData({
        id: selectedRow.id,
        bloqueId: toNum(selectedRow.bloqueId),
        tipoEspacioId: toNum(selectedRow.tipoEspacioId),
        nombre: selectedRow.nombre ?? "",
        descripcion: selectedRow.descripcion ?? "",
        estadoId:
          selectedRow.estadoId === 0
            ? 2
            : selectedRow.estadoId ?? 1,
      });
    } else {
      setFormData({
        ...initialData,
        bloqueId: toNum(bloqueId, ""),
      });
    }
    setErrors({});
  }, [open, formMode, selectedRow, bloqueId]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    let v = value;

    // Nombre solo letras
    if (name === "nombre") {
      v = v.replace(/[^A-Za-zÁÉÍÓÚÜÑáéíóúüñ\s]/g, "");
    }

    // Descripción: evitar solo números y limpieza básica
    if (name === "descripcion") {
      v = v.replace(/[<>/"'`;(){}\[\]\\]/g, "");
    }

    if (["tipoEspacioId", "estadoId", "bloqueId"].includes(name)) {
      setFormData((prev) => ({ ...prev, [name]: toNum(v) }));
      return;
    }

    setFormData((prev) => ({ ...prev, [name]: v }));
  };

  const validate = () => {
    const e = {};

    const baseErrors = validateCamposBase({
      nombre: formData.nombre,
      descripcion: formData.descripcion,
      estado: formData.estadoId,
    });

    if (baseErrors.nombre) e.nombre = baseErrors.nombre;
    if (baseErrors.descripcion) e.descripcion = baseErrors.descripcion;
    if (baseErrors.estado) e.estadoId = baseErrors.estado;

    // NOMBRE: obligatorio y solo letras
    if (!formData.nombre.trim()) {
      e.nombre = "El nombre es obligatorio.";
    } else if (!lettersOnlyRegex.test(formData.nombre.trim())) {
      e.nombre = "El nombre solo debe contener letras.";
    }

    // DESCRIPCIÓN: no puede ser solo números
    if (formData.descripcion && onlyNumbersRegex.test(formData.descripcion.trim())) {
      e.descripcion = "La descripción no puede ser solo números.";
    }

    if (!Number(formData.tipoEspacioId)) {
      e.tipoEspacioId = "Debe seleccionar un tipo de espacio.";
    }

    if (!Number(formData.bloqueId)) {
      e.bloqueId = "Debe seleccionar un bloque.";
    }

    if (![1, 2].includes(Number(formData.estadoId))) {
      e.estadoId = "Debe seleccionar un estado válido.";
    }

    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const buildPayload = () => ({
    nombre: formData.nombre.trim(),
    descripcion: formData.descripcion?.trim() || "",
    tipoEspacioId: formData.tipoEspacioId,
    bloqueId: formData.bloqueId,
    estadoId: formData.estadoId,
  });

  const handleSubmit = async () => {
    if (!validate()) return;

    const payload = buildPayload();

    try {
      if (formMode === "edit" && formData.id) {
        await axios.put(
          `/v1/espacio/${formData.id}`,
          { id: Number(formData.id), ...payload },
          authHeaders
        );
        setMessage({
          open: true,
          severity: "success",
          text: "Espacio actualizado correctamente.",
        });
      } else {
        await axios.post("/v1/espacio", payload, authHeaders);
        setMessage({
          open: true,
          severity: "success",
          text: "Espacio creado correctamente.",
        });
      }
      setOpen(false);
      reloadData();
    } catch (err) {
      const api = err.response?.data || {};
      setMessage({
        open: true,
        severity: "error",
        text: api.message || api.error || "Error al guardar espacio.",
      });
    }
  };

  const tipos = tiposEspacio.length ? tiposEspacio : tiposEspacioLocal;

  return (
    <Dialog open={open} onClose={() => setOpen(false)} maxWidth="sm" fullWidth>
      <DialogTitle>
        {formMode === "edit" ? "Editar Espacio" : "Nuevo Espacio"}
      </DialogTitle>

      <DialogContent>
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
          label="Descripción"
          name="descripcion"
          value={formData.descripcion}
          onChange={handleChange}
          error={!!errors.descripcion}
          helperText={errors.descripcion}
        />

        {/* 🔵 Selector de Bloque SIEMPRE visible */}
        <FormControl fullWidth margin="normal" error={!!errors.bloqueId}>
          <InputLabel>Bloque</InputLabel>
          <Select
            name="bloqueId"
            value={formData.bloqueId || ""}
            onChange={handleChange}
            label="Bloque"
          >
            {Array.isArray(bloques) &&
              bloques.map((b) => (
                <MenuItem key={b.id} value={b.id}>
                  {b.nombre}
                </MenuItem>
              ))}
          </Select>
          <FormHelperText>{errors.bloqueId}</FormHelperText>
        </FormControl>

        <FormControl fullWidth margin="normal" error={!!errors.tipoEspacioId}>
          <InputLabel>Tipo de Espacio</InputLabel>
          <Select
            name="tipoEspacioId"
            value={formData.tipoEspacioId}
            onChange={handleChange}
            label="Tipo de Espacio"
          >
            {tipos.map((t) => (
              <MenuItem key={t.id} value={Number(t.id)}>
                {t.nombre}
              </MenuItem>
            ))}
          </Select>
          <FormHelperText>{errors.tipoEspacioId}</FormHelperText>
        </FormControl>

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
          <FormHelperText>{errors.estadoId}</FormHelperText>
        </FormControl>
      </DialogContent>

      <DialogActions>
        <Button onClick={() => setOpen(false)}>Cancelar</Button>
        <Button variant="contained" onClick={handleSubmit}>
          Guardar
        </Button>
      </DialogActions>
    </Dialog>
  );
}
