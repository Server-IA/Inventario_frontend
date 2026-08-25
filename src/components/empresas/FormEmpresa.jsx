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
import Box from "@mui/material/Box";
import Autocomplete from "@mui/material/Autocomplete";

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
  formMode = "create",
  selectedRow = null,
}) {
  const { t } = useTranslation();
  const [logoFile, setLogoFile] = useState(null);
  const [logoPreview, setLogoPreview] = useState("");
  const [logoError, setLogoError] = useState("");
  const [saving, setSaving] = useState(false);
  const [persona, setPersona] = useState(null);
  const [loadingData, setLoadingData] = useState(false);
  
  // Estados para controlar los valores de los campos (necesario para edición)
  const [formData, setFormData] = useState({
    tipoIdentificacionId: "",
    identificacion: "",
    nombre: "",
    correo: "",
    celular: "",
    contacto: "",
    descripcion: "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  React.useEffect(() => {
    if (open) {
      if (formMode === "edit" && selectedRow) {
        setLoadingData(true);
        axios
          .get(`/v1/empresas/${selectedRow.id}`)
          .then((res) => {
            const data = res.data;
            setFormData({
              tipoIdentificacionId: data.tipoIdentificacionId || "",
              identificacion: data.identificacion || "",
              nombre: data.nombre || "",
              correo: data.correo || "",
              celular: data.celular || "",
              contacto: data.contacto || "",
              descripcion: data.descripcion || "",
            });
            // Buscar la persona en la lista para setear el Autocomplete
            if (data.personaResponsableId && personas.length > 0) {
              const found = personas.find((p) => p.id === data.personaResponsableId);
              setPersona(found || null);
            }
            if (data.logo) {
              setLogoPreview(data.logo);
            }
          })
          .catch((err) => {
            console.error("Error al cargar detalle para edición:", err);
            setMessage({
              open: true,
              severity: "error",
              text: t("empresa.messages.loadError", "Error al cargar los datos de la empresa."),
            });
          })
          .finally(() => setLoadingData(false));
      } else {
        setFormData({
          tipoIdentificacionId: "",
          identificacion: "",
          nombre: "",
          correo: "",
          celular: "",
          contacto: "",
          descripcion: "",
        });
        setPersona(null);
        setLogoPreview("");
        setLogoFile(null);
        setLogoError("");
      }
    }
  }, [open, formMode, selectedRow, personas, t, setMessage]);

  const handleClose = () => {
    if (saving) return;
    if (logoPreview) URL.revokeObjectURL(logoPreview);
    setOpen(false);
    setLogoFile(null);
    setLogoPreview("");
    setLogoError("");
    setPersona(null);
  };

  const handleLogoChange = (e) => {
    const file = e.target.files?.[0];
    if (!file) {
      setLogoFile(null);
      setLogoPreview("");
      setLogoError("");
      return;
    }

    if (file.size > LOGO_MAX_SIZE) {
      setLogoError(t("empresa.messages.logoSize", "El archivo supera el tamaño permitido de 2MB"));
      setLogoFile(null);
      setLogoPreview("");
    } else if (!LOGO_ALLOWED_TYPES.includes(file.type)) {
      setLogoError(t("empresa.messages.logoFormat", "Formato no soportado, usa PNG, JPG, SVG o WEBP"));
      setLogoFile(null);
      setLogoPreview("");
    } else {
      setLogoError("");
      setLogoFile(file);
      setLogoPreview(URL.createObjectURL(file));
    }
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    const empresaPayload = {
      tipoIdentificacionId: Number(formData.tipoIdentificacionId),
      identificacion: formData.identificacion,
      nombre: formData.nombre,
      correo: formData.correo,
      celular: formData.celular,
      contacto: formData.contacto,
      descripcion: formData.descripcion,
      personaId: Number(persona?.id),
    };

    if (!empresaPayload.personaId && formMode === "create") {
      setMessage({
        open: true,
        severity: "warning",
        text: t("empresa.form.validation.personaRequired", "Debe seleccionar una Persona Responsable"),
      });
      return;
    }

    setSaving(true);

    try {
      if (formMode === "edit" && selectedRow) {
        // En edición, solo mandamos los campos permitidos. Eliminamos los no permitidos.
        const updatePayload = {
          identificacion: empresaPayload.identificacion,
          nombre: empresaPayload.nombre,
          correo: empresaPayload.correo,
          celular: empresaPayload.celular,
          contacto: empresaPayload.contacto,
          descripcion: empresaPayload.descripcion,
        };
        
        // Si hay un logo nuevo en edición, lo mandamos como multipart
        if (logoFile) {
          const payloadFormData = new FormData();
          payloadFormData.append(
            "empresa",
            new Blob([JSON.stringify(updatePayload)], { type: "application/json" })
          );
          payloadFormData.append("logo", logoFile);

          const resp = await axios.put(`/v1/empresas/${selectedRow.id}`, payloadFormData, {
            headers: { "Content-Type": "multipart/form-data" },
          });

          // Avisar si el backend indica que el logo no se actualizó (dependiendo de su estructura)
          if (resp.data && resp.data.logoActualizado === false) {
             console.warn("Backend indica que el logo no se actualizó.");
          }
        } else {
          // Si no hay logo nuevo, mandamos json plano
          await axios.put(`/v1/empresas/${selectedRow.id}`, updatePayload, {
            headers: { "Content-Type": "application/json" },
          });
        }

        setMessage({
          open: true,
          severity: "success",
          text: t("empresa.messages.updated", "Empresa actualizada con éxito!"),
        });
      } else {
        const payloadFormData = new FormData();
        payloadFormData.append(
          "empresa",
          new Blob([JSON.stringify(empresaPayload)], { type: "application/json" })
        );
        if (logoFile) {
          payloadFormData.append("logo", logoFile);
        } else if (logoError) {
          setMessage({
            open: true,
            severity: "warning",
            text: t("empresa.messages.logoError", "El logo no se cargó. Corrige el archivo para adjuntarlo."),
          });
        }

        await axios.post("/v1/empresas", payloadFormData, {
          headers: { "Content-Type": "multipart/form-data" },
        });

        setMessage({
          open: true,
          severity: "success",
          text: t("empresa.messages.created", "Empresa creada con éxito!"),
        });
      }

      if (logoPreview && formMode === "create") URL.revokeObjectURL(logoPreview);
      setOpen(false);
      setLogoFile(null);
      setLogoPreview("");
      setLogoError("");
      setPersona(null);
      setFormData({
        tipoIdentificacionId: "",
        identificacion: "",
        nombre: "",
        correo: "",
        celular: "",
        contacto: "",
        descripcion: "",
      });
      reloadData();
    } catch (error) {
      const detail =
        error.response?.data?.detail ||
        error.response?.data?.message ||
        error.message;
      setMessage({
        open: true,
        severity: "error",
        text: `${t("empresa.messages.saveError", "Error al guardar empresa:")} ${detail}`,
      });
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
      PaperProps={{ component: "form", onSubmit: handleSubmit }}
    >
      <DialogTitle>
        {formMode === "edit"
          ? t("empresa.form.editTitle", "Actualizar Empresa")
          : t("empresa.form.registerTitle", "Registrar Empresa")}
      </DialogTitle>
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
            label={t("empresa.form.tipoIdentificacion", "Tipo de Identificación")}
            required
            value={formData.tipoIdentificacionId}
            onChange={handleChange}
            fullWidth
            disabled={loadingData || formMode === "edit"}
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
            value={formData.identificacion}
            onChange={handleChange}
            disabled={loadingData}
            label={t("empresa.form.identificacion", "Número de Identificación")}
          />
        </FormControl>

        <FormControl fullWidth margin="normal">
          <TextField
            required
            id="nombre"
            name="nombre"
            value={formData.nombre}
            onChange={handleChange}
            disabled={loadingData}
            label={t("empresa.form.nombre", "Nombre")}
          />
        </FormControl>

        <FormControl fullWidth margin="normal">
          <TextField
            required
            id="correo"
            name="correo"
            value={formData.correo}
            onChange={handleChange}
            disabled={loadingData}
            label={t("empresa.form.correo", "Correo")}
            type="email"
          />
        </FormControl>

        <FormControl fullWidth margin="normal">
          <TextField
            id="celular"
            name="celular"
            value={formData.celular}
            onChange={handleChange}
            disabled={loadingData}
            label={t("empresa.form.celular", "Celular")}
          />
        </FormControl>

        <FormControl fullWidth margin="normal">
          <TextField
            id="contacto"
            name="contacto"
            value={formData.contacto}
            onChange={handleChange}
            disabled={loadingData}
            label={t("empresa.form.contacto", "Contacto")}
          />
        </FormControl>

        <FormControl fullWidth margin="normal">
          <TextField
            id="descripcion"
            name="descripcion"
            value={formData.descripcion}
            onChange={handleChange}
            disabled={loadingData}
            label={t("empresa.form.descripcion", "Descripción")}
            multiline
            minRows={2}
          />
        </FormControl>

        <FormControl fullWidth margin="normal" required>
          <Autocomplete
            id="personaId"
            options={personas}
            getOptionLabel={(p) => `${p.nombre} ${p.apellido}`}
            value={persona}
            onChange={(event, newValue) => setPersona(newValue)}
            disabled={loadingData || formMode === "edit"}
            filterOptions={(options, state) => {
              const input = (state.inputValue || "").trim().toLowerCase();
              if (!input) return options;
              return options.filter((p) =>
                `${p.nombre} ${p.apellido} ${p.identificacion || ""}`.toLowerCase().includes(input)
              );
            }}
            renderInput={(params) => (
              <TextField
                {...params}
                required
                label={t("empresa.form.personaId", "Persona Responsable")}
                placeholder={t("empresa.form.personaSearch", "Buscar por nombre, apellido o identificación")}
              />
            )}
          />
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
          {/* Preview de logo actual (en edición) o nuevo archivo seleccionado */}
          {!logoFile && logoPreview && !logoError && (
            <Box sx={{ display: "flex", alignItems: "center", gap: 1.5, marginTop: "5px" }}>
              <Box
                component="img"
                src={logoPreview}
                alt={t("empresa.form.logoPreviewAlt", "Vista previa del logo")}
                sx={{
                  width: 56,
                  height: 56,
                  objectFit: "contain",
                  borderRadius: 1,
                  border: "1px solid rgba(23,63,57,0.25)",
                }}
              />
              <span style={{ fontSize: "0.8rem" }}>
                {t("empresa.form.currentLogo", "Logo actual")}
              </span>
            </Box>
          )}
          {logoFile && !logoError && (
            <Box sx={{ display: "flex", alignItems: "center", gap: 1.5, marginTop: "5px" }}>
              {logoPreview && (
                <Box
                  component="img"
                  src={logoPreview}
                  alt={t("empresa.form.logoPreviewAlt", "Vista previa del logo")}
                  sx={{
                    width: 56,
                    height: 56,
                    objectFit: "contain",
                    borderRadius: 1,
                    border: "1px solid rgba(23,63,57,0.25)",
                  }}
                />
              )}
              <span style={{ fontSize: "0.8rem" }}>
                {t("empresa.form.file", "Archivo:")} {logoFile.name}
              </span>
            </Box>
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
        <Button type="submit" variant="contained" disabled={saving || loadingData}>
          {saving
            ? t("common.actions.saving", "Guardando...")
            : formMode === "edit"
            ? t("empresa.actions.update", "Actualizar")
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
  formMode: PropTypes.string,
  selectedRow: PropTypes.object,
};