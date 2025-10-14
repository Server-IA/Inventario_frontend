import React, { useEffect, useState } from "react";
import {
  Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, Button, FormControl, InputLabel, Select, MenuItem
} from "@mui/material";
import axios from "../axiosConfig";

export default function FormProduccion({
  open = false,
  setOpen = () => {},
  formMode = "create",            // 'create' | 'edit'
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
    fechaInicio: "",      // 'YYYY-MM-DDTHH:mm'
    fechaFinal: "",
    tipoProduccionId: "",
    sedeId: "",
    bloqueId: "",
    espacioId: "",
    subSeccionId: "",
    estadoId: 1,          // 1=Activo, 2=Inactivo
  };

  const [formData, setFormData] = useState(initialForm);

  const [tiposProduccion, setTiposProduccion] = useState([]);
  const [sedes, setSedes] = useState([]);
  const [bloques, setBloques] = useState([]);
  const [espacios, setEspacios] = useState([]);
  const [subsecciones, setSubsecciones] = useState([]);

  const [errors, setErrors] = useState({});

  // Normaliza respuesta (paginada o plana)
  const takeList = (data) =>
    Array.isArray(data) ? data :
    Array.isArray(data?.content) ? data.content :
    Array.isArray(data?.data) ? data.data : [];

  // Helpers para setear si una opción deja de pertenecer a la lista filtrada
  const ensureInListOrEmpty = (id, list) =>
    list?.some?.(x => String(x.id) === String(id)) ? id : "";

  // ------- CARGA CATÁLOGOS BÁSICOS + PREFILL -------
  useEffect(() => {
    if (!open) return;

    (async () => {
      try {
        const [tp, sd] = await Promise.all([
          axios.get("/v1/items/tipo_produccion/0", headers),
          axios.get("/v1/items/sede/0", headers),
        ]);
        setTiposProduccion(takeList(tp.data));
        setSedes(takeList(sd.data));
      } catch (e) {
        console.error("[FormProduccion] Error cargando catálogos base:", e);
        setTiposProduccion([]); setSedes([]);
      }
    })();

    // Prefill en modo edición
    if (formMode === "edit" && selectedRow) {
      const toLocal = (iso) => (iso ? String(iso).replace("Z", "").slice(0, 16) : "");
      setFormData((prev) => ({
        ...prev,
        id: selectedRow.id,
        nombre: selectedRow.nombre ?? "",
        descripcion: selectedRow.descripcion ?? "",
        fechaInicio: toLocal(selectedRow.fechaInicio),
        fechaFinal: toLocal(selectedRow.fechaFinal),
        tipoProduccionId: selectedRow.tipoProduccionId ?? selectedRow?.tipoProduccion?.id ?? "",
        // Si vienen anidados, tomamos ids en cascada:
        sedeId: selectedRow.sedeId ?? selectedRow?.sede?.id ?? "",
        bloqueId: selectedRow.bloqueId ?? selectedRow?.bloque?.id ?? "",
        espacioId: selectedRow.espacioId ?? selectedRow?.espacio?.id ?? "",
        subSeccionId: selectedRow.subSeccionId ?? selectedRow?.subSeccion?.id ?? "",
        estadoId: selectedRow.estadoId ?? selectedRow?.estado?.id ?? 1,
      }));
      setErrors({});
    } else {
      setFormData(initialForm);
      setErrors({});
    }
  }, [open, formMode, selectedRow]);

  // ------- CASCADA: SEDE -> BLOQUE -------
  useEffect(() => {
    if (!open) return;

    const load = async () => {
      if (!formData.sedeId) {
        // sin sede: lista completa de bloques como fallback
        try {
          const res = await axios.get("/v1/items/bloque/0", headers);
          const list = takeList(res.data);
          setBloques(list);
          // limpia dependientes
          setFormData((p) => ({
            ...p,
            bloqueId: "",
            espacioId: "",
            subSeccionId: "",
          }));
        } catch {
          setBloques([]);
        }
        return;
      }

      try {
        // bloques de la sede
        const res = await axios.get(`/v1/items/bloque/${formData.sedeId}`, headers);
        const list = takeList(res.data);
        setBloques(list);
        setFormData((p) => ({
          ...p,
          bloqueId: ensureInListOrEmpty(p.bloqueId, list),
          // al cambiar la sede, invalida descendientes si es necesario
          espacioId: "",
          subSeccionId: "",
        }));
      } catch (e) {
        console.error("[FormProduccion] Error cargando bloques por sede:", e);
        setBloques([]);
        setFormData((p) => ({ ...p, bloqueId: "", espacioId: "", subSeccionId: "" }));
      }
    };

    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [open, formData.sedeId]);

  // ------- CASCADA: BLOQUE -> ESPACIO -------
  useEffect(() => {
    if (!open) return;

    const load = async () => {
      if (!formData.bloqueId) {
        // sin bloque: lista completa de espacios como fallback
        try {
          const res = await axios.get("/v1/items/espacio/0", headers);
          const list = takeList(res.data);
          setEspacios(list);
          setFormData((p) => ({ ...p, espacioId: "", subSeccionId: "" }));
        } catch {
          setEspacios([]);
        }
        return;
      }

      try {
        // espacios del bloque
        const res = await axios.get(`/v1/items/espacio/${formData.bloqueId}`, headers);
        const list = takeList(res.data);
        setEspacios(list);
        setFormData((p) => ({
          ...p,
          espacioId: ensureInListOrEmpty(p.espacioId, list),
          subSeccionId: "",
        }));
      } catch (e) {
        console.error("[FormProduccion] Error cargando espacios por bloque:", e);
        setEspacios([]);
        setFormData((p) => ({ ...p, espacioId: "", subSeccionId: "" }));
      }
    };

    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [open, formData.bloqueId]);

  // ------- CASCADA: ESPACIO -> SUBSECCIÓN -------
  useEffect(() => {
    if (!open) return;

    const load = async () => {
      if (!formData.espacioId) {
        // sin espacio: lista completa de subsecciones como fallback
        try {
          const res = await axios.get("/v1/items/sub_seccion/0", headers);
          const list = takeList(res.data);
          setSubsecciones(list);
          setFormData((p) => ({ ...p, subSeccionId: "" }));
        } catch {
          setSubsecciones([]);
        }
        return;
      }

      try {
        // subsecciones del espacio
        const res = await axios.get(`/v1/items/sub_seccion/${formData.espacioId}`, headers);
        const list = takeList(res.data);
        setSubsecciones(list);
        setFormData((p) => ({
          ...p,
          subSeccionId: ensureInListOrEmpty(p.subSeccionId, list),
        }));
      } catch (e) {
        console.error("[FormProduccion] Error cargando subsecciones por espacio:", e);
        setSubsecciones([]);
        setFormData((p) => ({ ...p, subSeccionId: "" }));
      }
    };

    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [open, formData.espacioId]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    // limpia errores por campo al escribir
    setErrors((prev) => ({ ...prev, [name]: "" }));
  };


  const onlyNumbersRegex = /^\s*\d+\s*$/; // solo dígitos (con o sin espacios)
  const sqliWordsRegex = /\b(SELECT|INSERT|UPDATE|DELETE|DROP|ALTER|TRUNCATE|UNION|EXEC|EXECUTE|MERGE)\b|(--|\/\*|\*\/|;)/i;
  const invalidCharsRegex = /[<>`$\\]/; // caracteres típicos a bloquear
  
  const validate = () => {
     // Reglas simples anti-sólo-números y anti-inyección

    const e = {};
    if (!formData.nombre.trim()) e.nombre = "Obligatorio";
    if (!formData.tipoProduccionId) e.tipoProduccionId = "Obligatorio";
    if (!formData.fechaInicio) e.fechaInicio = "Obligatorio";
    if (!formData.fechaFinal) e.fechaFinal = "Obligatorio";

    // NUEVO: requeridos para la cascada
    if (!formData.sedeId) e.sedeId = "Obligatorio";
    if (!formData.bloqueId) e.bloqueId = "Obligatorio";
    if (!formData.espacioId) e.espacioId = "Obligatorio";
    if (!formData.subSeccionId) e.subSeccionId = "Obligatorio";


    
       // --- Reglas anti solo números y anti inyección ---
       const nombre = (formData.nombre ?? "").trim();
       const descripcion = (formData.descripcion ?? "").trim();
    
       if (!e.nombre) {
         if (onlyNumbersRegex.test(nombre)) {
           e.nombre = "El nombre no puede ser solo números.";
         } else if (sqliWordsRegex.test(nombre) || invalidCharsRegex.test(nombre)) {
           e.nombre = "El nombre contiene patrones no permitidos.";
         }
       }
    
       if (!e.descripcion && descripcion) { // descripción puede ser vacía, pero si hay valor se valida
         if (onlyNumbersRegex.test(descripcion)) {
           e.descripcion = "La descripción no puede ser solo números.";
         } else if (sqliWordsRegex.test(descripcion) || invalidCharsRegex.test(descripcion)) {
           e.descripcion = "La descripción contiene patrones no permitidos.";
         }
       }


    // fecha de inicio no puede ser futura
    if (formData.fechaInicio) {
      const inicio = new Date(formData.fechaInicio);
      const ahora  = new Date();
      if (inicio > ahora) e.fechaInicio = "La fecha de inicio no puede ser mayor a la fecha actual";
    }

    // coherencia de fechas
    if (formData.fechaInicio && formData.fechaFinal) {
      const a = new Date(formData.fechaInicio);
      const b = new Date(formData.fechaFinal);
      if (a > b) e.fechaFinal = "La fecha final debe ser >= a la fecha inicio";
    }

    setErrors(e);
    return Object.keys(e).length === 0;
  };

  // Helpers numéricos/fecha para el payload
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
      fechaInicio: addSeconds(formData.fechaInicio), // "YYYY-MM-DDTHH:mm:ss"
      fechaFinal: addSeconds(formData.fechaFinal),
      tipoProduccionId: asIntOrNull(formData.tipoProduccionId),

      // Cascada (inclúyelos si tu backend los recibe; si no, puedes omitirlos)
      sedeId: asIntOrNull(formData.sedeId),
      bloqueId: asIntOrNull(formData.bloqueId),

      espacioId: asIntOrNull(formData.espacioId),
      subSeccionId: asIntOrNull(formData.subSeccionId),
      estadoId: asIntOrNull(formData.estadoId),
    };

    console.groupCollapsed("[FormProduccion] Payload a enviar");
    console.table(payload);
    console.groupEnd();

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

      console.group("[FormProduccion] Error al guardar");
      console.log("status:", status);
      console.log("api.data:", api);
      console.log("payload:", payload);
      console.groupEnd();

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

        {/* --- NUEVOS SELECTS EN CASCADA --- */}
        <FormControl fullWidth error={!!errors.sedeId}>
          <InputLabel>Sede</InputLabel>
          <Select
            name="sedeId"
            value={formData.sedeId}
            label="Sede"
            onChange={handleChange}
          >
            {sedes.map((s) => (
              <MenuItem key={s.id} value={s.id}>
                {s.nombre || s.name}
              </MenuItem>
            ))}
          </Select>
        </FormControl>

        <FormControl fullWidth error={!!errors.bloqueId}>
          <InputLabel>Bloque</InputLabel>
          <Select
            name="bloqueId"
            value={formData.bloqueId}
            label="Bloque"
            onChange={handleChange}
            disabled={!formData.sedeId}
          >
            {bloques.map((b) => (
              <MenuItem key={b.id} value={b.id}>
                {b.nombre || b.name}
              </MenuItem>
            ))}
          </Select>
        </FormControl>

        <FormControl fullWidth error={!!errors.espacioId}>
          <InputLabel>Espacio</InputLabel>
          <Select
            name="espacioId"
            value={formData.espacioId}
            label="Espacio"
            onChange={handleChange}
            disabled={!formData.bloqueId}
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
            disabled={!formData.espacioId}
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
