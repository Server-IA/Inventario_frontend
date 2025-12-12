// src/components/UsuarioRoles/FormUsuarioRoles.jsx
import React, { useEffect, useState, useMemo } from "react";
import PropTypes from "prop-types";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Grid,
  TextField,
  Button,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
} from "@mui/material";

const toDateTimeLocal = (value) => {
  if (!value) return "";
  const d = new Date(value);
  if (Number.isNaN(d.getTime())) return "";
  return d.toISOString().substring(0, 16); // yyyy-MM-ddTHH:mm
};

export default function FormUsuarioRoles({
  open,
  onClose,
  mode,
  initialData,
  onSubmit,
  usuarios,
  empresas,
  roles,
  estados,
}) {
  const [form, setForm] = useState(initialData || {});

  useEffect(() => {
    setForm(initialData || {});
  }, [initialData, open]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  // 👉 opciones únicas de estado: sólo primer Activo y primer Inactivo
  const estadoOptions = useMemo(() => {
    const result = [];
    const seen = new Set();

    for (const e of estados) {
      const nombre = e.name ?? e.nombre;
      if (!nombre) continue;

      // normalizamos un poco por si vienen en mayúsculas
      const upper = nombre.toUpperCase();

      if (
        (upper.includes("ACTIVO") || upper.includes("INACTIVO")) &&
        !seen.has(upper)
      ) {
        result.push(e);
        seen.add(upper);
      }
    }
    return result;
  }, [estados]);

  const handleSubmit = () => {
    if (!form.usuarioId) {
      alert("Debes seleccionar un usuario.");
      return;
    }
    if (!form.empresaId) {
      alert("Debes seleccionar una empresa.");
      return;
    }
    if (!form.rolId) {
      alert("Debes seleccionar un rol.");
      return;
    }
    if (!form.estadoId) {
      alert("Debes seleccionar un estado (Activo/Inactivo).");
      return;
    }

    const payload = {
      ...form,
      usuarioId: form.usuarioId ? Number(form.usuarioId) : null,
      empresaId: form.empresaId ? Number(form.empresaId) : null,
      rolId: form.rolId ? Number(form.rolId) : null,
      estadoId: form.estadoId ? Number(form.estadoId) : null,
      iniciaContratoEn: form.iniciaContratoEn
        ? new Date(form.iniciaContratoEn).toISOString()
        : null,
      finalizaContratoEn: form.finalizaContratoEn
        ? new Date(form.finalizaContratoEn).toISOString()
        : null,
    };

    onSubmit(payload);
  };

  const isEdit = mode === "edit";

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>
        {isEdit ? "Editar Usuario – Rol" : "Crear Usuario – Rol"}
      </DialogTitle>

      <DialogContent dividers>
        <Grid container spacing={2}>
          {/* Usuario - en edición solo lectura */}
          <Grid item xs={12} sm={6}>
            <FormControl fullWidth disabled={isEdit}>
              <InputLabel>Usuario</InputLabel>
              <Select
                name="usuarioId"
                label="Usuario"
                value={form.usuarioId ?? ""}
                onChange={handleChange}
              >
                <MenuItem value="">
                  <em>Seleccione...</em>
                </MenuItem>
                {usuarios.map((u) => (
                  <MenuItem key={u.id} value={u.id}>
                    {u.name}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>

          {/* Empresa - en edición solo lectura */}
          <Grid item xs={12} sm={6}>
            <FormControl fullWidth disabled={isEdit}>
              <InputLabel>Empresa</InputLabel>
              <Select
                name="empresaId"
                label="Empresa"
                value={form.empresaId ?? ""}
                onChange={handleChange}
              >
                <MenuItem value="">
                  <em>Seleccione...</em>
                </MenuItem>
                {empresas.map((e) => (
                  <MenuItem key={e.id} value={e.id}>
                    {e.name}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>

          {/* Rol */}
          <Grid item xs={12} sm={6}>
            <FormControl fullWidth>
              <InputLabel>Rol</InputLabel>
              <Select
                name="rolId"
                label="Rol"
                value={form.rolId ?? ""}
                onChange={handleChange}
              >
                <MenuItem value="">
                  <em>Seleccione...</em>
                </MenuItem>
                {roles.map((r) => (
                  <MenuItem key={r.id} value={r.id}>
                    {r.name}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>

          {/* Estado - editable en creación y edición */}
          <Grid item xs={12} sm={6}>
            <FormControl fullWidth>
              <InputLabel>Estado</InputLabel>
              <Select
                name="estadoId"
                label="Estado"
                value={form.estadoId ?? ""}
                onChange={handleChange}
              >
                <MenuItem value="">
                  <em>Seleccione...</em>
                </MenuItem>
                {estadoOptions.map((es) => (
                  <MenuItem key={es.id} value={es.id}>
                    {es.name ?? es.nombre}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>

          {/* Fecha inicio contrato */}
          <Grid item xs={12} sm={6}>
            <TextField
              name="iniciaContratoEn"
              label="Inicia contrato"
              type="datetime-local"
              value={toDateTimeLocal(form.iniciaContratoEn)}
              onChange={handleChange}
              InputLabelProps={{ shrink: true }}
              fullWidth
            />
          </Grid>

          {/* Fecha final contrato */}
          <Grid item xs={12} sm={6}>
            <TextField
              name="finalizaContratoEn"
              label="Finaliza contrato"
              type="datetime-local"
              value={toDateTimeLocal(form.finalizaContratoEn)}
              onChange={handleChange}
              InputLabelProps={{ shrink: true }}
              fullWidth
            />
          </Grid>
        </Grid>
      </DialogContent>

      <DialogActions>
        <Button onClick={onClose}>Cancelar</Button>
        <Button variant="contained" onClick={handleSubmit}>
          {isEdit ? "Actualizar" : "Guardar"}
        </Button>
      </DialogActions>
    </Dialog>
  );
}

FormUsuarioRoles.propTypes = {
  open: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
  mode: PropTypes.oneOf(["create", "edit"]).isRequired,
  initialData: PropTypes.object.isRequired,
  onSubmit: PropTypes.func.isRequired,
  usuarios: PropTypes.array.isRequired,
  empresas: PropTypes.array.isRequired,
  roles: PropTypes.array.isRequired,
  estados: PropTypes.array.isRequired,
};
