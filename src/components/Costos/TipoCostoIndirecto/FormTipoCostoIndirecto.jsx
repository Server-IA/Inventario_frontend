/*=============================================================================
 Nombre del archivo : FormTipoCostoIndirecto.jsx
 Descripcion        : Formulario modal para registrar o actualizar un Tipo de Costo Indirecto.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-10-16 | 0.4.0   | Jeisson Sanchez      | Creación del modal de registro para HU-044.1. |
 | 2026-10-16 | 0.4.0   | Jeisson Sanchez      | Habilitar edición mediante PUT para HU-044.3. |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/
import React, { useState, useEffect } from "react";
import PropTypes from "prop-types";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Box,
  TextField,
  Alert,
  Typography,
} from "@mui/material";
import { useTranslation } from "react-i18next";
import axios from "../../axiosConfig";
import {
  emptyForm,
  validateTipoCostoIndirecto,
  buildPayload,
  parseServerError,
} from "./tipoCostoIndirectoValidation";

export default function FormTipoCostoIndirecto({
  open,
  setOpen,
  selectedRow = null,
  setMessage,
  onCreated,
}) {
  const { t } = useTranslation();
  const isEdit = Boolean(selectedRow?.id);
  const [form, setForm] = useState(emptyForm);
  const [errors, setErrors] = useState({});
  const [formAlert, setFormAlert] = useState("");
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (!open) return;
    if (isEdit && selectedRow) {
      setForm({
        nombre: selectedRow.nombre || "",
        descripcion: selectedRow.descripcion || "",
      });
    } else {
      setForm(emptyForm);
    }
    setErrors({});
    setFormAlert("");
  }, [open, isEdit, selectedRow]);

  const clearError = (field) => setErrors((prev) => ({ ...prev, [field]: undefined }));

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
    clearError(name);
  };

  const handleClose = () => {
    if (!saving) setOpen(false);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    const codes = validateTipoCostoIndirecto(form);
    if (Object.keys(codes).length > 0) {
      const translatedErrors = {};
      Object.entries(codes).forEach(([field, code]) => {
        translatedErrors[field] = t(`costos.tipoCostoIndirecto.form.validation.${code}`);
      });
      setErrors(translatedErrors);
      setFormAlert("");
      return;
    }

    setSaving(true);
    setErrors({});
    setFormAlert("");

    const payload = buildPayload(form);

    try {
      const { data } = isEdit
        ? await axios.put(`/v1/tipos-costos-indirectos/${selectedRow.id}`, payload)
        : await axios.post("/v1/tipos-costos-indirectos", payload);

      const successText =
        data?.mensaje ||
        t(
          isEdit
            ? "costos.tipoCostoIndirecto.messages.updated"
            : "costos.tipoCostoIndirecto.messages.created"
        );

      setMessage({
        open: true,
        severity: "success",
        text: successText,
      });

      setOpen(false);
      onCreated?.();
    } catch (error) {
      const { fieldErrors, detail, status } = parseServerError(error);
      setErrors(fieldErrors);

      if (status === 409) {
        setFormAlert(
          detail ||
            t(
              "costos.tipoCostoIndirecto.messages.duplicateName",
              "Ya existe un tipo de costo indirecto con ese nombre para la empresa."
            )
        );
      } else {
        setFormAlert(
          detail ||
            t(
              "costos.tipoCostoIndirecto.messages.saveError",
              "No se pudo guardar el tipo de costo indirecto."
            )
        );
      }
    } finally {
      setSaving(false);
    }
  };

  return (
    <Dialog
      open={open}
      onClose={handleClose}
      maxWidth="sm"
      fullWidth
      PaperProps={{ component: "form", onSubmit: handleSubmit, noValidate: true }}
    >
      <DialogTitle>
        {t(
          isEdit
            ? "costos.tipoCostoIndirecto.form.editTitle"
            : "costos.tipoCostoIndirecto.form.title",
          isEdit ? "Actualizar tipo de costo indirecto" : "Registrar tipo de costo indirecto"
        )}
      </DialogTitle>

      <DialogContent dividers>
        {formAlert && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {formAlert}
          </Alert>
        )}

        <Box sx={{ display: "flex", flexDirection: "column", gap: 2.5, pt: 1 }}>
          <TextField
            name="nombre"
            label={t("costos.tipoCostoIndirecto.form.fields.nombre", "Nombre")}
            value={form.nombre}
            onChange={handleChange}
            required
            fullWidth
            error={Boolean(errors.nombre)}
            helperText={
              errors.nombre ||
              t("costos.tipoCostoIndirecto.form.helpers.charLimit", {
                count: form.nombre.length,
                max: 100,
              })
            }
            inputProps={{ maxLength: 100 }}
            InputLabelProps={{ shrink: true }}
            autoFocus
          />

          <Box>
            <TextField
              name="descripcion"
              label={t("costos.tipoCostoIndirecto.form.fields.descripcion", "Descripción")}
              value={form.descripcion}
              onChange={handleChange}
              fullWidth
              multiline
              rows={3}
              error={Boolean(errors.descripcion)}
              helperText={errors.descripcion}
              inputProps={{ maxLength: 2048 }}
              InputLabelProps={{ shrink: true }}
            />
            <Typography
              variant="caption"
              color="text.secondary"
              sx={{ display: "block", textAlign: "right", mt: 0.5 }}
            >
              {t("costos.tipoCostoIndirecto.form.helpers.charLimit", {
                count: form.descripcion.length,
                max: 2048,
              })}
            </Typography>
          </Box>
        </Box>
      </DialogContent>

      <DialogActions sx={{ px: 2.5, py: 1.5 }}>
        <Button onClick={handleClose} disabled={saving} color="inherit">
          {t("common.actions.cancel", "Cancelar")}
        </Button>
        <Button type="submit" variant="contained" disabled={saving}>
          {saving
            ? t("common.actions.saving", "Guardando...")
            : t("common.actions.save", "Guardar")}
        </Button>
      </DialogActions>
    </Dialog>
  );
}

FormTipoCostoIndirecto.propTypes = {
  open: PropTypes.bool.isRequired,
  setOpen: PropTypes.func.isRequired,
  selectedRow: PropTypes.object,
  setMessage: PropTypes.func.isRequired,
  onCreated: PropTypes.func,
};
