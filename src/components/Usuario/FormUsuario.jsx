import React, { useEffect, useMemo, useState } from "react";
import { Dialog, DialogTitle, DialogContent, DialogActions, Stack, TextField, MenuItem, Button, Grid, Paper, Switch, FormControlLabel } from "@mui/material";
import { useTranslation } from "react-i18next";
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
  const { t } = useTranslation();
  const [formData, setFormData] = useState(initialData);
  const [assignDraft, setAssignDraft] = useState({ rolId: "", rolNombre: "", empresaId: "", empresaNombre: "", iniciaContratoEn: "", finalizaContratoEn: "", preferido: false, estadoId: 1 });
  useEffect(() => {
    setFormData(initialData);
    setAssignDraft({ rolId: "", rolNombre: "", empresaId: "", empresaNombre: "", iniciaContratoEn: "", finalizaContratoEn: "", preferido: false, estadoId: 1 });
  }, [initialData, open]);
  const handleChange = (name, value) => setFormData((p) => ({ ...p, [name]: value }));
  const addAssign = () => {
    const empresa = empresas.find((e) => String(e.id) === String(assignDraft.empresaId));
    const rol = roles.find((r) => String(r.id) === String(assignDraft.rolId));
    const draft = { ...assignDraft, empresaNombre: empresa?.nombre ?? assignDraft.empresaNombre, rolNombre: rol?.nombre ?? assignDraft.rolNombre };
    let asignaciones = Array.isArray(formData.asignaciones) ? [...formData.asignaciones] : [];
    if (draft.preferido) asignaciones = asignaciones.map((a) => ({ ...a, preferido: false }));
    asignaciones.push(draft);
    setFormData((p) => ({ ...p, asignaciones }));
    setAssignDraft({ rolId: "", rolNombre: "", empresaId: "", empresaNombre: "", iniciaContratoEn: "", finalizaContratoEn: "", preferido: false, estadoId: 1 });
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
  const rolesOptions = useMemo(() => (Array.isArray(roles) ? roles : []).map((r) => ({ value: String(r.id), label: r.nombre ?? r.name ?? String(r.id) })), [roles]);
  const empresasOptions = useMemo(() => empresas.map((e) => ({ value: String(e.id), label: e.nombre })), [empresas]);
  return (
    <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
      <DialogTitle>{mode === "create" ? t("usuario.form.createTitle") : t("usuario.form.editTitle")}</DialogTitle>
      <DialogContent dividers>
        <Grid container spacing={3}>
          <Grid item xs={12} md={6}>
            <Stack spacing={2}>
              <TextField label={t("usuario.form.fields.username")} type="email" required value={formData.username} onChange={(e) => handleChange("username", e.target.value)} fullWidth />
              <Stack direction={{ xs: "column", sm: "row" }} spacing={2}>
                <TextField label={t("usuario.form.fields.name")} required value={formData.nombre} onChange={(e) => handleChange("nombre", e.target.value)} fullWidth />
                <TextField label={t("usuario.form.fields.lastName")} required value={formData.apellido} onChange={(e) => handleChange("apellido", e.target.value)} fullWidth />
              </Stack>
              <Stack direction={{ xs: "column", sm: "row" }} spacing={2}>
                <TextField label={t("usuario.form.fields.gender")} value={formData.genero} onChange={(e) => handleChange("genero", e.target.value)} fullWidth />
                <TextField label={t("usuario.form.fields.stratum")} value={formData.estrato} onChange={(e) => handleChange("estrato", e.target.value)} fullWidth />
              </Stack>
              <Stack direction={{ xs: "column", sm: "row" }} spacing={2}>
                <TextField label={t("usuario.form.fields.documentType")} value={formData.tipoDocumentoIdentidadId} onChange={(e) => handleChange("tipoDocumentoIdentidadId", e.target.value)} fullWidth />
                <TextField label={t("usuario.form.fields.documentNumber")} value={formData.codigoIdentificacion} onChange={(e) => handleChange("codigoIdentificacion", e.target.value)} fullWidth />
              </Stack>
              <Stack direction={{ xs: "column", sm: "row" }} spacing={2}>
                <TextField label={t("usuario.form.fields.birthDate")} type="date" value={formData.fechaNacimiento} onChange={(e) => handleChange("fechaNacimiento", e.target.value)} fullWidth InputLabelProps={{ shrink: true }} />
                <TextField label={t("usuario.form.fields.phone")} value={formData.celular} onChange={(e) => handleChange("celular", e.target.value)} fullWidth />
              </Stack>
              <TextField label={t("usuario.form.fields.address")} value={formData.direccion} onChange={(e) => handleChange("direccion", e.target.value)} fullWidth />
              <TextField select label={t("usuario.form.fields.status")} value={formData.estadoId} onChange={(e) => handleChange("estadoId", Number(e.target.value))} fullWidth>
                <MenuItem value={1}>{t("common.labels.active")}</MenuItem>
                <MenuItem value={2}>{t("common.labels.inactive")}</MenuItem>
              </TextField>
            </Stack>
          </Grid>
          <Grid item xs={12} md={6}>
            <Stack spacing={2}>
              <Paper sx={{ p: 2 }}>
                <Stack spacing={2}>
                  <TextField select label={t("usuario.form.fields.role")} required value={assignDraft.rolId} onChange={(e) => setAssignDraft((p) => ({ ...p, rolId: e.target.value }))} fullWidth>
                    <MenuItem value="">{t("common.labels.select")}</MenuItem>
                    {rolesOptions.map((r) => <MenuItem key={r.value} value={r.value}>{r.label}</MenuItem>)}
                  </TextField>
                  <TextField select label={t("usuario.form.fields.company")} required={isAdmin} value={assignDraft.empresaId} onChange={(e) => setAssignDraft((p) => ({ ...p, empresaId: e.target.value }))} fullWidth>
                    <MenuItem value="">{t("common.labels.select")}</MenuItem>
                    {empresasOptions.map((e) => <MenuItem key={e.value} value={e.value}>{e.label}</MenuItem>)}
                  </TextField>
                  <Stack direction={{ xs: "column", sm: "row" }} spacing={2}>
                    <TextField label={t("usuario.form.fields.contractStart")} type="date" value={assignDraft.iniciaContratoEn} onChange={(e) => setAssignDraft((p) => ({ ...p, iniciaContratoEn: e.target.value }))} fullWidth InputLabelProps={{ shrink: true }} />
                    <TextField label={t("usuario.form.fields.contractEnd")} type="date" value={assignDraft.finalizaContratoEn} onChange={(e) => setAssignDraft((p) => ({ ...p, finalizaContratoEn: e.target.value }))} fullWidth InputLabelProps={{ shrink: true }} />
                  </Stack>
                  <FormControlLabel control={<Switch checked={assignDraft.preferido} onChange={(e) => setAssignDraft((p) => ({ ...p, preferido: e.target.checked }))} />} label={t("usuario.form.fields.preferredRole")} />
                  <Button variant="outlined" onClick={addAssign}>{t("common.actions.addAssignment")}</Button>
                </Stack>
              </Paper>
              <Stack spacing={1.5}>
                {(Array.isArray(formData.asignaciones) ? formData.asignaciones : []).map((a, idx) => (
                  <Paper key={idx} sx={{ p: 2 }}>
                    <Stack direction={{ xs: "column", sm: "row" }} spacing={2} alignItems="center" justifyContent="space-between">
                      <Stack spacing={0.5}>
                        <div>{t("common.labels.role")}: {a.rolNombre}</div>
                        <div>{t("common.labels.company")}: {a.empresaNombre}</div>
                      </Stack>
                      <Stack spacing={0.5} alignItems={{ xs: "flex-start", sm: "flex-end" }}>
                        <div>{t("common.labels.start")}: {a.iniciaContratoEn || "-"}</div>
                        <div>{t("common.labels.end")}: {a.finalizaContratoEn || t("common.labels.withoutDate")}</div>
                      </Stack>
                      <Stack direction="row" spacing={1} alignItems="center">
                        <FormControlLabel control={<Switch checked={Boolean(a.preferido)} onChange={() => togglePreferido(idx)} />} label={t("usuario.form.fields.preferred")} />
                        <Button color="error" onClick={() => removeAssign(idx)}>{t("common.actions.delete")}</Button>
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
        <Button onClick={onClose}>{t("common.actions.cancel")}</Button>
        <Button variant="contained" onClick={handleSave}>{t("common.actions.save")}</Button>
      </DialogActions>
    </Dialog>
  );
}
