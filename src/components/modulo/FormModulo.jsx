import React, { useEffect, useState } from "react";
import {
  Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, Button, Grid, MenuItem,
  Checkbox, FormControlLabel,
  Box, Typography
} from "@mui/material";
import axios from "../axiosConfig";

export default function FormModulo({
  open,
  setOpen,
  formMode,
  selectedRow,
  reloadData,
  setMessage,
  authHeaders,
}) {

  // 🔹 Generador automático del acrónimo
  const generarNombreId = (nombre) => {
    return nombre
      .normalize("NFD")
      .replace(/[\u0300-\u036f]/g, "")
      .toLowerCase()
      .trim()
      .replace(/\s+/g, "_")
      .replace(/[^a-z0-9_]/g, "");
  };

  const initialData = {
    id: null,
    nombre: "",
    nombreId: "",
    url: "",
    descripcion: "",
    estadoId: 1,
    subSistemaId: "",
    tipoModuloId: "",
    tipoAplicacionId: "",
    requerido: false,
  };

  const [formData, setFormData] = useState(initialData);
  const [subSistemas, setSubSistemas] = useState([]);
  const [tipoModulos, setTipoModulos] = useState([]);
  const [tipoAplicaciones, setTipoAplicaciones] = useState([]);

  // 🔹 Cargar combos
  useEffect(() => {
    if (!open) return;

    Promise.all([
      axios.get("/v1/sub-sistemas?campos=id,nombre", authHeaders),
      axios.get("/v1/tipo-modulos?campos=id,nombre", authHeaders),
      axios.get("/v1/tipo-aplicaciones?campos=id,nombre", authHeaders),
    ]).then(([subs, tipos, apps]) => {
      const unwrap = (d) => Array.isArray(d) ? d : d?.content ?? [];
      setSubSistemas(unwrap(subs.data));
      setTipoModulos(unwrap(tipos.data));
      setTipoAplicaciones(unwrap(apps.data));
    });
  }, [open]);

  // 🔹 Cargar datos en edición
  useEffect(() => {
    if (!open) return;

    if (formMode === "edit" && selectedRow) {

      const subSistema = subSistemas.find(
        s => s.nombre === selectedRow.subSistema
      );

      const tipoModulo = tipoModulos.find(
        t => t.nombre === selectedRow.tipoModulo
      );

      const tipoAplicacion = tipoAplicaciones.find(
        a => a.nombre === selectedRow.tipoAplicacion
      );

      setFormData({
        id: selectedRow.id,
        nombre: selectedRow.nombre || "",
        nombreId: selectedRow.nombreId || "",
        url: selectedRow.url || "",
        descripcion: selectedRow.descripcion || "",
        estadoId: selectedRow.estado === "Activo" ? 1 : 2,
        subSistemaId: subSistema?.id || "",
        tipoModuloId: tipoModulo?.id || "",
        tipoAplicacionId: tipoAplicacion?.id || "",
        requerido: Boolean(selectedRow.requerido),
      });

    } else {
      setFormData(initialData);
    }

  }, [
    open,
    formMode,
    selectedRow,
    subSistemas,
    tipoModulos,
    tipoAplicaciones
  ]);

  // 🔹 Manejo de cambios con generación automática
  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;

    setFormData(prev => {
      const updated = {
        ...prev,
        [name]: type === "checkbox" ? checked : value,
      };

      if (name === "nombre") {
        updated.nombreId = generarNombreId(value);
      }

      return updated;
    });
  };

  const error = (text) => {
    setMessage({ open: true, severity: "error", text });
  };

  const handleSubmit = async () => {

    if (!formData.nombre.trim())
      return error("El nombre es obligatorio.");

    if (formData.nombre.length > 100)
      return error("El nombre no puede superar 100 caracteres.");

    if (!formData.url.trim())
      return error("La URL es obligatoria.");

    if (formData.url.length > 100)
      return error("La URL no puede superar 100 caracteres.");

    if (!Number(formData.subSistemaId))
      return error("SubSistema inválido.");

    if (!Number(formData.tipoModuloId))
      return error("Tipo Módulo inválido.");

    if (!Number(formData.tipoAplicacionId))
      return error("Tipo Aplicación inválido.");

    if (!formData.nombreId)
      return error("No se pudo generar el identificador técnico.");

    const payload = {
      nombre: formData.nombre.trim(),
      nombreId: formData.nombreId,
      url: formData.url.trim(),
      descripcion: formData.descripcion.trim(),
      estadoId: formMode === "create" ? 1 : Number(formData.estadoId),
      subSistemaId: Number(formData.subSistemaId),
      tipoModuloId: Number(formData.tipoModuloId),
      tipoAplicacionId: Number(formData.tipoAplicacionId),
      requerido: Boolean(formData.requerido),
    };

    try {
      if (formMode === "edit") {
        await axios.put(
          `/v2/modulos/${formData.id}`,
          payload,
          authHeaders
        );
      } else {
        await axios.post("/v2/modulos", payload, authHeaders);
      }

      setMessage({
        open: true,
        severity: "success",
        text: "Módulo guardado correctamente.",
      });

      setOpen(false);
      reloadData();

    } catch (err) {
      const res = err.response;

      if (!res)
        return error("Error de conexión con el servidor.");

      error(res.data?.detail || "Error inesperado.");
    }
  };

  return (
    <Dialog open={open} onClose={() => setOpen(false)} maxWidth="md" fullWidth>

      <DialogTitle>
        {formMode === "edit" ? "Editar Módulo" : "Nuevo Módulo"}
      </DialogTitle>

      <DialogContent>
        <Grid container spacing={2} sx={{ mt: 1 }}>

          {/* Nombre */}
          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              required
              label="Nombre"
              name="nombre"
              value={formData.nombre}
              onChange={handleChange}
            />
          </Grid>

          {/* Acrónimo visual mejorado */}
          <Grid item xs={12} md={6}>
            <Box
              sx={{
                p: 2,
                borderRadius: 2,
                border: "1px solid",
                borderColor: "divider",
                backgroundColor: "action.hover",
                height: "100%",
              }}
            >
              <Typography variant="caption" color="text.secondary">
                Acrónimo generado automáticamente
              </Typography>

              <Typography
                variant="body2"
                sx={{
                  fontWeight: 600,
                  mt: 0.5,
                  px: 1.5,
                  py: 0.5,
                  display: "inline-block",
                  borderRadius: 1,
                  backgroundColor: "primary.main",
                  color: "primary.contrastText",
                  letterSpacing: 1,
                }}
              >
                {formData.nombreId || "—"}
              </Typography>
            </Box>
          </Grid>

          {/* URL */}
          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              required
              label="URL"
              name="url"
              value={formData.url}
              onChange={handleChange}
            />
          </Grid>

          {/* Descripción */}
          <Grid item xs={12}>
            <TextField
              fullWidth
              multiline
              rows={2}
              label="Descripción"
              name="descripcion"
              value={formData.descripcion}
              onChange={handleChange}
            />
          </Grid>

          {/* Estado */}
          {formMode === "edit" && (
            <Grid item xs={12} md={6}>
              <TextField
                select
                fullWidth
                label="Estado"
                name="estadoId"
                value={formData.estadoId}
                onChange={handleChange}
              >
                <MenuItem value={1}>Activo</MenuItem>
                <MenuItem value={2}>Inactivo</MenuItem>
              </TextField>
            </Grid>
          )}

          {/* SubSistema */}
          <Grid item xs={12} md={6}>
            <TextField
              select
              fullWidth
              required
              label="SubSistema"
              name="subSistemaId"
              value={formData.subSistemaId}
              onChange={handleChange}
            >
              {subSistemas.map(s => (
                <MenuItem key={s.id} value={s.id}>
                  {s.nombre}
                </MenuItem>
              ))}
            </TextField>
          </Grid>

          {/* Tipo Módulo */}
          <Grid item xs={12} md={6}>
            <TextField
              select
              fullWidth
              required
              label="Tipo Módulo"
              name="tipoModuloId"
              value={formData.tipoModuloId}
              onChange={handleChange}
            >
              {tipoModulos.map(t => (
                <MenuItem key={t.id} value={t.id}>
                  {t.nombre}
                </MenuItem>
              ))}
            </TextField>
          </Grid>

          {/* Tipo Aplicación */}
          <Grid item xs={12} md={6}>
            <TextField
              select
              fullWidth
              required
              label="Tipo Aplicación"
              name="tipoAplicacionId"
              value={formData.tipoAplicacionId}
              onChange={handleChange}
            >
              {tipoAplicaciones.map(a => (
                <MenuItem key={a.id} value={a.id}>
                  {a.nombre}
                </MenuItem>
              ))}
            </TextField>
          </Grid>

          {/* Requerido */}
          <Grid item xs={12}>
            <FormControlLabel
              control={
                <Checkbox
                  name="requerido"
                  checked={formData.requerido}
                  onChange={handleChange}
                />
              }
              label="Módulo requerido"
            />
          </Grid>

        </Grid>
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
