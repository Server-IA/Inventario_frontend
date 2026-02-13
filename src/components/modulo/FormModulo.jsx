import React, { useEffect, useState } from "react";
import {
  Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, Button, Grid, MenuItem,
  Checkbox, FormControlLabel
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

  const initialData = {
    id: null,
    nombre: "",
    url: "",
    descripcion: "",
    icon: "",
    estadoId: 1,
    subSistemaId: "",
    tipoModuloId: "",
    tipoAplicacionId: "",
    roles: "",
    nombreId: "",
    requerido: false,
  };

  const [formData, setFormData] = useState(initialData);
  const [subSistemas, setSubSistemas] = useState([]);
  const [tipoModulos, setTipoModulos] = useState([]);
  const [tipoAplicaciones, setTipoAplicaciones] = useState([]);

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

  useEffect(() => {
    if (!open) return;

    if (formMode === "edit" && selectedRow) {
      setFormData({
        ...selectedRow,
        estadoId: selectedRow.estado === "Activo" ? 1 : 2,
        roles: Array.isArray(selectedRow.roles)
          ? selectedRow.roles.join(", ")
          : "",
      });
    } else {
      setFormData(initialData);
    }
  }, [open, formMode, selectedRow]);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
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

    if (!Number(formData.subSistemaId) || Number(formData.subSistemaId) <= 0)
      return error("SubSistema inválido.");

    if (!Number(formData.tipoModuloId) || Number(formData.tipoModuloId) <= 0)
      return error("Tipo Módulo inválido.");

    if (!Number(formData.tipoAplicacionId) || Number(formData.tipoAplicacionId) <= 0)
      return error("Tipo Aplicación inválido.");

    const rolesArray = formData.roles
      .split(",")
      .map(r => r.trim())
      .filter(r => r.length > 0);

    if (!rolesArray.length)
      return error("Debe ingresar al menos un rol.");

    const payload = {
      nombre: formData.nombre.trim(),
      url: formData.url.trim(),
      descripcion: formData.descripcion.trim(),
      icon: formData.icon.trim(),
      estadoId: formMode === "create" ? 1 : Number(formData.estadoId),
      subSistemaId: Number(formData.subSistemaId),
      tipoModuloId: Number(formData.tipoModuloId),
      tipoAplicacionId: Number(formData.tipoAplicacionId),
      roles: rolesArray,
      nombreId: formData.nombreId.trim(),
      requerido: Boolean(formData.requerido),
    };

    try {
      if (formMode === "edit") {
        await axios.put(
          `/v1/modulos/${formData.id}`,
          payload,
          authHeaders
        );
      } else {
        await axios.post("/v1/modulos", payload, authHeaders);
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

      switch (res.status) {
        case 400:
          if (res.data?.errors) {
            error(Object.values(res.data.errors).join(" - "));
          } else {
            error(res.data?.detail || "Error de validación.");
          }
          break;

        case 401:
          error("Token expirado. Inicie sesión nuevamente.");
          break;

        case 409:
          error(res.data?.detail || "El módulo ya existe.");
          break;

        case 422:
          error(res.data?.detail || "Referencia inválida.");
          break;

        default:
          error("Error inesperado.");
      }
    }
  };

  return (
    <Dialog open={open} onClose={() => setOpen(false)} maxWidth="md" fullWidth>

      <DialogTitle>
        {formMode === "edit" ? "Editar Módulo" : "Nuevo Módulo"}
      </DialogTitle>

      <DialogContent>
        <Grid container spacing={2} sx={{ mt: 1 }}>

          <Grid item xs={12} md={6}>
            <TextField fullWidth required label="Nombre"
              name="nombre" value={formData.nombre}
              onChange={handleChange} />
          </Grid>

          <Grid item xs={12} md={6}>
            <TextField fullWidth required label="URL"
              name="url" value={formData.url}
              onChange={handleChange} />
          </Grid>

          <Grid item xs={12}>
            <TextField fullWidth multiline rows={2}
              label="Descripción"
              name="descripcion"
              value={formData.descripcion}
              onChange={handleChange} />
          </Grid>

          <Grid item xs={12} md={6}>
            <TextField fullWidth label="Icono"
              name="icon" value={formData.icon}
              onChange={handleChange} />
          </Grid>

          {formMode === "edit" && (
            <Grid item xs={12} md={6}>
              <TextField select fullWidth label="Estado"
                name="estadoId" value={formData.estadoId}
                onChange={handleChange}>
                <MenuItem value={1}>Activo</MenuItem>
                <MenuItem value={2}>Inactivo</MenuItem>
              </TextField>
            </Grid>
          )}

          <Grid item xs={12} md={6}>
            <TextField select fullWidth required label="SubSistema"
              name="subSistemaId" value={formData.subSistemaId}
              onChange={handleChange}>
              {subSistemas.map(s =>
                <MenuItem key={s.id} value={s.id}>{s.nombre}</MenuItem>
              )}
            </TextField>
          </Grid>

          <Grid item xs={12} md={6}>
            <TextField select fullWidth required label="Tipo Módulo"
              name="tipoModuloId" value={formData.tipoModuloId}
              onChange={handleChange}>
              {tipoModulos.map(t =>
                <MenuItem key={t.id} value={t.id}>{t.nombre}</MenuItem>
              )}
            </TextField>
          </Grid>

          <Grid item xs={12} md={6}>
            <TextField select fullWidth required label="Tipo Aplicación"
              name="tipoAplicacionId" value={formData.tipoAplicacionId}
              onChange={handleChange}>
              {tipoAplicaciones.map(a =>
                <MenuItem key={a.id} value={a.id}>{a.nombre}</MenuItem>
              )}
            </TextField>
          </Grid>

          <Grid item xs={12}>
            <TextField fullWidth
              label="Roles (separados por coma)"
              name="roles" value={formData.roles}
              onChange={handleChange} />
          </Grid>

          <Grid item xs={12} md={6}>
            <TextField fullWidth label="Nombre ID"
              name="nombreId" value={formData.nombreId}
              onChange={handleChange} />
          </Grid>

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
