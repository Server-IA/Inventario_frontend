/*=============================================================================
 Nombre del archivo : FormCostoIndirecto.jsx
 Descripcion        : Formulario modal para registrar un costo indirecto
                      (tipo, ubicación en cascada, periodo y valor).
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-09-25 | 0.4.0   | Arekkazu             | Creación del archivo para   |
 |            |         |                      | la HU-045.1.                |
 | 2026-09-26 | 0.4.0   | Arekkazu             | Exporta Selector/useOpciones|
 |            |         |                      | y onCreated (HU-045.2).     |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/
/**
 * @module FormCostoIndirecto
 * @description Modal de registro de costo indirecto. Consume
 * `POST /v1/costos-indirectos` y carga solo catálogos activos.
 */
import React, { useEffect, useState } from "react";
import PropTypes from "prop-types";
import {
  Alert,
  Autocomplete,
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  InputAdornment,
  TextField,
} from "@mui/material";
import { useTranslation } from "react-i18next";
import axios from "../../axiosConfig";
import {
  seleccionarNivel,
  emptyForm,
  validateCostoIndirecto,
  buildPayload,
  parseServerError,
} from "./costoIndirectoValidation";
import { getTipos, getSedes, getBloques, getEspacios, getAlmacenes } from "./costoIndirectoCatalogos";

const IDLE = { options: [], loading: false, error: false };

/**
 * Carga opciones con `loader(arg)` mientras `active` sea true; si no, queda inactivo.
 * @param {function} loader Función estable (nivel de módulo).
 * @param {*} arg Argumento del loader (id del padre o empresa).
 * @param {boolean} active Habilita la carga.
 */
export function useOpciones(loader, arg, active) {
  const [state, setState] = useState(IDLE);

  useEffect(() => {
    if (!active) {
      setState(IDLE);
      return undefined;
    }
    let cancelled = false;
    setState({ ...IDLE, loading: true });
    loader(arg)
      .then((options) => !cancelled && setState({ ...IDLE, options }))
      .catch(() => !cancelled && setState({ ...IDLE, error: true }));
    return () => {
      cancelled = true;
    };
  }, [loader, arg, active]);

  return state;
}

export function Selector({ name, label, value, catalogo, disabled, required, error, helperText, onChange, noOptionsText }) {
  const { t } = useTranslation();
  return (
    <Autocomplete
      options={catalogo.options}
      value={value}
      onChange={(_, nuevo) => onChange(name, nuevo)}
      getOptionLabel={(option) => option.label}
      isOptionEqualToValue={(option, actual) => option.id === actual.id}
      disabled={disabled}
      loading={catalogo.loading}
      loadingText={t("common.labels.loading")}
      noOptionsText={noOptionsText}
      renderInput={(params) => (
        <TextField
          {...params}
          label={label}
          required={required}
          error={Boolean(error) || catalogo.error}
          helperText={error || helperText}
        />
      )}
    />
  );
}

Selector.propTypes = {
  name: PropTypes.string.isRequired,
  label: PropTypes.string.isRequired,
  value: PropTypes.object,
  catalogo: PropTypes.shape({
    options: PropTypes.array,
    loading: PropTypes.bool,
    error: PropTypes.bool,
  }).isRequired,
  disabled: PropTypes.bool,
  required: PropTypes.bool,
  error: PropTypes.string,
  helperText: PropTypes.string,
  onChange: PropTypes.func.isRequired,
  noOptionsText: PropTypes.string,
};

const FULL_ROW = { gridColumn: "1 / -1" };

export default function FormCostoIndirecto({ open, setOpen, setMessage, onCreated }) {
  const { t, i18n } = useTranslation();
  const [form, setForm] = useState(emptyForm);
  const [errors, setErrors] = useState({});
  const [formAlert, setFormAlert] = useState("");
  const [saving, setSaving] = useState(false);

  const tipos = useOpciones(getTipos, undefined, open);
  const sedes = useOpciones(getSedes, undefined, open);
  const bloques = useOpciones(getBloques, form.sede?.id, open && Boolean(form.sede));
  const espacios = useOpciones(getEspacios, form.bloque?.id, open && Boolean(form.bloque));
  const almacenes = useOpciones(getAlmacenes, form.espacio?.id, open && Boolean(form.espacio));

  useEffect(() => {
    if (!open) return;
    setForm(emptyForm);
    setErrors({});
    setFormAlert("");
  }, [open]);

  const catalogFailed = open && (tipos.error || sedes.error);
  useEffect(() => {
    if (!catalogFailed) return;
    setMessage({ open: true, severity: "error", text: t("costos.costoIndirecto.messages.catalogError") });
  }, [catalogFailed, setMessage, t]);

  const t2 = (key, options) => t(`costos.costoIndirecto.${key}`, options);

  const clearError = (campo) => setErrors((prev) => ({ ...prev, [campo]: undefined }));

  const handleSelect = (campo, valor) => {
    setForm((prev) => seleccionarNivel(prev, campo, valor));
    clearError(campo);
  };

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
    const codes = validateCostoIndirecto(form);
    if (Object.keys(codes).length) {
      setErrors(
        Object.fromEntries(Object.entries(codes).map(([campo, code]) => [campo, t2(`form.validation.${code}`)]))
      );
      setFormAlert("");
      return;
    }

    setSaving(true);
    setErrors({});
    setFormAlert("");
    try {
      const { data } = await axios.post("/v1/costos-indirectos", buildPayload(form));
      setMessage({ open: true, severity: "success", text: data?.mensaje ?? t2("messages.created") });
      setOpen(false);
      onCreated?.();
    } catch (error) {
      const { fieldErrors, detail } = parseServerError(error);
      setErrors(fieldErrors);
      if (detail || !Object.keys(fieldErrors).length) {
        setFormAlert(detail ?? t2("messages.saveError"));
      }
    } finally {
      setSaving(false);
    }
  };

  const helperNivel = (padre, catalogo, selectFirstKey) => {
    if (catalogo.error) return t2("form.helpers.catalogError");
    return padre ? t2("form.helpers.optional") : t2(selectFirstKey);
  };

  const valorNumerico = Number(form.valor);
  const valorFormateado =
    valorNumerico > 0
      ? valorNumerico.toLocaleString(i18n.language === "en" ? "en-US" : "es-CO", {
          style: "currency",
          currency: "COP",
          minimumFractionDigits: Number.isInteger(valorNumerico) ? 0 : 2,
          maximumFractionDigits: 2,
        })
      : "";

  const noOptionsText = t2("form.helpers.noOptions");
  const fieldLabel = (campo) => t2(`form.fields.${campo}`);

  return (
    <Dialog
      open={open}
      onClose={handleClose}
      maxWidth="sm"
      fullWidth
      PaperProps={{ component: "form", onSubmit: handleSubmit, noValidate: true }}
    >
      <DialogTitle>{t2("form.title")}</DialogTitle>
      <DialogContent>
        {formAlert && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {formAlert}
          </Alert>
        )}
        <Box sx={{ display: "grid", gridTemplateColumns: { xs: "1fr", sm: "1fr 1fr" }, gap: 2, pt: 1 }}>
          <Box sx={FULL_ROW}>
            <Selector
              name="tipo"
              label={fieldLabel("tipo")}
              value={form.tipo}
              catalogo={tipos}
              required
              error={errors.tipo}
              helperText={tipos.error ? t2("form.helpers.catalogError") : undefined}
              onChange={handleSelect}
              noOptionsText={noOptionsText}
            />
          </Box>
          <Box sx={FULL_ROW}>
            <Selector
              name="sede"
              label={fieldLabel("sede")}
              value={form.sede}
              catalogo={sedes}
              required
              error={errors.sede}
              helperText={sedes.error ? t2("form.helpers.catalogError") : undefined}
              onChange={handleSelect}
              noOptionsText={noOptionsText}
            />
          </Box>
          <Selector
            name="bloque"
            label={fieldLabel("bloque")}
            value={form.bloque}
            catalogo={bloques}
            disabled={!form.sede}
            error={errors.bloque}
            helperText={helperNivel(form.sede, bloques, "form.helpers.selectSedeFirst")}
            onChange={handleSelect}
            noOptionsText={noOptionsText}
          />
          <Selector
            name="espacio"
            label={fieldLabel("espacio")}
            value={form.espacio}
            catalogo={espacios}
            disabled={!form.bloque}
            error={errors.espacio}
            helperText={helperNivel(form.bloque, espacios, "form.helpers.selectBloqueFirst")}
            onChange={handleSelect}
            noOptionsText={noOptionsText}
          />
          <Box sx={FULL_ROW}>
            <Selector
              name="almacen"
              label={fieldLabel("almacen")}
              value={form.almacen}
              catalogo={almacenes}
              disabled={!form.espacio}
              error={errors.almacen}
              helperText={helperNivel(form.espacio, almacenes, "form.helpers.selectEspacioFirst")}
              onChange={handleSelect}
              noOptionsText={noOptionsText}
            />
          </Box>
          <TextField
            name="fechaInicio"
            type="date"
            label={fieldLabel("fechaInicio")}
            value={form.fechaInicio}
            onChange={handleChange}
            required
            error={Boolean(errors.fechaInicio)}
            helperText={errors.fechaInicio}
            InputLabelProps={{ shrink: true }}
          />
          <TextField
            name="fechaFin"
            type="date"
            label={fieldLabel("fechaFin")}
            value={form.fechaFin}
            onChange={handleChange}
            required
            error={Boolean(errors.fechaFin)}
            helperText={errors.fechaFin}
            InputLabelProps={{ shrink: true }}
            inputProps={{ min: form.fechaInicio || undefined }}
          />
          <TextField
            sx={FULL_ROW}
            name="valor"
            type="number"
            label={fieldLabel("valor")}
            value={form.valor}
            onChange={handleChange}
            required
            error={Boolean(errors.valor)}
            helperText={errors.valor || valorFormateado || " "}
            InputProps={{ startAdornment: <InputAdornment position="start">$</InputAdornment> }}
            inputProps={{ min: 0, step: "0.01", inputMode: "decimal" }}
          />
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose} disabled={saving}>
          {t("common.actions.cancel")}
        </Button>
        <Button type="submit" variant="contained" disabled={saving}>
          {saving ? t("common.actions.saving") : t("common.actions.save")}
        </Button>
      </DialogActions>
    </Dialog>
  );
}

FormCostoIndirecto.propTypes = {
  open: PropTypes.bool.isRequired,
  setOpen: PropTypes.func.isRequired,
  setMessage: PropTypes.func.isRequired,
  onCreated: PropTypes.func,
};
