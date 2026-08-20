/*=============================================================================
 Nombre del archivo : Empresa.jsx
 Descripcion        : Componente principal del módulo de empresas (HU-043.1).
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-08-16 | 0.5.0   | Jeisson Sanchez      | HU-043.1 Registrar empresa  |
 | 2026-08-16 | 0.5.1   | Jeisson Sanchez      | HU-043.3 Detalle empresa    |
 | 2026-08-16 | 0.5.2   | Jeisson Sanchez      | HU-043.4 Actualizar empresa |
 | 2026-08-16 | 0.5.3   | Jeisson Sanchez      | HU-043.5 Activar/Inactivar  |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/
/**
 * @file Empresa.jsx
 * @module Empresa
 * @description Componente principal para la gestión de empresas.
 * @author Jeisson Sanchez
 */

import * as React from "react";
import { Button } from "@mui/material";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogContentText from "@mui/material/DialogContentText";
import DialogTitle from "@mui/material/DialogTitle";
import VisibilityIcon from "@mui/icons-material/Visibility";
import ToggleOffIcon from "@mui/icons-material/ToggleOff";
import ToggleOnIcon from "@mui/icons-material/ToggleOn";
import axios from "../axiosConfig";
import { useTranslation } from "react-i18next";
import MessageSnackBar from "../MessageSnackBar";
import GridActionBar from "../common/GridActionBar";
import SectionHeader from "../common/SectionHeader";
import FormEmpresa from "./FormEmpresa";
import GridEmpresa from "./GridEmpresa";
import DetailEmpresa from "./DetailEmpresa";
import { TextField, FormControl, InputLabel, Select, MenuItem, Box } from "@mui/material";

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
  const [formMode, setFormMode] = React.useState("create");
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

  const [selectedRow, setSelectedRow] = React.useState(null);
  const [openDetail, setOpenDetail] = React.useState(false);
  const [detail, setDetail] = React.useState(null);
  const [detailLoading, setDetailLoading] = React.useState(false);
  const [detailError, setDetailError] = React.useState("");
  const [openConfirm, setOpenConfirm] = React.useState(false);
  const [toggling, setToggling] = React.useState(false);

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
    setFormMode("create");
    setSelectedRow(null);
    setOpenForm(true);
  };

  /**
   * Abre el formulario para actualizar la empresa seleccionada.
   */
  const handleUpdate = () => {
    if (!selectedRow) return;
    setFormMode("edit");
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

  /**
   * Consulta el detalle de la empresa seleccionada en el modal.
   */
  const handleView = () => {
    if (!selectedRow) return;
    setOpenDetail(true);
    setDetail(null);
    setDetailError("");
    setDetailLoading(true);

    axios
      .get(\/v1/empresas/\\)
      .then((resp) => {
        setDetail(resp?.data ?? {});
      })
      .catch((err) => {
        console.error("Error al consultar detalle de empresa:", err);
        setDetailError(err.response?.data?.message ?? t("empresa.detail.loadError", "No se pudo cargar el detalle."));
      })
      .finally(() => setDetailLoading(false));
  };

  /** Indica si la empresa seleccionada está activa (estadoId === 1). */
  const isActive = selectedRow?.estadoId === 1;

  /**
   * Ejecuta el cambio de estado de la empresa seleccionada (Activar/Inactivar).
   * Obtiene el detalle completo y envía PUT con el estadoId invertido.
   */
  const confirmToggleEstado = () => {
    if (!selectedRow) return;
    setToggling(true);
    const nuevoEstado = isActive ? 2 : 1;

    axios
      .get(\/v1/empresas/\\)
      .then((res) => {
        const data = res.data;
        return axios.put(
          \/v1/empresas/\\,
          {
            id: data.id,
            nombre: data.nombre,
            descripcion: data.descripcion,
            estadoId: nuevoEstado,
            celular: data.celular,
            correo: data.correo,
            contacto: data.contacto,
            tipoIdentificacionId: data.tipoIdentificacionId,
            personaId: data.personaResponsableId,
            identificacion: data.identificacion,
            logo: data.logo,
          },
          { headers: { "Content-Type": "application/json" } }
        );
      })
      .then(() => {
        setMessage({
          open: true,
          severity: "success",
          text: isActive
            ? t("empresa.messages.inactivated", "Empresa inactivada con éxito!")
            : t("empresa.messages.activated", "Empresa activada con éxito!"),
        });
        setOpenConfirm(false);
        setSelectedRow(null);
        setGridRefreshKey((key) => key + 1);
      })
      .catch((err) => {
        const detail =
          err.response?.data?.detail ||
          err.response?.data?.message ||
          err.message;
        setMessage({
          open: true,
          severity: "error",
          text: \\ \\,
        });
      })
      .finally(() => setToggling(false));
  };

  return (
    <div style={{ height: "100%", width: "100%" }}>
      <SectionHeader title={t("empresa.title", "Gestión de Empresas")} />

      <MessageSnackBar message={message} setMessage={setMessage} />

      <GridActionBar
        onAdd={handleAdd}
        onUpdate={handleUpdate}
        onFilters={handleOpenFilters}
        canUpdate={!!selectedRow}
        canDelete={false}
        extraActions={
          <>
            <Button
              onClick={handleView}
              startIcon={<VisibilityIcon />}
              disabled={!selectedRow}
            >
              {t("common.actions.viewDetail")}
            </Button>
            <Button
              onClick={() => setOpenConfirm(true)}
              startIcon={isActive ? <ToggleOffIcon /> : <ToggleOnIcon />}
              disabled={!selectedRow}
              color={isActive ? "warning" : "success"}
            >
              {isActive
                ? t("empresa.actions.inactivate", "Inactivar")
                : t("empresa.actions.activate", "Activar")}
            </Button>
          </>
        }
      />

      <FormEmpresa
        personas={personas}
        tiposIdentificacion={tiposIdentificacion}
        setMessage={setMessage}
        reloadData={handleEmpresaCreated}
        open={openForm}
        setOpen={setOpenForm}
        formMode={formMode}
        selectedRow={selectedRow}
      />

      <GridEmpresa 
        refreshKey={gridRefreshKey} 
        filters={filters} 
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
      />

      <DetailEmpresa
        open={openDetail}
        data={detail}
        loading={detailLoading}
        error={detailError}
        onClose={() => {
          setOpenDetail(false);
          setDetail(null);
          setDetailError("");
        }}
      />

      {/* Diálogo de confirmación para Activar / Inactivar */}
      <Dialog open={openConfirm} onClose={() => !toggling && setOpenConfirm(false)} maxWidth="xs" fullWidth>
        <DialogTitle>
          {isActive
            ? t("empresa.confirm.inactivateTitle", "Inactivar empresa")
            : t("empresa.confirm.activateTitle", "Activar empresa")}
        </DialogTitle>
        <DialogContent>
          <DialogContentText>
            {isActive
              ? t(
                  "empresa.confirm.inactivateBody",
                  "¿Está seguro de que desea inactivar la empresa «{{nombre}}»?",
                  { nombre: selectedRow?.nombre }
                )
              : t(
                  "empresa.confirm.activateBody",
                  "¿Está seguro de que desea activar la empresa «{{nombre}}»?",
                  { nombre: selectedRow?.nombre }
                )}
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenConfirm(false)} disabled={toggling}>
            {t("common.actions.cancel", "Cancelar")}
          </Button>
          <Button
            onClick={confirmToggleEstado}
            variant="contained"
            disabled={toggling}
            color={isActive ? "warning" : "success"}
          >
            {toggling
              ? t("common.actions.saving", "Guardando...")
              : isActive
              ? t("empresa.actions.inactivate", "Inactivar")
              : t("empresa.actions.activate", "Activar")}
          </Button>
        </DialogActions>
      </Dialog>

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
