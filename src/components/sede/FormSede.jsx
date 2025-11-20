import React, { useEffect, useState } from "react";
import {
  Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, Button, FormControl, InputLabel,
  Select, MenuItem, FormHelperText
} from "@mui/material";
import axios from "../axiosConfig";
import { validateCamposBase } from "../utils/validations";

export default function FormSede({
  open = false,
  setOpen = () => {},
  formMode = "create",
  selectedRow = null,
  municipioId = "",
  grupos = [],
  tiposSede = [],
  reloadData = () => {},
  setMessage = () => {},
  authHeaders = {},
}) {
  const initialData = {
    id: null,
    grupoId: "",
    tipoSedeId: "",
    nombre: "",
    municipioId: municipioId || "",
    geolocalizacion: "",
    coordenadas: "",
    area: "",
    comuna: "",
    descripcion: "",
    estadoId: 1,
  };

  const [formData, setFormData] = useState(initialData);
  const [errors, setErrors] = useState({});
  const [municipiosOpts, setMunicipiosOpts] = useState([]);

  const invalidCharsRegex = /[<>/"'`;(){}\[\]\\]/g;
  const sqliWordsRegex =
    /\b(select|insert|update|delete|drop|union|exec|xp_|information_schema)\b|--|\/\*|\*\//i;

  const cleanText = (v = "") => v.replace(invalidCharsRegex, "");

  const toNum = (v) =>
    v === null || v === undefined || v === "" ? "" : Number(v);

  const getSafeId = (...values) => {
    for (const v of values) {
      const n = Number(v);
      if (!isNaN(n) && n > 0) return n;
    }
    return "";
  };

  useEffect(() => {
    if (!open) return;
    axios
      .get("/v1/items/municipio/0", { ...authHeaders })
      .then((res) => {
        const arr = Array.isArray(res.data) ? res.data : [];
        setMunicipiosOpts(
          arr.map((m) => ({
            id: Number(m.id),
            nombre: m.nombre ?? m.name ?? String(m.id),
          }))
        );
      })
      .catch(() => setMunicipiosOpts([]));
  }, [open]);

  useEffect(() => {
    if (!open) return;

    if (formMode === "edit" && selectedRow) {
      setFormData({
        id: toNum(selectedRow.id),
        nombre: selectedRow.nombre ?? "",
        grupoId: getSafeId(selectedRow.grupoId, selectedRow?.grupo?.id),
        tipoSedeId: getSafeId(
          selectedRow.tipoSedeId,
          selectedRow?.tipoSede?.id
        ),
        municipioId: getSafeId(
          selectedRow.municipioId,
          selectedRow?.municipio?.id,
          municipioId
        ),
        geolocalizacion: selectedRow.geolocalizacion ?? "",
        coordenadas: selectedRow.coordenadas ?? "",
        area: selectedRow.area ?? "",
        comuna: selectedRow.comuna ?? "",
        descripcion: selectedRow.descripcion ?? "",
        estadoId: toNum(selectedRow.estadoId) || 1,
      });
    } else {
      setFormData({
        ...initialData,
        municipioId: getSafeId(municipioId),
      });
    }

    setErrors({});
  }, [open, formMode, selectedRow, municipioId]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    let v = value;

    // Limpiar textos de caracteres especiales peligrosos
    if (["nombre", "descripcion"].includes(name)) {
      v = cleanText(v);
      if (sqliWordsRegex.test(v)) {
        // Por si quieres marcar algún error de seguridad:
        // aquí solo limpiamos, no seteamos error directo
      }
    }

    // Geolocalización: SOLO NÚMEROS (no negativos, sin signos)
    if (name === "geolocalizacion") {
      v = value.replace(/[^0-9]/g, "");
    }

    // Coordenadas: permitir dígitos, punto, coma, espacios y signo -
    if (name === "coordenadas") {
      v = value.replace(/[^0-9.,\s-]/g, "");
    }

    // Área: números y punto, sin signos
    if (name === "area") {
      v = value.replace(/[^0-9.]/g, "");
    }

    // COMUNA como select numérico (1-10)
    if (name === "comuna") {
      v = Number(v);
    }

    const newVal =
      ["estadoId", "grupoId", "tipoSedeId", "municipioId"].includes(name)
        ? toNum(v)
        : v;

    setFormData((prev) => ({ ...prev, [name]: newVal }));
  };

  const parseCoordinates = (value = "") => {
    if (!value.trim()) return null;
    // Separamos por coma o espacio
    const parts = value.split(/[,\s]+/).filter(Boolean);
    if (parts.length !== 2) return null;

    const lat = Number(parts[0].replace(",", "."));
    const lon = Number(parts[1].replace(",", "."));

    if (Number.isNaN(lat) || Number.isNaN(lon)) return null;
    return { lat, lon };
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
    if (baseErrors._security) e._security = baseErrors._security;

    // Nombre: obligatorio + NO solo números
    if (!formData.nombre.trim()) {
      e.nombre = "El nombre es obligatorio.";
    } else if (/^\d+$/.test(formData.nombre.trim())) {
      e.nombre = "El nombre no puede ser solo números.";
    }

    // Grupo / Tipo Sede / Municipio obligatorios
    if (!Number(formData.grupoId))
      e.grupoId = "Debe seleccionar un grupo.";
    if (!Number(formData.tipoSedeId))
      e.tipoSedeId = "Debe seleccionar un tipo de sede.";
    if (!Number(formData.municipioId))
      e.municipioId = "Debe seleccionar un municipio.";

    // Área: numérica y no negativa
    if (formData.area !== "") {
      const n = Number(formData.area);
      if (isNaN(n)) e.area = "El área debe ser numérica.";
      else if (n < 0) e.area = "El área no puede ser negativa.";
    }

    // Geolocalización: solo números (ya limpiada en handleChange)
    if (formData.geolocalizacion && !/^[0-9]+$/.test(formData.geolocalizacion)) {
      e.geolocalizacion = "La geolocalización debe contener solo números.";
    }

    // Coordenadas: formato numérico y rangos de lat/long
    if (formData.coordenadas) {
      const coords = parseCoordinates(formData.coordenadas);
      if (!coords) {
        e.coordenadas =
          "Debes ingresar coordenadas numéricas en formato 'latitud, longitud'.";
      } else {
        const { lat, lon } = coords;
        if (lat < -90 || lat > 90) {
          e.coordenadas = "La latitud debe estar entre -90° y 90°.";
        } else if (lon < -180 || lon > 180) {
          e.coordenadas = "La longitud debe estar entre -180° y 180°.";
        }
      }
    }

    // COMUNA → OBLIGATORIA + SOLO 1–10
    if (!formData.comuna) {
      e.comuna = "Debe seleccionar la comuna.";
    } else if (formData.comuna < 1 || formData.comuna > 10) {
      e.comuna = "La comuna debe estar entre 1 y 10.";
    }

    // Descripción: no solo números
    if (formData.descripcion && /^\d+$/.test(formData.descripcion.trim())) {
      e.descripcion = "La descripción no puede ser solo números.";
    }

    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleSubmit = async () => {
    if (!validate()) return;

    const payload = {
      grupoId: Number(formData.grupoId),
      tipoSedeId: Number(formData.tipoSedeId),
      nombre: formData.nombre.trim(),
      municipioId: Number(formData.municipioId),
      geolocalizacion: formData.geolocalizacion?.trim() || null,
      coordenadas: formData.coordenadas?.trim() || null,
      area: formData.area === "" ? null : Number(formData.area),
      comuna: Number(formData.comuna),
      descripcion: formData.descripcion?.trim() || null,
      estadoId: Number(formData.estadoId),
    };

    try {
      if (formMode === "edit" && formData.id) {
        await axios.put(
          `/v1/sede/${formData.id}`,
          { id: Number(formData.id), ...payload },
          authHeaders
        );
        setMessage({
          open: true,
          severity: "success",
          text: "Sede actualizada correctamente.",
        });
      } else {
        await axios.post("/v1/sede", payload, authHeaders);
        setMessage({
          open: true,
          severity: "success",
          text: "Sede creada correctamente.",
        });
      }
      setOpen(false);
      reloadData();
    } catch (err) {
      const api = err.response?.data || {};
      const txt =
        api.message ||
        api.error ||
        (err.response?.status === 409
          ? "Datos duplicados o restricción en BD."
          : "Error al guardar sede.");
      setMessage({ open: true, severity: "error", text: txt });
    }
  };

  return (
    <Dialog open={open} onClose={() => setOpen(false)} maxWidth="sm" fullWidth>
      <DialogTitle>
        {formMode === "edit" ? "Editar Sede" : "Nueva Sede"}
      </DialogTitle>

      <DialogContent>
        {/* Nombre */}
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

        {/* Grupo */}
        <FormControl fullWidth margin="normal" error={!!errors.grupoId}>
          <InputLabel>Grupo</InputLabel>
          <Select
            name="grupoId"
            value={formData.grupoId}
            onChange={handleChange}
            label="Grupo"
          >
            {grupos.map((g) => (
              <MenuItem key={g.id} value={g.id}>
                {g.nombre}
              </MenuItem>
            ))}
          </Select>
          <FormHelperText>{errors.grupoId}</FormHelperText>
        </FormControl>

        {/* Tipo de sede */}
        <FormControl fullWidth margin="normal" error={!!errors.tipoSedeId}>
          <InputLabel>Tipo de Sede</InputLabel>
          <Select
            name="tipoSedeId"
            value={formData.tipoSedeId}
            onChange={handleChange}
            label="Tipo de Sede"
          >
            {tiposSede.map((t) => (
              <MenuItem key={t.id} value={t.id}>
                {t.nombre}
              </MenuItem>
            ))}
          </Select>
          <FormHelperText>{errors.tipoSedeId}</FormHelperText>
        </FormControl>

        {/* Municipio */}
        <FormControl fullWidth margin="normal" error={!!errors.municipioId}>
          <InputLabel>Municipio</InputLabel>
          <Select
            name="municipioId"
            value={formData.municipioId}
            onChange={handleChange}
            label="Municipio"
          >
            {municipiosOpts.map((m) => (
              <MenuItem key={m.id} value={m.id}>
                {m.nombre}
              </MenuItem>
            ))}
          </Select>
          <FormHelperText>{errors.municipioId}</FormHelperText>
        </FormControl>

        {/* Geolocalización */}
        <TextField
          fullWidth
          margin="normal"
          label="Geolocalización"
          name="geolocalizacion"
          value={formData.geolocalizacion}
          onChange={handleChange}
          error={!!errors.geolocalizacion}
          helperText={errors.geolocalizacion}
        />

        {/* Coordenadas */}
        <TextField
          fullWidth
          margin="normal"
          label="Coordenadas (lat, lon)"
          name="coordenadas"
          value={formData.coordenadas}
          onChange={handleChange}
          error={!!errors.coordenadas}
          helperText={
            errors.coordenadas ||
            "Ejemplo: 2.927, -75.281  (latitud, longitud)"
          }
        />

        {/* Área */}
        <TextField
          fullWidth
          margin="normal"
          label="Área"
          name="area"
          value={formData.area}
          onChange={handleChange}
          error={!!errors.area}
          helperText={errors.area}
        />

        {/* Comuna */}
        <FormControl fullWidth margin="normal" error={!!errors.comuna}>
          <InputLabel>Comuna</InputLabel>
          <Select
            name="comuna"
            value={formData.comuna}
            onChange={handleChange}
            label="Comuna"
          >
            {[1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map((c) => (
              <MenuItem key={c} value={c}>
                {c}
              </MenuItem>
            ))}
          </Select>
          <FormHelperText>{errors.comuna}</FormHelperText>
        </FormControl>

        {/* Descripción */}
        <TextField
          fullWidth
          margin="normal"
          label="Descripción"
          name="descripcion"
          value={formData.descripcion}
          onChange={handleChange}
          error={!!errors.descripcion}
          helperText={errors.descripcion}
          multiline
          minRows={2}
        />

        {/* Estado */}
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
