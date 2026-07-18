/*=============================================================================
 Nombre del archivo : FormEmpresa.jsx
 Descripcion        : Componente principal del formulario de empresa.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-07-18 | 0.5.0   | Jeisson Sanchez      | HU-043 Gestión de Empresas  |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/
/**
 * Componente principal del formulario de empresa.
 *
 * Este componente permite crear, actualizar o eliminar una empresa mediante un formulario emergente.
 *
 * @module FormEmpresa
 * @component
 * @returns {JSX.Element}
 */

import React, { useState } from "react";
import PropTypes from "prop-types";
import axios from "axios";
import { useTranslation } from 'react-i18next';
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
import { SiteProps } from "../dashboard/SiteProps";

/**
 * @typedef {Object} EmpresaRow
 * @property {number} id
 * @property {string} nombre
 * @property {string} descripcion
 * @property {number} estado
 * @property {string} celular
 * @property {string} correo
 * @property {string} contacto
 * @property {number|string} tipoIdentificacionId
 * @property {number|string} personaId
 * @property {string} identificacion
 * @property {string} logo
 */

/**
 * @typedef {Object} SnackbarMessage
 * @property {boolean} open
 * @property {string} severity
 * @property {string} text
 */

/**
 * @param {{
 *   selectedRow: EmpresaRow,
 *   setSelectedRow: Function,
 *   setMessage: Function,
 *   reloadData: Function
 * }} props
 * @returns {JSX.Element}
 */
export default function FormEmpresa({ selectedRow, setSelectedRow, setMessage, reloadData, personas = [], tiposIdentificacion = [], open, setOpen, methodName }) {
  const { t } = useTranslation();
  const [logoFile, setLogoFile] = useState(null);
  const [logoError, setLogoError] = useState("");

  const handleClose = () => {
    setOpen(false);
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    if (logoError) {
      setMessage({ open: true, severity: "error", text: t('empresa.messages.logoError', 'Corrige los errores del logo antes de guardar.') });
      return;
    }
    const formData = new FormData(event.currentTarget);
    const formJson = Object.fromEntries(formData.entries());

    const url = `${SiteProps.urlbasev1}/empresas`;
    const method = methodName === "Add" ? axios.post : axios.put;
    const endpoint = methodName === "Add" ? url : `${url}/${selectedRow.id}`;

    method(endpoint, formJson, {
      headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
    })
      .then(() => {
        setMessage({
          open: true,
          severity: "success",
          text: methodName === "Add" || methodName === t('empresa.actions.create', 'Crear') ? t('empresa.messages.created', 'Empresa creada con éxito!') : t('empresa.messages.updated', 'Empresa actualizada con éxito!'),
        });
        setOpen(false);
        reloadData();
      })
      .catch((error) => {
        setMessage({
          open: true,
          severity: "error",
          text: `${t('empresa.messages.saveError', 'Error al guardar empresa:')} ${error.response?.data.message || error.message}`,
        });
      });
  };

  return (
    <>
      <Dialog open={open} onClose={handleClose} PaperProps={{ component: "form", onSubmit: handleSubmit }}>
        <DialogTitle>{methodName === "Add" ? t('empresa.actions.create', 'Crear') : methodName} {t('empresa.titleSingle', 'Empresa')}</DialogTitle>
        <DialogContent>
          <DialogContentText>{t('empresa.form.subtitle', 'Completa el formulario.')}</DialogContentText>

          <FormControl fullWidth margin="normal">
            <TextField required id="nombre" name="nombre" label={t('empresa.form.nombre', 'Nombre')} variant="standard" defaultValue={selectedRow?.nombre || ""} />
          </FormControl>

          <FormControl fullWidth margin="normal">
            <TextField required id="descripcion" name="descripcion" label={t('empresa.form.descripcion', 'Descripción')} variant="standard" defaultValue={selectedRow?.descripcion || ""} />
          </FormControl>

          <FormControl fullWidth margin="normal">
            <InputLabel id="estado-label">{t('empresa.form.estado', 'Estado')}</InputLabel>
            <Select labelId="estado-label" id="estado" name="estado" defaultValue={selectedRow?.estado || 1} fullWidth>
              <MenuItem value={1}>{t('empresa.estado.activo', 'Activo')}</MenuItem>
              <MenuItem value={0}>{t('empresa.estado.inactivo', 'Inactivo')}</MenuItem>
            </Select>
          </FormControl>

          <FormControl fullWidth margin="normal">
            <TextField required id="celular" name="celular" label={t('empresa.form.celular', 'Celular')} variant="standard" defaultValue={selectedRow?.celular || ""} />
          </FormControl>

          <FormControl fullWidth margin="normal">
            <TextField required id="correo" name="correo" label={t('empresa.form.correo', 'Correo')} type="email" variant="standard" defaultValue={selectedRow?.correo || ""} />
          </FormControl>

          <FormControl fullWidth margin="normal">
            <TextField required id="contacto" name="contacto" label={t('empresa.form.contacto', 'Contacto')} variant="standard" defaultValue={selectedRow?.contacto || ""} />
          </FormControl>

          <FormControl fullWidth margin="normal">
            <InputLabel id="tipoIdentificacionId-label">{t('empresa.form.tipoIdentificacion', 'Tipo de Identificación')}</InputLabel>
            <Select labelId="tipoIdentificacionId-label" id="tipoIdentificacionId" name="tipoIdentificacionId" defaultValue={selectedRow?.tipoIdentificacionId || ""} fullWidth>
              {tiposIdentificacion.map((tipo) => (
                <MenuItem key={tipo.id} value={tipo.id}>{tipo.name}</MenuItem>
              ))}
            </Select>
          </FormControl>

          <FormControl fullWidth margin="normal">
            <InputLabel id="personaId-label">{t('empresa.form.personaId', 'Persona Responsable')}</InputLabel>
            <Select labelId="personaId-label" id="personaId" name="personaId" defaultValue={selectedRow?.personaId || ""} fullWidth>
              {personas.map((persona) => (
                <MenuItem key={persona.id} value={persona.id}>{persona.nombre} {persona.apellido}</MenuItem>
              ))}
            </Select>
          </FormControl>

          <FormControl fullWidth margin="normal">
            <TextField required id="identificacion" name="identificacion" label={t('empresa.form.identificacion', 'Número de Identificación')} variant="standard" defaultValue={selectedRow?.identificacion || ""} />
          </FormControl>

          {/* Logo Upload Field */}
          <FormControl fullWidth margin="normal">
            <Button variant="outlined" component="label" color={logoError ? "error" : "primary"}>
              {t('empresa.form.uploadLogo', 'Subir Logo (PNG/JPG)')}
              <input
                type="file"
                name="logo"
                hidden
                accept="image/png, image/jpeg"
                onChange={(e) => {
                  const file = e.target.files?.[0];
                  if (!file) {
                    setLogoFile(null);
                    setLogoError("");
                    return;
                  }
                  if (file.size > 2 * 1024 * 1024) {
                    setLogoError(t('empresa.messages.logoSize', 'El archivo supera el tamaño permitido de 2MB'));
                  } else if (!["image/png", "image/jpeg"].includes(file.type)) {
                    setLogoError(t('empresa.messages.logoFormat', 'Formato no soportado, usa PNG o JPG'));
                  } else {
                    setLogoError("");
                  }
                  setLogoFile(file);
                }}
              />
            </Button>
            {logoFile && !logoError && <span style={{ fontSize: '0.8rem', marginTop: '5px' }}>{t('empresa.form.file', 'Archivo:')} {logoFile.name}</span>}
            {logoError && <span style={{ color: 'red', fontSize: '0.8rem', marginTop: '5px' }}>{logoError}</span>}
          </FormControl>

        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>{t('common.cancel', 'Cancelar')}</Button>
          <Button type="submit">{methodName === "Add" ? t('empresa.actions.create', 'Crear') : methodName}</Button>
        </DialogActions>
      </Dialog>
    </>
  );
}

// Validación de Props con PropTypes
FormEmpresa.propTypes = {
  selectedRow: PropTypes.object.isRequired,
  setSelectedRow: PropTypes.func.isRequired,
  setMessage: PropTypes.func.isRequired,
  reloadData: PropTypes.func.isRequired,
  personas: PropTypes.array,
  tiposIdentificacion: PropTypes.array,
  open: PropTypes.bool.isRequired,
  setOpen: PropTypes.func.isRequired,
  methodName: PropTypes.string.isRequired,
};
