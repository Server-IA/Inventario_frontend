// FormProduccion.jsx
import React, { useEffect, useState } from "react";
import {
  Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, Button, FormControl, InputLabel, Select, MenuItem
} from "@mui/material";
import axios from "../axiosConfig";

export default function FormProduccion({
  open = false,
  setOpen = () => {},
  formMode = "create",      // 'create' | 'edit'
  selectedRow = null,
  reloadData = () => {},
  setMessage = () => {},
}) {
  const token = localStorage.getItem("token");
  const headers = { headers: { Authorization: `Bearer ${token}` } };

  const initialForm = {
    id: undefined,
    nombre: "",
    descripcion: "",
    fechaInicio: "",        // 'YYYY-MM-DDTHH:mm'
    fechaFinal: "",
    tipoProduccionId: "",
    espacioId: "",
    subSeccionId: "",
    estadoId: 1,            // 1=Activo, 2=Inactivo
  };

  const [formData, setFormData] = useState(initialForm);
  const [tiposProduccion, setTiposProduccion] = useState([]);
  const [espacios, setEspacios] = useState([]);
  const [subsecciones, setSubsecciones] = useState([]);
  const [errors, setErrors] = useState({});

  // Normaliza respuestas (paginadas o planas)
  const takeList = (data) =>
    Array.isArray(data) ? data :
    Array.isArray(data?.content) ? data.content :
    Array.isArray(data?.data) ? data.data : [];

  // Si el id elegido ya no existe en la lista, lo limpia
  const ensureInListOrEmpty = (id, list) =>
    list?.some?.(x => String(x.id) === String(id)) ? id : "";

  // ---------- CARGA CATÁLOGOS (SIN CASCADA) ----------
  useEffect(() => {
    if (!open) return;

    (async () => {
      try {
        const [tpRes, espRes, subRes] = await Promise.all([
          axios.get("/v1/items/tipo_produccion/0", headers),
          axios.get("/v1/items/espacio/0", headers),
          axios.get("/v1/items/sub_seccion/0", headers),
        ]);

        const tp = takeList(tpRes.data);
        const esp = takeList(espRes.data);
        const sub = takeList(subRes.data);

        setTiposProduccion(tp);
        setEspacios(esp);
        setSubsecciones(sub);

        // Asegura que los ids actuales sigan siendo válidos
        setFormData((p) => ({
          ...p,
          tipoProduccionId: ensureInListOrEmpty(p.tipoProduccionId, tp),
          espacioId: ensureInListOrEmpty(p.espacioId, esp),
          subSeccionId: ensureInListOrEmpty(p.subSeccionId, sub),
        }));
      } catch (e) {
        console.error("[FormProduccion] Error cargando catálogos:", e);
        setTiposProduccion([]); setEspacios([]); setSubsecciones([]);
      }
    })();

    // Prefill en modo edición
    if (formMode === "edit" && selectedRow) {
      const toLocal = (iso) => (iso ? String(iso).replace("Z", "").slice(0, 16) : "");
      setFormData({
        id: selectedRow.id,
        nombre: selectedRow.nombre ?? "",
        descripcion: selectedRow.descripcion ?? "",
        fechaInicio: toLocal(selectedRow.fechaInicio),
        fechaFinal: toLocal(selectedRow.fechaFinal),
        tipoProduccionId: selectedRow.tipoProduccionId ?? selectedRow?.tipoProduccion?.id ?? "",
        espacioId: selectedRow.espacioId ?? selectedRow?.espacio?.id ?? "",
        subSeccionId: selectedRow.subSeccionId ?? selectedRow?.subSeccion?.id ?? "",
        estadoId: selectedRow.estadoId ?? selectedRow?.estado?.id ?? 1,
      });
      setErrors({});
    } else {
      setFormData(initialForm);
      setErrors({});
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [open, formMode, selectedRow]);

  // ---------- HANDLERS ----------
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    setErrors((prev) => ({ ...prev, [name]: "" }));
  };

  // ---------- VALIDACIONES ----------
  const onlyNumbersRegex = /^\s*\d+\s*$/;
  const sqliWordsRegex = /\b(SELECT|INSERT|UPDATE|DELETE|DROP|ALTER|TRUNCATE|UNION|EXEC|EXECUTE|MERGE)\b|(--|\/\*|\*\/|;)/i;
  const invalidCharsRegex = /[<>`$\\]/;

  const validate = () => {
    const e = {};
    if (!formData.nombre.trim()) e.nombre = "Obligatorio";
    if (!formData.tipoProduccionId) e.tipoProduccionId = "Obligatorio";
    if (!formData.fechaInicio) e.fechaInicio = "Obligatorio";
    if (!formData.fechaFinal) e.fechaFinal = "Obligatorio";
    // Requeridos (SIN cascada)
    if (!formData.espacioId) e.espacioId = "Obligatorio";
    if (!formData.subSeccionId) e.subSeccionId = "Obligatorio";

    const nombre = (formData.nombre ?? "").trim();
    const descripcion = (formData.descripcion ?? "").trim();

    if (!e.nombre) {
      if (onlyNumbersRegex.test(nombre)) {
        e.nombre = "El nombre no puede ser solo números.";
      } else if (sqliWordsRegex.test(nombre) || invalidCharsRegex.test(nombre)) {
        e.nombre = "El nombre contiene patrones no permitidos.";
      }
    }

    if (!e.descripcion && descripcion) {
      if (onlyNumbersRegex.test(descripcion)) {
        e.descripcion = "La descripción no puede ser solo números.";
      } else if (sqliWordsRegex.test(descripcion) || invalidCharsRegex.test(descripcion)) {
        e.descripcion = "La descripción contiene patrones no permitidos.";
      }
    }

    if (formData.fechaInicio) {
      const inicio = new Date(formData.fechaInicio);
      const ahora  = new Date();
      if (inicio > ahora) e.fechaInicio = "La fecha de inicio no puede ser mayor a la fecha actual";
    }

    if (formData.fechaInicio && formData.fechaFinal) {
      const a = new Date(formData.fechaInicio);
      const b = new Date(formData.fechaFinal);
      if (a > b) e.fechaFinal = "La fecha final debe ser >= a la fecha inicio";
    }

    setErrors(e);
    return Object.keys(e).length === 0;
  };

  // ---------- SAVE ----------
  const asIntOrNull = (v) => {
    if (v === "" || v === null || v === undefined) return null;
    const n = Number(v);
    return Number.isFinite(n) ? n : null;
  };

  const addSeconds = (val) => (val ? (val.length === 16 ? `${val}:00` : val) : null);

  const handleSubmit = async () => {
    if (!validate()) return;

    const payload = {
      id: asIntOrNull(formData.id),
      nombre: formData.nombre?.trim() ?? "",
      descripcion: formData.descripcion?.trim() ?? "",
      fechaInicio: addSeconds(formData.fechaInicio),
      fechaFinal: addSeconds(formData.fechaFinal),
      tipoProduccionId: asIntOrNull(formData.tipoProduccionId),
      // SIN cascada: ambas listas son independientes
      espacioId: asIntOrNull(formData.espacioId),
      subSeccionId: asIntOrNull(formData.subSeccionId),
      estadoId: asIntOrNull(formData.estadoId),
    };

    const method = formMode === "edit" ? axios.put : axios.post;
    const url = formMode === "edit" ? `/v1/produccion/${formData.id}` : "/v1/produccion";

    try {
      await method(url, payload, headers);
      reloadData?.();
      setMessage?.({
        open: true,
        severity: "success",
        text: `Producción ${formMode === "edit" ? "actualizada" : "creada"} correctamente`,
      });
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
        "Solicitud inválida (400). Revisa los datos.";

      setMessage?.({
        open: true,
        severity: "error",
        text: `Error ${status ?? ""}: ${serverMsg}`,
      });
    }
  };

  return (
    <Dialog open={open} onClose={() => setOpen(false)} fullWidth maxWidth="sm">
      <DialogTitle>{formMode === "edit" ? "Editar Producción" : "Nueva Producción"}</DialogTitle>

      <DialogContent sx={{ display: "flex", flexDirection: "column", gap: 2, mt: 1 }}>
        <TextField
          label="Fecha inicio"
          name="fechaInicio"
          type="datetime-local"
          value={formData.fechaInicio}
          onChange={handleChange}
          error={!!errors.fechaInicio}
          helperText={errors.fechaInicio}
          fullWidth
          InputLabelProps={{ shrink: true }}
        />
        <TextField
          label="Fecha final"
          name="fechaFinal"
          type="datetime-local"
          value={formData.fechaFinal}
          onChange={handleChange}
          error={!!errors.fechaFinal}
          helperText={errors.fechaFinal}
          fullWidth
          InputLabelProps={{ shrink: true }}
        />

        <TextField
          label="Nombre"
          name="nombre"
          value={formData.nombre}
          onChange={handleChange}
          error={!!errors.nombre}
          helperText={errors.nombre}
          fullWidth
        />

        <FormControl fullWidth error={!!errors.tipoProduccionId}>
          <InputLabel>Tipo de Producción</InputLabel>
          <Select
            name="tipoProduccionId"
            value={formData.tipoProduccionId}
            label="Tipo de Producción"
            onChange={handleChange}
          >
            {tiposProduccion.map((tp) => (
              <MenuItem key={tp.id} value={tp.id}>
                {tp.nombre || tp.name}
              </MenuItem>
            ))}
          </Select>
        </FormControl>

        <TextField
          label="Descripción"
          name="descripcion"
          value={formData.descripcion}
          onChange={handleChange}
          fullWidth
          multiline
          minRows={2}
        />

        <FormControl fullWidth error={!!errors.espacioId}>
          <InputLabel>Espacio</InputLabel>
          <Select
            name="espacioId"
            value={formData.espacioId}
            label="Espacio"
            onChange={handleChange}
          >
            {espacios.map((e) => (
              <MenuItem key={e.id} value={e.id}>
                {e.nombre || e.name}
              </MenuItem>
            ))}
          </Select>
        </FormControl>

        <FormControl fullWidth error={!!errors.subSeccionId}>
          <InputLabel>Subsección</InputLabel>
          <Select
            name="subSeccionId"
            value={formData.subSeccionId}
            label="Subsección"
            onChange={handleChange}
          >
            {subsecciones.map((ss) => (
              <MenuItem key={ss.id} value={ss.id}>
                {ss.nombre || ss.name}
              </MenuItem>
            ))}
          </Select>
        </FormControl>

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
        </FormControl>
      </DialogContent>

      <DialogActions>
        <Button onClick={() => setOpen(false)}>Cancelar</Button>
        <Button onClick={handleSubmit} variant="contained">Guardar</Button>
      </DialogActions>
    </Dialog>
  );
}
