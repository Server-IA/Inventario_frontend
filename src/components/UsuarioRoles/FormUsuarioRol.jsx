// src/components/UsuarioRoles/FormUsuarioRol.jsx
import React, { useEffect, useState } from "react";
import PropTypes from "prop-types";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Grid,
  TextField,
  Button,
} from "@mui/material";

const toDateTimeLocal = (value) => {
  if (!value) return "";
  const d = new Date(value);
  const iso = d.toISOString();
  return iso.substring(0, 16); // "YYYY-MM-DDTHH:mm"
};

export default function FormUsuarioRol({
  open,
  onClose,
  mode,
  initialData,
  onSubmit,
}) {
  const [form, setForm] = useState(initialData);

  useEffect(() => {
    setForm(initialData);
  }, [initialData, open]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = () => {
    // convierto las fechas al formato ISO (Z) para el backend
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
        <Grid container spacing={2} mt={0.5}>
          {isEdit && (
            <Grid item xs={12}>
              <TextField
                label="ID"
                value={form.id ?? ""}
                fullWidth
                InputProps={{ readOnly: true }}
              />
            </Grid>
          )}

          <Grid item xs={12} sm={6}>
            <TextField
              name="usuarioId"
              label="Usuario ID"
              type="number"
              value={form.usuarioId}
              onChange={handleChange}
              fullWidth
            />
          </Grid>

          <Grid item xs={12} sm={6}>
            <TextField
              name="empresaId"
              label="Empresa ID"
              type="number"
              value={form.empresaId}
              onChange={handleChange}
              fullWidth
            />
          </Grid>

          <Grid item xs={12} sm={6}>
            <TextField
              name="rolId"
              label="Rol ID"
              type="number"
              value={form.rolId}
              onChange={handleChange}
              fullWidth
            />
          </Grid>

          <Grid item xs={12} sm={6}>
            <TextField
              name="estadoId"
              label="Estado ID"
              type="number"
              value={form.estadoId}
              onChange={handleChange}
              fullWidth
            />
          </Grid>

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

FormUsuarioRol.propTypes = {
  open: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
  mode: PropTypes.oneOf(["create", "edit"]).isRequired,
  initialData: PropTypes.object.isRequired,
  onSubmit: PropTypes.func.isRequired,
};
