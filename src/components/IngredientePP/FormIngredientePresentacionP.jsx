// src/components/IngredientePP/FormIngredientePresentacionP.jsx
import React, { useEffect, useState } from "react";
import PropTypes from "prop-types";
import axios from "../axiosConfig";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  FormHelperText,
} from "@mui/material";

export default function FormIngredientePresentacionP({
  open = false,
  setOpen = () => {},
  selectedRow = null,
  setSelectedRow = () => {},
  setMessage = () => {},
  reloadData = () => {},
}) {
  const initialForm = {
    id: null,
    ingredienteId: "",
    presentacionProductoId: "",
    unidadId: "",
    estadoId: 1, // Activo por defecto
    cantidad: "",
  };

  const [formData, setFormData] = useState(initialForm);
  const [errors, setErrors] = useState({});

  // catálogos
  const [ingredientes, setIngredientes] = useState([]);
  const [presentaciones, setPresentaciones] = useState([]);
  const [unidades, setUnidades] = useState([]);

  const takeList = (data) =>
    Array.isArray(data)
      ? data
      : Array.isArray(data?.content)
      ? data.content
      : Array.isArray(data?.data)
      ? data.data
      : [];

  // cargar catálogos + setear datos cuando se abre el dialog
  useEffect(() => {
    if (!open) return;

    const loadCatalogs = async () => {
      try {
        const [resIng, resPres, resUni] = await Promise.all([
          axios.get("/v1/items/ingrediente/0"),
          axios.get("/v1/items/producto_presentacion/0"),
          axios.get("/v1/items/unidad/0"),
        ]);

        setIngredientes(takeList(resIng.data));
        setPresentaciones(takeList(resPres.data));
        setUnidades(takeList(resUni.data));
      } catch (err) {
        console.error("[FormIngredientePP] Error cargando catálogos:", err);
        setIngredientes([]);
        setPresentaciones([]);
        setUnidades([]);
      }
    };

    loadCatalogs();

    // Si hay selectedRow -> modo editar
    if (selectedRow && (selectedRow.id || selectedRow.idIngredientePresentacionProducto)) {
      setFormData({
        id: selectedRow.idIngredientePresentacionProducto ?? selectedRow.id ?? null,
        ingredienteId:
          selectedRow.ingredienteId ??
          selectedRow.ingrediente?.idIngrediente ??
          "",
        presentacionProductoId:
          selectedRow.presentacionProductoId ??
          selectedRow.idPresentacionProducto ??
          selectedRow.presentacionProducto?.id ??
          "",
        unidadId:
          selectedRow.unidadId ??
          selectedRow.ingrediente?.idUnidad ??
          selectedRow.unidad?.id ??
          "",
        estadoId:
          selectedRow.estadoId ??
          selectedRow.ingrediente?.idEstado ??
          selectedRow.estado?.id ??
          1,
        cantidad:
          selectedRow.cantidad ??
          selectedRow.ingrediente?.cantidad ??
          "",
      });
    } else {
      // modo crear
      setFormData(initialForm);
      setSelectedRow(null);
    }

    setErrors({});
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [open]);

  // ---------- HANDLERS ----------
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    setErrors((prev) => ({ ...prev, [name]: "" }));
  };

  // ---------- VALIDACIONES ----------
  const validate = () => {
    const e = {};

    if (!formData.ingredienteId) e.ingredienteId = "Obligatorio";
    if (!formData.presentacionProductoId) e.presentacionProductoId = "Obligatorio";
    if (!formData.unidadId) e.unidadId = "Obligatorio";
    if (!formData.estadoId) e.estadoId = "Obligatorio";

    const cantStr = String(formData.cantidad ?? "").trim();
    if (!cantStr) {
      e.cantidad = "Obligatorio";
    } else {
      const num = Number(cantStr);
      if (!Number.isFinite(num) || num <= 0) {
        e.cantidad = "Debe ser un número mayor que 0";
      }
    }

    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const asIntOrNull = (v) => {
    if (v === "" || v === null || v === undefined) return null;
    const n = Number(v);
    return Number.isFinite(n) ? n : null;
  };

  const asNumberOrNull = (v) => {
    if (v === "" || v === null || v === undefined) return null;
    const n = Number(v);
    return Number.isFinite(n) ? n : null;
  };

  const handleSubmit = async () => {
    if (!validate()) return;

    const isEdit = !!formData.id;

    const payload = {
      ingredienteId: asIntOrNull(formData.ingredienteId),
      presentacionProductoId: asIntOrNull(formData.presentacionProductoId),
      unidadId: asIntOrNull(formData.unidadId),
      estadoId: asIntOrNull(formData.estadoId),
      cantidad: asNumberOrNull(formData.cantidad),
    };

    // SIN "api", recuerda: axiosConfig ya se encarga del host/base
    const baseUrl = "/v1/ingrediente-presentacion-producto";
    const url = isEdit ? `${baseUrl}/${formData.id}` : baseUrl;
    const method = isEdit ? axios.put : axios.post;

    try {
      await method(url, payload);
      setMessage({
        open: true,
        severity: "success",
        text: isEdit
          ? "Ingrediente–Presentación actualizado correctamente"
          : "Ingrediente–Presentación creado correctamente",
      });
      reloadData();
      setOpen(false);
    } catch (err) {
      const status = err?.response?.status;
      const api = err?.response?.data;

      const serverMsg =
        api?.message ||
        api?.error ||
        (Array.isArray(api?.errors) && api.errors.join(", ")) ||
        (Array.isArray(api?.fieldErrors) &&
          api.fieldErrors.map((fe) => `${fe.field}: ${fe.message}`).join(" | ")) ||
        "Error al guardar. Revisa los datos.";

      console.error("[FormIngredientePP] Error en submit:", err);
      setMessage({
        open: true,
        severity: "error",
        text: `Error ${status ?? ""} - ${serverMsg}`,
      });
    }
  };

  const handleClose = () => {
    setOpen(false);
  };

  // ---------- RENDER ----------
  return (
    <Dialog open={open} onClose={handleClose} fullWidth maxWidth="sm">
      <DialogTitle>
        {formData.id
          ? "Editar Ingrediente – Presentación de Producto"
          : "Nuevo Ingrediente – Presentación de Producto"}
      </DialogTitle>

      <DialogContent sx={{ display: "flex", flexDirection: "column", gap: 2, mt: 1 }}>
        {/* Presentación de Producto */}
        <FormControl fullWidth error={!!errors.presentacionProductoId}>
          <InputLabel>Presentación de Producto</InputLabel>
          <Select
            name="presentacionProductoId"
            value={formData.presentacionProductoId}
            label="Presentación de Producto"
            onChange={handleChange}
          >
            {presentaciones.map((p) => (
              <MenuItem key={p.id} value={p.id}>
                {p.nombre || p.name || p.descripcion}
              </MenuItem>
            ))}
          </Select>
          {errors.presentacionProductoId && (
            <FormHelperText>{errors.presentacionProductoId}</FormHelperText>
          )}
        </FormControl>

        {/* Ingrediente */}
        <FormControl fullWidth error={!!errors.ingredienteId}>
          <InputLabel>Ingrediente</InputLabel>
          <Select
            name="ingredienteId"
            value={formData.ingredienteId}
            label="Ingrediente"
            onChange={handleChange}
          >
            {ingredientes.map((ing) => (
              <MenuItem key={ing.id} value={ing.id}>
                {ing.nombre || ing.name || ing.nombreIngrediente}
              </MenuItem>
            ))}
          </Select>
          {errors.ingredienteId && (
            <FormHelperText>{errors.ingredienteId}</FormHelperText>
          )}
        </FormControl>

        {/* Cantidad */}
        <TextField
          label="Cantidad"
          name="cantidad"
          type="number"
          value={formData.cantidad}
          onChange={handleChange}
          error={!!errors.cantidad}
          helperText={errors.cantidad}
          fullWidth
          inputProps={{ min: 0, step: "0.01" }}
        />

        {/* Unidad */}
        <FormControl fullWidth error={!!errors.unidadId}>
          <InputLabel>Unidad</InputLabel>
          <Select
            name="unidadId"
            value={formData.unidadId}
            label="Unidad"
            onChange={handleChange}
          >
            {unidades.map((u) => (
              <MenuItem key={u.id} value={u.id}>
                {u.nombre || u.name}
              </MenuItem>
            ))}
          </Select>
          {errors.unidadId && (
            <FormHelperText>{errors.unidadId}</FormHelperText>
          )}
        </FormControl>

        {/* Estado */}
        <FormControl fullWidth error={!!errors.estadoId}>
          <InputLabel>Estado</InputLabel>
          <Select
            name="estadoId"
            value={formData.estadoId}
            label="Estado"
            onChange={handleChange}
          >
            <MenuItem value={1}>Activo</MenuItem>
            <MenuItem value={2}>Inactivo</MenuItem>
          </Select>
          {errors.estadoId && (
            <FormHelperText>{errors.estadoId}</FormHelperText>
          )}
        </FormControl>
      </DialogContent>

      <DialogActions>
        <Button onClick={handleClose}>Cancelar</Button>
        <Button onClick={handleSubmit} variant="contained">
          Guardar
        </Button>
      </DialogActions>
    </Dialog>
  );
}

FormIngredientePresentacionP.propTypes = {
  open: PropTypes.bool,
  setOpen: PropTypes.func,
  selectedRow: PropTypes.object,
  setSelectedRow: PropTypes.func,
  setMessage: PropTypes.func,
  reloadData: PropTypes.func,
};
