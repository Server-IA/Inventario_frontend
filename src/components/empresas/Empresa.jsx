/*=============================================================================
 Nombre del archivo : Empresa.jsx
 Descripcion        : Componente principal del módulo de empresas (HU-043.1).
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-08-16 | 0.5.0   | Jeisson Sanchez      | HU-043.1 Registrar empresa  |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/
/**
 * @file Empresa.jsx
 * @module Empresa
 * @description Componente principal para la gestión de empresas.
 * @author Jeisson Sanchez
 */

import * as React from "react";
import axios from "../axiosConfig";
import { useTranslation } from "react-i18next";
import MessageSnackBar from "../MessageSnackBar";
import GridActionBar from "../common/GridActionBar";
import SectionHeader from "../common/SectionHeader";
import FormEmpresa from "./FormEmpresa";
import GridEmpresa from "./GridEmpresa";
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField, FormControl, InputLabel, Select, MenuItem, Box } from "@mui/material";

/**
 * @typedef {Object} SnackbarMessage
 * @property {boolean} open - Si el mensaje está visible
 * @property {string} severity - Nivel de severidad ("success", "error", etc.)
 * @property {string} text - Texto del mensaje
 */

/**
 * Componente principal para la gestión de empresas.
 *
 * @returns {JSX.Element} El módulo de gestión de empresas
 */
export default function Empresa() {
  const { t } = useTranslation();
  const [message, setMessage] = React.useState(
    /** @type {SnackbarMessage} */ ({
      open: false,
      severity: "success",
      text: "",
    })
  );
  const [personas, setPersonas] = React.useState([]);
  const [tiposIdentificacion, setTiposIdentificacion] = React.useState([]);
  const [openForm, setOpenForm] = React.useState(false);
  const [gridRefreshKey, setGridRefreshKey] = React.useState(0);
  const [openFilters, setOpenFilters] = React.useState(false);
  const [filters, setFilters] = React.useState({
    tipoIdentificacionId: "",
    identificacion: "",
    nombre: "",
    correo: "",
    estadoId: "",
  });
  const [tempFilters, setTempFilters] = React.useState(filters);

  /**
   * Carga las personas y los tipos de identificación usados por el formulario.
   */
  const reloadData = React.useCallback(() => {
    axios
      .get("/v1/persona", { params: { page: 0, size: 500 } })
      .then((res) => setPersonas(res.data.content || []))
      .catch((err) => console.error("Error al cargar personas:", err));

    axios
      .get("/v1/tipo_identificacion")
      .then((res) => setTiposIdentificacion(res.data || []))
      .catch((err) => console.error("Error al cargar tipos de identificación:", err));
  }, []);

  /**
   * Recarga datos de soporte y dispara el refresco del listado de empresas.
   * Se usa como callback de éxito del formulario tras registrar una empresa.
   */
  const handleEmpresaCreated = React.useCallback(() => {
    reloadData();
    setGridRefreshKey((key) => key + 1);
  }, [reloadData]);

  React.useEffect(() => {
    reloadData();
  }, [reloadData]);

  /**
   * Abre el formulario de registro de una nueva empresa.
   */
  const handleAdd = () => {
    setOpenForm(true);
  };

  const handleOpenFilters = () => {
    setTempFilters(filters);
    setOpenFilters(true);
  };

  const handleApplyFilters = () => {
    setFilters(tempFilters);
    setOpenFilters(false);
  };

  const handleClearFilters = () => {
    const emptyFilters = {
      tipoIdentificacionId: "",
      identificacion: "",
      nombre: "",
      correo: "",
      estadoId: "",
    };
    setTempFilters(emptyFilters);
    setFilters(emptyFilters);
    setOpenFilters(false);
  };

  return (
    <div style={{ height: "100%", width: "100%" }}>
      <SectionHeader title={t("empresa.title", "Gestión de Empresas")} />

      <MessageSnackBar message={message} setMessage={setMessage} />

      <GridActionBar onAdd={handleAdd} onFilters={handleOpenFilters} />

      <FormEmpresa
        personas={personas}
        tiposIdentificacion={tiposIdentificacion}
        setMessage={setMessage}
        reloadData={handleEmpresaCreated}
        open={openForm}
        setOpen={setOpenForm}
      />

      <GridEmpresa refreshKey={gridRefreshKey} filters={filters} />

      <Dialog open={openFilters} onClose={() => setOpenFilters(false)} maxWidth="sm" fullWidth>
        <DialogTitle>{t("common.actions.filters", "Filtros")}</DialogTitle>
        <DialogContent>
          <Box sx={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 2, mt: 1 }}>
            <FormControl fullWidth size="small">
              <InputLabel>{t("empresa.grid.tipoIdentificacion", "Tipo de Identificación")}</InputLabel>
              <Select
                value={tempFilters.tipoIdentificacionId}
                label={t("empresa.grid.tipoIdentificacion", "Tipo de Identificación")}
                onChange={(e) => setTempFilters({ ...tempFilters, tipoIdentificacionId: e.target.value })}
              >
                <MenuItem value="">{t("common.labels.all", "Todos")}</MenuItem>
                {tiposIdentificacion.map((tipo) => (
                  <MenuItem key={tipo.id} value={tipo.id}>
                    {tipo.nombre}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>

            <TextField
              size="small"
              fullWidth
              label={t("empresa.grid.identificacion", "No. de Identificación")}
              value={tempFilters.identificacion}
              onChange={(e) => setTempFilters({ ...tempFilters, identificacion: e.target.value })}
            />

            <TextField
              size="small"
              fullWidth
              label={t("empresa.grid.nombre", "Nombre")}
              value={tempFilters.nombre}
              onChange={(e) => setTempFilters({ ...tempFilters, nombre: e.target.value })}
            />

            <TextField
              size="small"
              fullWidth
              label={t("empresa.grid.correo", "Correo")}
              value={tempFilters.correo}
              onChange={(e) => setTempFilters({ ...tempFilters, correo: e.target.value })}
            />

            <FormControl fullWidth size="small">
              <InputLabel>{t("empresa.grid.estado", "Estado")}</InputLabel>
              <Select
                value={tempFilters.estadoId}
                label={t("empresa.grid.estado", "Estado")}
                onChange={(e) => setTempFilters({ ...tempFilters, estadoId: e.target.value })}
              >
                <MenuItem value="">{t("common.labels.all", "Todos")}</MenuItem>
                <MenuItem value={1}>{t("empresa.estado.activo", "Activo")}</MenuItem>
                <MenuItem value={2}>{t("empresa.estado.inactivo", "Inactivo")}</MenuItem>
              </Select>
            </FormControl>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClearFilters}>{t("common.actions.clear", "Limpiar")}</Button>
          <Button variant="contained" onClick={handleApplyFilters}>
            {t("common.actions.apply", "Aplicar")}
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}