import React, { useEffect, useMemo, useState } from "react";
import { Dialog, DialogTitle, DialogContent, DialogActions, Stack, TextField, MenuItem, Button, Grid, Paper, Switch, FormControlLabel } from "@mui/material";
export default function FormUsuario({
  open,
  onClose,
  mode = "create",
  initialData,
  onSubmit,
  roles = [],
  empresas = [],
  isAdmin = false,
}) {
  const [formData, setFormData] = useState(initialData);
  const [assignDraft, setAssignDraft] = useState({ rolNombre: "", empresaId: "", empresaNombre: "", iniciaContratoEn: "", finalizaContratoEn: "", preferido: false, estadoId: 1 });
  useEffect(() => {
    setFormData(initialData);
    setAssignDraft({ rolNombre: "", empresaId: "", empresaNombre: "", iniciaContratoEn: "", finalizaContratoEn: "", preferido: false, estadoId: 1 });
  }, [initialData, open]);
  const handleChange = (name, value) => setFormData((p) => ({ ...p, [name]: value }));
  const addAssign = () => {
    const empresa = empresas.find((e) => String(e.id) === String(assignDraft.empresaId));
    const draft = { ...assignDraft, empresaNombre: empresa?.nombre ?? assignDraft.empresaNombre };
    let asignaciones = Array.isArray(formData.asignaciones) ? [...formData.asignaciones] : [];
    if (draft.preferido) asignaciones = asignaciones.map((a) => ({ ...a, preferido: false }));
    asignaciones.push(draft);
    setFormData((p) => ({ ...p, asignaciones }));
    setAssignDraft({ rolNombre: "", empresaId: "", empresaNombre: "", iniciaContratoEn: "", finalizaContratoEn: "", preferido: false, estadoId: 1 });
  };
  const removeAssign = (idx) => {
    const asignaciones = Array.isArray(formData.asignaciones) ? [...formData.asignaciones] : [];
    asignaciones.splice(idx, 1);
    setFormData((p) => ({ ...p, asignaciones }));
  };
  const togglePreferido = (idx) => {
    const asignaciones = Array.isArray(formData.asignaciones) ? [...formData.asignaciones] : [];
    asignaciones.forEach((a, i) => (a.preferido = i === idx));
    setFormData((p) => ({ ...p, asignaciones }));
  };
  const handleSave = () => onSubmit?.(formData);
  const rolesOptions = useMemo(() => roles.map((r) => ({ value: r, label: r })), [roles]);
  const empresasOptions = useMemo(() => empresas.map((e) => ({ value: String(e.id), label: e.nombre })), [empresas]);
  return (
    <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
      <DialogTitle>{mode === "create" ? "Registrar usuario" : "Actualizar usuario"}</DialogTitle>
      <DialogContent dividers>
        <Grid container spacing={3}>
          <Grid item xs={12} md={6}>
            <Stack spacing={2}>
              <TextField label="Username (correo)" type="email" required value={formData.username} onChange={(e) => handleChange("username", e.target.value)} fullWidth />
              <Stack direction={{ xs: "column", sm: "row" }} spacing={2}>
                <TextField label="Nombre" required value={formData.nombre} onChange={(e) => handleChange("nombre", e.target.value)} fullWidth />
                <TextField label="Apellido" required value={formData.apellido} onChange={(e) => handleChange("apellido", e.target.value)} fullWidth />
              </Stack>
              <Stack direction={{ xs: "column", sm: "row" }} spacing={2}>
                <TextField label="Género" value={formData.genero} onChange={(e) => handleChange("genero", e.target.value)} fullWidth />
                <TextField label="Estrato" value={formData.estrato} onChange={(e) => handleChange("estrato", e.target.value)} fullWidth />
              </Stack>
              <Stack direction={{ xs: "column", sm: "row" }} spacing={2}>
                <TextField label="Tipo de documento" value={formData.tipoDocumentoIdentidadId} onChange={(e) => handleChange("tipoDocumentoIdentidadId", e.target.value)} fullWidth />
                <TextField label="Código de identificación" value={formData.codigoIdentificacion} onChange={(e) => handleChange("codigoIdentificacion", e.target.value)} fullWidth />
              </Stack>
              <Stack direction={{ xs: "column", sm: "row" }} spacing={2}>
                <TextField label="Fecha de nacimiento" type="date" value={formData.fechaNacimiento} onChange={(e) => handleChange("fechaNacimiento", e.target.value)} fullWidth InputLabelProps={{ shrink: true }} />
                <TextField label="Celular" value={formData.celular} onChange={(e) => handleChange("celular", e.target.value)} fullWidth />
              </Stack>
              <TextField label="Dirección" value={formData.direccion} onChange={(e) => handleChange("direccion", e.target.value)} fullWidth />
              <TextField select label="Estado" value={formData.estadoId} onChange={(e) => handleChange("estadoId", Number(e.target.value))} fullWidth>
                <MenuItem value={1}>Activo</MenuItem>
                <MenuItem value={2}>Inactivo</MenuItem>
              </TextField>
            </Stack>
          </Grid>
          <Grid item xs={12} md={6}>
            <Stack spacing={2}>
              <Paper sx={{ p: 2 }}>
                <Stack spacing={2}>
                  <TextField select label="Rol" required value={assignDraft.rolNombre} onChange={(e) => setAssignDraft((p) => ({ ...p, rolNombre: e.target.value }))} fullWidth>
                    <MenuItem value="">Seleccione</MenuItem>
                    {rolesOptions.map((r) => <MenuItem key={r.value} value={r.value}>{r.label}</MenuItem>)}
                  </TextField>
                  <TextField select label="Empresa" required={isAdmin} value={assignDraft.empresaId} onChange={(e) => setAssignDraft((p) => ({ ...p, empresaId: e.target.value }))} fullWidth>
                    <MenuItem value="">Seleccione</MenuItem>
                    {empresasOptions.map((e) => <MenuItem key={e.value} value={e.value}>{e.label}</MenuItem>)}
                  </TextField>
                  <Stack direction={{ xs: "column", sm: "row" }} spacing={2}>
                    <TextField label="Fecha inicio contrato" type="date" value={assignDraft.iniciaContratoEn} onChange={(e) => setAssignDraft((p) => ({ ...p, iniciaContratoEn: e.target.value }))} fullWidth InputLabelProps={{ shrink: true }} />
                    <TextField label="Fecha fin contrato" type="date" value={assignDraft.finalizaContratoEn} onChange={(e) => setAssignDraft((p) => ({ ...p, finalizaContratoEn: e.target.value }))} fullWidth InputLabelProps={{ shrink: true }} />
                  </Stack>
                  <FormControlLabel control={<Switch checked={assignDraft.preferido} onChange={(e) => setAssignDraft((p) => ({ ...p, preferido: e.target.checked }))} />} label="Rol preferido" />
                  <Button variant="outlined" onClick={addAssign}>Agregar asignación</Button>
                </Stack>
              </Paper>
              <Stack spacing={1.5}>
                {(Array.isArray(formData.asignaciones) ? formData.asignaciones : []).map((a, idx) => (
                  <Paper key={idx} sx={{ p: 2 }}>
                    <Stack direction={{ xs: "column", sm: "row" }} spacing={2} alignItems="center" justifyContent="space-between">
                      <Stack spacing={0.5}>
                        <div>Rol: {a.rolNombre}</div>
                        <div>Empresa: {a.empresaNombre}</div>
                      </Stack>
                      <Stack spacing={0.5} alignItems={{ xs: "flex-start", sm: "flex-end" }}>
                        <div>Inicio: {a.iniciaContratoEn || "-"}</div>
                        <div>Fin: {a.finalizaContratoEn || "Sin fecha"}</div>
                      </Stack>
                      <Stack direction="row" spacing={1} alignItems="center">
                        <FormControlLabel control={<Switch checked={Boolean(a.preferido)} onChange={() => togglePreferido(idx)} />} label="Preferido" />
                        <Button color="error" onClick={() => removeAssign(idx)}>Eliminar</Button>
                      </Stack>
                    </Stack>
                  </Paper>
                ))}
              </Stack>
            </Stack>
          </Grid>
        </Grid>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Cancelar</Button>
        <Button variant="contained" onClick={handleSave}>Guardar</Button>
      </DialogActions>
    </Dialog>
  );
}
