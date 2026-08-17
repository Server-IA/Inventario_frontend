/*=============================================================================
 Nombre del archivo : FormEmpresa.jsx
 Descripcion        : Formulario de registro de empresa (HU-043.1).
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-08-16 | 0.5.0   | Jeisson Sanchez      | HU-043.1 Registrar empresa  |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/
/**
 * Componente del formulario de empresa.
 *
 * Permite registrar una nueva empresa con sus datos generales y un logo
 * opcional (formato PNG/JPG, máximo 2MB).
 *
 * @module FormEmpresa
 * @component
 * @returns {JSX.Element}
 */

import React, { useState } from "react";
import PropTypes from "prop-types";
import axios from "../axiosConfig";
import { useTranslation } from "react-i18next";
import Button from "@mui/material/Button";
import TextField from "@mui/material/TextField";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogContentText from "@mui/material/DialogContentText";
import DialogTitle from "@mui/material/DialogTitle";
import InputLabel from "@mui/material/InputLabel";
import MenuItem from "@mui/material/MenuItem";
import FormControl from "@mui/material/FormControl";
import Select from "@mui/material/Select";

const LOGO_MAX_SIZE = 2 * 1024 * 1024;
const LOGO_ALLOWED_TYPES = ["image/png", "image/jpeg", "image/svg+xml", "image/webp"];

/**
 * @typedef {Object} EmpresaRegistro
 * @property {number|string} tipoIdentificacionId
 * @property {string} identificacion
 * @property {string} nombre
 * @property {string} correo
 * @property {string} celular
 * @property {string} contacto
 * @property {string} descripcion
 * @property {number|string} personaId
 */

/**
 * @param {{
 *   personas: Array<{id: number, nombre: string, apellido: string}>,
 *   tiposIdentificacion: Array<{id: number, nombre: string}>,
 *   setMessage: Function,
 *   reloadData: Function,
 *   open: boolean,
 *   setOpen: Function
 * }} props
 * @returns {JSX.Element}
 */
export default function FormEmpresa({
  personas = [],
  tiposIdentificacion = [],
  setMessage,
  reloadData,
  open,
  setOpen,
}) {
  const { t } = useTranslation();
  const [logoFile, setLogoFile] = useState(null);
  const [logoError, setLogoError] = useState("");
  const [saving, setSaving] = useState(false);

  const handleClose = () => {
    if (saving) return;
    setOpen(false);
    setLogoFile(null);
    setLogoError("");
  };

  const handleLogoChange = (e) => {
    const file = e.target.files?.[0];
    if (!file) {
      setLogoFile(null);
      setLogoError("");
      return;
    }

    if (file.size > LOGO_MAX_SIZE) {
      setLogoError(t("empresa.messages.logoSize", "El archivo supera el tamaño permitido de 2MB"));
      setLogoFile(null);
    } else if (!LOGO_ALLOWED_TYPES.includes(file.type)) {
      setLogoError(t("empresa.messages.logoFormat", "Formato no soportado, usa PNG, JPG, SVG o WEBP"));
      setLogoFile(null);
    } else {
      setLogoError("");
      setLogoFile(file);
    }
  };

  const handleSubmit = (event) => {
    event.preventDefault();

    const formData = new FormData(event.currentTarget);
    const empresa = {
      tipoIdentificacionId: Number(formData.get("tipoIdentificacionId")),
      identificacion: formData.get("identificacion"),
      nombre: formData.get("nombre"),
      correo: formData.get("correo"),
      celular: formData.get("celular") || "",
      contacto: formData.get("contacto") || "",
      descripcion: formData.get("descripcion") || "",
      personaId: Number(formData.get("personaId")),
    };

    const payload = new FormData();
    payload.append(
      "empresa",
      new Blob([JSON.stringify(empresa)], { type: "application/json" })
    );
    if (logoFile) {
      payload.append("logo", logoFile);
    } else if (logoError) {
      setMessage({
        open: true,
        severity: "warning",
        text: t("empresa.messages.logoError", "El logo no se cargó. Corrige el archivo para adjuntarlo."),
      });
    }

    setSaving(true);
    axios
      .post("/v1/empresas", payload, {
        headers: { "Content-Type": "multipart/form-data" },
      })
      .then(() => {
        setMessage({
          open: true,
          severity: "success",
          text: t("empresa.messages.created", "Empresa creada con éxito!"),
        });
        setOpen(false);
        setLogoFile(null);
        setLogoError("");
        reloadData();
      })
      .catch((error) => {
        setMessage({
          open: true,
          severity: "error",
          text: `${t("empresa.messages.saveError", "Error al guardar empresa:")} ${
            error.response?.data?.message || error.message
          }`,
        });
      })
      .finally(() => setSaving(false));
  };

  return (
    <Dialog
      open={open}
      onClose={handleClose}
      maxWidth="sm"
      fullWidth
      PaperProps={{ component: "form", onSubmit: handleSubmit }}
    >
      <DialogTitle>{t("empresa.form.registerTitle", "Registrar Empresa")}</DialogTitle>
      <DialogContent>
        <DialogContentText>
          {t("empresa.form.subtitle", "Completa el formulario.")}
        </DialogContentText>

        <FormControl fullWidth margin="normal" required>
          <InputLabel id="tipoIdentificacionId-label">
            {t("empresa.form.tipoIdentificacion", "Tipo de Identificación")}
          </InputLabel>
          <Select
            labelId="tipoIdentificacionId-label"
            id="tipoIdentificacionId"
            name="tipoIdentificacionId"
            required
            defaultValue=""
            fullWidth
          >
            {tiposIdentificacion.map((tipo) => (
              <MenuItem key={tipo.id} value={tipo.id}>
                {tipo.nombre}
              </MenuItem>
            ))}
          </Select>
        </FormControl>

        <FormControl fullWidth margin="normal">
          <TextField
            required
            id="identificacion"
            name="identificacion"
            label={t("empresa.form.identificacion", "Número de Identificación")}
            variant="standard"
          />
        </FormControl>

        <FormControl fullWidth margin="normal">
          <TextField
            required
            id="nombre"
            name="nombre"
            label={t("empresa.form.nombre", "Nombre")}
            variant="standard"
          />
        </FormControl>

        <FormControl fullWidth margin="normal">
          <TextField
            required
            id="correo"
            name="correo"
            label={t("empresa.form.correo", "Correo")}
            type="email"
            variant="standard"
          />
        </FormControl>

        <FormControl fullWidth margin="normal">
          <TextField
            id="celular"
            name="celular"
            label={t("empresa.form.celular", "Celular")}
            variant="standard"
          />
        </FormControl>

        <FormControl fullWidth margin="normal">
          <TextField
            id="contacto"
            name="contacto"
            label={t("empresa.form.contacto", "Contacto")}
            variant="standard"
          />
        </FormControl>

        <FormControl fullWidth margin="normal">
          <TextField
            id="descripcion"
            name="descripcion"
            label={t("empresa.form.descripcion", "Descripción")}
            variant="standard"
            multiline
            minRows={2}
          />
        </FormControl>

        <FormControl fullWidth margin="normal" required>
          <InputLabel id="personaId-label">
            {t("empresa.form.personaId", "Persona Responsable")}
          </InputLabel>
          <Select
            labelId="personaId-label"
            id="personaId"
            name="personaId"
            required
            defaultValue=""
            fullWidth
          >
            {personas.map((persona) => (
              <MenuItem key={persona.id} value={persona.id}>
                {persona.nombre} {persona.apellido}
              </MenuItem>
            ))}
          </Select>
        </FormControl>

        <FormControl fullWidth margin="normal">
          <Button
            variant="outlined"
            component="label"
            color={logoError ? "error" : "primary"}
            disabled={saving}
          >
            {t("empresa.form.uploadLogo", "Subir Logo (PNG/JPG/SVG/WEBP)")}
            <input
              type="file"
              name="logo"
              hidden
              accept="image/png, image/jpeg, image/svg+xml, image/webp"
              onChange={handleLogoChange}
            />
          </Button>
          {logoFile && !logoError && (
            <span style={{ fontSize: "0.8rem", marginTop: "5px" }}>
              {t("empresa.form.file", "Archivo:")} {logoFile.name}
            </span>
          )}
          {logoError && (
            <span style={{ color: "red", fontSize: "0.8rem", marginTop: "5px" }}>
              {logoError}
            </span>
          )}
        </FormControl>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose} disabled={saving}>
          {t("common.actions.cancel", "Cancelar")}
        </Button>
        <Button type="submit" variant="contained" disabled={saving}>
          {saving
            ? t("common.actions.saving", "Guardando...")
            : t("empresa.actions.create", "Registrar")}
        </Button>
      </DialogActions>
    </Dialog>
  );
}

FormEmpresa.propTypes = {
  personas: PropTypes.array,
  tiposIdentificacion: PropTypes.array,
  setMessage: PropTypes.func.isRequired,
  reloadData: PropTypes.func.isRequired,
  open: PropTypes.bool.isRequired,
  setOpen: PropTypes.func.isRequired,
};