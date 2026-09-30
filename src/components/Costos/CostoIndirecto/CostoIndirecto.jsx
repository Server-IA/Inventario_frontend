/*=============================================================================
 Nombre del archivo : CostoIndirecto.jsx
 Descripcion        : Módulo Gestión de Costos Indirectos del subsistema Costos.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-09-25 | 0.4.0   | Arekkazu             | Creación del archivo para   |
 |            |         |                      | la HU-045.1 (solo registro).|
 | 2026-09-26 | 0.4.0   | Arekkazu             | Listado paginado con filtros|
 |            |         |                      | (HU-045.2).                 |
 | 2026-09-26 | 0.4.0   | Arekkazu             | Chips de filtros activos    |
 |            |         |                      | (HU-045.2).                 |
 | 2026-09-27 | 0.5.0   | Arekkazu             | Selección de fila y edición |
 |            |         |                      | mediante PUT (HU-045.3).    |
 | 2026-09-27 | 0.6.0   | Arekkazu             | Eliminación lógica con      |
 |            |         |                      | confirmación (HU-045.4).    |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/
/**
 * @module CostoIndirecto
 * @description Contenedor del módulo: encabezado, barra de acciones, listado
 * paginado de `GET /v1/costos-indirectos` con filtros y modal de
 * registro/edición/eliminación lógica.
 */
import React, { useEffect, useMemo, useState } from "react";
import { Box, Button, Chip, Dialog, DialogActions, DialogContent, DialogTitle, Stack } from "@mui/material";
import { useTranslation } from "react-i18next";
import axios from "../../axiosConfig";
import SectionHeader from "../../common/SectionHeader.jsx";
import GridActionBar from "../../common/GridActionBar.jsx";
import AppDataGrid from "../../common/AppDataGrid.jsx";
import MessageSnackBar from "../../MessageSnackBar.jsx";
import FormCostoIndirecto from "./FormCostoIndirecto.jsx";
import FiltroCostoIndirecto, { filtrosVacios, toParams } from "./FiltroCostoIndirecto.jsx";
import { parseServerError, seleccionarNivel } from "./costoIndirectoValidation";

// El backend envía fechas `yyyy-MM-dd`; se leen como fecha local para que no se corran un día por UTC.
const fechaLocal = ({ value }) => (value ? new Date(`${value}T00:00:00`) : null);
const FORMATO_FECHA = { day: "2-digit", month: "2-digit", year: "numeric" };

export default function CostoIndirecto() {
  const { t, i18n } = useTranslation();
  const [formOpen, setFormOpen] = useState(false);
  const [selectedRow, setSelectedRow] = useState(null);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const [filtersOpen, setFiltersOpen] = useState(false);
  const [filtros, setFiltros] = useState(filtrosVacios);
  const [paginationModel, setPaginationModel] = useState({ page: 0, size: 10 });
  const [rows, setRows] = useState([]);
  const [rowCount, setRowCount] = useState(0);
  const [loading, setLoading] = useState(false);
  const [refreshKey, setRefreshKey] = useState(0);
  const [message, setMessage] = useState({ open: false, severity: "success", text: "" });

  const params = useMemo(() => toParams(filtros), [filtros]);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    axios
      .get("/v1/costos-indirectos", {
        params: { page: paginationModel.page, size: paginationModel.size, ...params },
      })
      .then(({ data }) => {
        if (cancelled) return;
        setRows(data?.data ?? []);
        setRowCount(data?.header?.totalElements ?? 0);
      })
      .catch((error) => {
        if (cancelled) return;
        setRows([]);
        setRowCount(0);
        setMessage({
          open: true,
          severity: "error",
          text: parseServerError(error).detail ?? t("costos.costoIndirecto.messages.listError"),
        });
      })
      .finally(() => !cancelled && setLoading(false));
    return () => {
      cancelled = true;
    };
  }, [paginationModel, params, refreshKey, t]);

  const applyFilters = (nuevos) => {
    setFiltros(nuevos);
    setPaginationModel((prev) => ({ ...prev, page: 0 }));
    setFiltersOpen(false);
  };

  const handleCreate = () => {
    setSelectedRow(null);
    setFormOpen(true);
  };

  const handleUpdate = () => {
    if (!selectedRow?.id) {
      setMessage({ open: true, severity: "warning", text: t("common.messages.selectRow") });
      return;
    }
    setFormOpen(true);
  };

  const handleDeleteIntent = () => {
    if (!selectedRow?.id) {
      setMessage({ open: true, severity: "warning", text: t("common.messages.selectRow") });
      return;
    }
    setConfirmOpen(true);
  };

  const confirmarEliminacion = async () => {
    setDeleting(true);
    try {
      await axios.delete(`/v1/costos-indirectos/${selectedRow.id}`);
      setMessage({ open: true, severity: "success", text: t("costos.costoIndirecto.messages.deleted") });
      setSelectedRow(null);
      setRefreshKey((key) => key + 1);
    } catch (error) {
      setMessage({
        open: true,
        severity: "error",
        text: parseServerError(error).detail ?? t("costos.costoIndirecto.messages.deleteError"),
      });
    } finally {
      setDeleting(false);
      setConfirmOpen(false);
    }
  };

  const columns = useMemo(
    () => [
      { field: "tipoCostoIndirectoNombre", headerKey: "costos.costoIndirecto.columns.tipo", flex: 1, minWidth: 110 },
      { field: "sedeNombre", headerKey: "costos.costoIndirecto.columns.sede", flex: 1, minWidth: 110 },
      { field: "bloqueNombre", headerKey: "costos.costoIndirecto.columns.bloque", flex: 1, minWidth: 110 },
      { field: "espacioNombre", headerKey: "costos.costoIndirecto.columns.espacio", flex: 1, minWidth: 110 },
      { field: "almacenNombre", headerKey: "costos.costoIndirecto.columns.almacen", flex: 1, minWidth: 110 },
      {
        field: "fechaInicio",
        headerKey: "costos.costoIndirecto.columns.fechaInicio",
        type: "date",
        valueGetter: fechaLocal,
        dateFormatOptions: FORMATO_FECHA,
        width: 120,
      },
      {
        field: "fechaFin",
        headerKey: "costos.costoIndirecto.columns.fechaFin",
        type: "date",
        valueGetter: fechaLocal,
        dateFormatOptions: FORMATO_FECHA,
        width: 120,
      },
      {
        field: "valor",
        headerKey: "costos.costoIndirecto.columns.valor",
        type: "number",
        numberFormatOptions: { style: "currency", currency: "COP", maximumFractionDigits: 2 },
        width: 160,
      },
      { field: "estadoNombre", headerKey: "costos.costoIndirecto.columns.estado", statusChip: true, width: 110 },
    ],
    []
  );
  const localeText = useMemo(() => ({ noRowsLabel: t("costos.costoIndirecto.grid.empty") }), [t]);

  // Un chip por filtro aplicado: [campo, [etiqueta, valor]]. Quitar uno limpia también sus niveles inferiores.
  const t2 = (key) => t(`costos.costoIndirecto.${key}`);
  const fecha = (value) => fechaLocal({ value }).toLocaleDateString(i18n.language, FORMATO_FECHA);
  const estado = filtros.estadoId && t(Number(filtros.estadoId) === 1 ? "common.labels.active" : "common.labels.inactive");
  const chips = Object.entries({
    tipo: [t2("form.fields.tipo"), filtros.tipo?.label],
    sede: [t2("form.fields.sede"), filtros.sede?.label],
    bloque: [t2("form.fields.bloque"), filtros.bloque?.label],
    espacio: [t2("form.fields.espacio"), filtros.espacio?.label],
    almacen: [t2("form.fields.almacen"), filtros.almacen?.label],
    estadoId: [t2("columns.estado"), estado],
    fechaInicio: [t2("filters.desde"), filtros.fechaInicio && fecha(filtros.fechaInicio)],
    fechaFin: [t2("filters.hasta"), filtros.fechaFin && fecha(filtros.fechaFin)],
  }).filter(([, [, valor]]) => valor);

  return (
    <Box p={2}>
      <SectionHeader titleKey="costos.costoIndirecto.title" />

      <MessageSnackBar message={message} setMessage={setMessage} />

      <GridActionBar
        onAdd={handleCreate}
        onUpdate={handleUpdate}
        onDelete={handleDeleteIntent}
        canUpdate={Boolean(selectedRow)}
        canDelete={selectedRow?.estadoId === 1}
        onFilters={() => setFiltersOpen(true)}
        onClearFilters={() => applyFilters(filtrosVacios)}
        hasActiveFilters={Object.keys(params).length > 0}
      />

      {chips.length > 0 && (
        <Stack direction="row" flexWrap="wrap" useFlexGap spacing={1} sx={{ mb: 2 }}>
          {chips.map(([campo, [etiqueta, valor]]) => (
            <Chip
              key={campo}
              size="small"
              label={`${etiqueta}: ${valor}`}
              onDelete={() => applyFilters(seleccionarNivel(filtros, campo, filtrosVacios[campo]))}
            />
          ))}
        </Stack>
      )}

      <FormCostoIndirecto
        open={formOpen}
        setOpen={setFormOpen}
        selectedRow={selectedRow}
        setMessage={setMessage}
        onCreated={() => {
          setRefreshKey((key) => key + 1);
          setSelectedRow(null);
        }}
      />

      <FiltroCostoIndirecto
        open={filtersOpen}
        filtros={filtros}
        onApply={applyFilters}
        onClose={() => setFiltersOpen(false)}
      />

      <AppDataGrid
        columns={columns}
        rows={rows}
        loading={loading}
        rowCount={rowCount}
        paginationModel={paginationModel}
        setPaginationModel={setPaginationModel}
        pageSizeOptions={[5, 10, 20, 50]}
        localeText={localeText}
        containerSx={{ borderRadius: 4 }}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
        onEscape={() => {
          setFormOpen(false);
          setConfirmOpen(false);
        }}
      />

      <Dialog open={confirmOpen} onClose={() => !deleting && setConfirmOpen(false)}>
        <DialogTitle>{t2("confirmDelete.title")}</DialogTitle>
        <DialogContent>{t2("confirmDelete.text")}</DialogContent>
        <DialogActions>
          <Button onClick={() => setConfirmOpen(false)} disabled={deleting}>
            {t("common.actions.cancel")}
          </Button>
          <Button color="error" variant="contained" onClick={confirmarEliminacion} disabled={deleting}>
            {t("common.actions.delete")}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
